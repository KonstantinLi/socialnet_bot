package ru.skillbox.socialnet.zeronebot.handler.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.MessageService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Comment.COMMENT_RECOVER;

@Component
@RequiredArgsConstructor
public class CommentRecoverHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final MessageService messageService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCallbackStartsWith(request.getUpdate(), COMMENT_RECOVER.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws IOException {
        Long chatId = request.getChatId();
        Long postId = request.getCommentSession().getPostId();

        if (postId == null) {
            return;
        }

        Long commentId = messageService.getIdFromCallback(request, COMMENT_RECOVER.getCommand());
        httpService.recoverComment(request, postId, commentId);

        telegramService.sendMessage(chatId, "<b>Комментарий восстановлен</b>");
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
