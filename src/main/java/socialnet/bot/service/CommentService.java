package socialnet.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.response.CommentRs;
import socialnet.bot.dto.session.CommentSession;
import socialnet.bot.exception.OutOfListException;
import socialnet.bot.service.session.CommentSessionService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static socialnet.bot.constant.Comment.COMMENT;
import static socialnet.bot.constant.Comment.COMMENT_COMMENT;
import static socialnet.bot.constant.Like.LIKE_COMMENT;
import static socialnet.bot.constant.Like.UNLIKE_COMMENT;
import static socialnet.bot.constant.Navigate.*;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final FormatService formatService;
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final CommentSessionService commentSessionService;

    public void navigateComment(SessionRq sessionRq, int commentsCount) {
        Long chatId = sessionRq.getChatId();
        Update update = sessionRq.getUpdate();
        String callbackData = update.getCallbackQuery().getData();

        CommentSession commentSession = sessionRq.getCommentSession();
        int index = Optional.ofNullable(commentSession.getIndex()).orElse(0);

        if (callbackData.equals(COMMENT.getCommand())) {
            commentSession.setIndex(0);
        } else if (callbackData.equals(PREV_COMMENT)) {
            commentSession.setIndex(Math.max(--index, 0));
        } else if (callbackData.startsWith(LIKE_COMMENT.getCommand()) ||
                callbackData.startsWith(UNLIKE_COMMENT.getCommand()) ||
                callbackData.equals(NEXT_COMMENT)) {
            index++;
            if (index >= commentsCount) {
                throw new OutOfListException();
            }
            commentSession.setIndex(index);
        }

        commentSessionService.saveSession(chatId, commentSession);
    }

    public void navigateCommentComment(SessionRq sessionRq, int commentsCount) {
        Long chatId = sessionRq.getChatId();
        Update update = sessionRq.getUpdate();
        String callbackData = update.getCallbackQuery().getData();

        CommentSession commentSession = sessionRq.getCommentSession();
        int index = Optional.ofNullable(commentSession.getSubIndex()).orElse(0);

        if (callbackData.equals(COMMENT_COMMENT.getCommand())) {
            commentSession.setSubIndex(0);
        } else if (callbackData.equals(PREV_COMMENT_COMMENT)) {
            commentSession.setSubIndex(Math.max(--index, 0));
        } else if (callbackData.equals(NEXT_COMMENT_COMMENT)) {
            index++;
            if (index >= commentsCount) {
                throw new OutOfListException();
            }
            commentSession.setSubIndex(index);
        }

        commentSessionService.saveSession(chatId, commentSession);
    }

    public void sendCommentDetailsNavigate(SessionRq sessionRq, CommentRs commentRs) {
        Long id = sessionRq.getUserSession().getId();
        InlineKeyboardMarkup markupInLine = keyboardService.buildCommentMenuNavigate(commentRs, id);
        telegramService.sendMessage(sessionRq.getChatId(),
                formatService.formatComment(commentRs),
                markupInLine);
    }

    public void sendCommentCommentDetailsNavigate(SessionRq sessionRq, CommentRs commentRs) {
        Long id = sessionRq.getUserSession().getId();
        InlineKeyboardMarkup markupInLine = keyboardService.buildCommentCommentMenuNavigate(commentRs, id);
        telegramService.sendMessage(sessionRq.getChatId(),
                formatService.formatComment(commentRs),
                markupInLine);
    }

    public CommentRs getCommentById(List<CommentRs> comments, Long id) {
        return comments.stream()
                .filter(parent -> parent.getId().equals(id))
                .findFirst()
                .orElseGet(() -> {
                    CommentRs commentRs = new CommentRs();
                    commentRs.setSubComments(new ArrayList<>());
                    return commentRs;
                });
    }

    public Comparator<CommentRs> commentComparator(Long authorId) {
        return (comment1, comment2) -> {
            Long author1 = comment1.getAuthor().getId();
            Long author2 = comment2.getAuthor().getId();

            if (!author1.equals(author2)) {
                if (author1.equals(authorId)) {
                    return -1;
                } else if (author2.equals(authorId)) {
                    return 1;
                } else {
                    return comment2.getTime().compareTo(comment1.getTime());
                }
            } else {
                return 0;
            }
        };
    }
}
