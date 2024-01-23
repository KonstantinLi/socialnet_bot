package socialnet.bot.handler.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import socialnet.bot.dto.enums.state.PostState;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.PostSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.PostSessionService;

import static socialnet.bot.constant.Post.POST_ADD;

@Component
@RequiredArgsConstructor
public class PostCreateHandler extends UserRequestHandler {
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final PostSessionService postSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCallback(request.getUpdate(), POST_ADD.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();

        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildMenuWithCancel();
        telegramService.sendMessage(
                chatId,
                "Введите заголовок:",
                replyKeyboardMarkup);

        PostSession postSession = request.getPostSession();
        postSession.setPostState(PostState.TITLE_WAIT);
        postSessionService.saveSession(chatId, postSession);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
