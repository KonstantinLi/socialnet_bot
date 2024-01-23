package socialnet.bot.handler.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.HttpService;
import socialnet.bot.service.MessageService;
import socialnet.bot.service.TelegramService;

import static socialnet.bot.constant.Post.POST_RECOVER;

@Component
@RequiredArgsConstructor
public class PostRecoverHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final MessageService messageService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCallbackStartsWith(request.getUpdate(), POST_RECOVER.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();
        Long postId = messageService.getIdFromCallback(request, POST_RECOVER.getCommand());

        httpService.recoverPost(request, postId);
        telegramService.sendMessage(chatId, "<b>Пост восстановлен</b>");
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
