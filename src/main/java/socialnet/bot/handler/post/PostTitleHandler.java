package socialnet.bot.handler.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import socialnet.bot.dto.enums.state.PostState;
import socialnet.bot.dto.request.PostRq;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.PostSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.PostSessionService;

@Component
@RequiredArgsConstructor
public class PostTitleHandler extends UserRequestHandler {
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final PostSessionService postSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        PostState postState = request.getPostSession().getPostState();
        return isTextMessage(request.getUpdate()) && postState == PostState.TITLE_WAIT;
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();
        PostSession postSession = request.getPostSession();

        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildMenuWithCancel();
        telegramService.sendMessage(
                chatId,
                "Введите текст поста:",
                replyKeyboardMarkup);

        String title = request.getUpdate().getMessage().getText();

        PostRq postRq = new PostRq();
        postRq.setTitle(title);

        postSession.setPublish(postRq);
        postSession.setPostState(PostState.TEXT_WAIT);
        postSessionService.saveSession(chatId, postSession);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
