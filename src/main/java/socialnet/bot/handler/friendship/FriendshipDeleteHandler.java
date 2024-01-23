package socialnet.bot.handler.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.HttpService;
import socialnet.bot.service.MessageService;
import socialnet.bot.service.TelegramService;

import static socialnet.bot.constant.Person.DELETE;

@Component
@RequiredArgsConstructor
public class FriendshipDeleteHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final MessageService messageService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCallbackStartsWith(request.getUpdate(), DELETE.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long id = messageService.getIdFromCallback(request, DELETE.getCommand());
        httpService.deleteFriend(request, id);

        telegramService.sendMessage(
                request.getChatId(),
                "Вы <b>удалили</b> пользователя из друзей");
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
