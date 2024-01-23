package socialnet.bot.handler.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import socialnet.bot.dto.enums.state.PostState;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.response.PostRs;
import socialnet.bot.dto.session.PostSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.MessageService;
import socialnet.bot.service.PostService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.CommentSessionService;
import socialnet.bot.service.session.PostSessionService;

import java.util.List;
import java.util.Optional;

import static socialnet.bot.constant.Navigate.NEXT_USER_POST;
import static socialnet.bot.constant.Navigate.PREV_USER_POST;
import static socialnet.bot.constant.Post.WALL;

@Component
@RequiredArgsConstructor
public class WallHandler extends UserRequestHandler {
    private final PostService postService;
    private final MessageService messageService;
    private final TelegramService telegramService;
    private final PostSessionService postSessionService;
    private final CommentSessionService commentSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        Update update = request.getUpdate();

        return isCallbackStartsWith(update, WALL.getCommand()) ||
                isCallbackStartsWith(update, PREV_USER_POST) ||
                isCallbackStartsWith(update, NEXT_USER_POST);
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();
        Update update = request.getUpdate();
        PostSession postSession = request.getPostSession();

        String command = isCallbackStartsWith(update, WALL.getCommand()) ?
                WALL.getCommand() :
                (isCallbackStartsWith(update, PREV_USER_POST) ?
                        PREV_USER_POST :
                        NEXT_USER_POST);

        Long authorId = messageService.getIdFromCallback(request, command);

        if (command.equals(WALL.getCommand())) {
            postService.wall(request, authorId);
            postSession.setPostState(PostState.WALL);
            postSession.setAuthorId(authorId);
            postSessionService.saveSession(chatId, postSession);
            commentSessionService.deleteSession(chatId);
        } else {
            postService.navigatePost(request, authorId);
        }

        List<PostRs> posts = postSession.getPosts();

        if (posts == null || posts.isEmpty()) {
            if (command.equals(WALL.getCommand())) {
                telegramService.sendMessage(chatId, "Публикации отсутствуют");
            }
            return;
        }

        int index = Optional.ofNullable(postSession.getIndex()).orElse(0);
        PostRs post = posts.get(index);

        postService.sendPostDetailsNavigate(request, post, true);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
