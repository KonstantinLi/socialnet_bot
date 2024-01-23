package socialnet.bot.handler.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.HttpService;
import socialnet.bot.service.MessageService;
import socialnet.bot.service.TelegramService;

import static socialnet.bot.constant.Person.BLOCK;
import static socialnet.bot.constant.Person.UNBLOCK;

@Component
@RequiredArgsConstructor
public class BlockUserHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final MessageService messageService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCallbackStartsWith(request.getUpdate(), BLOCK.getCommand()) ||
                isCallbackStartsWith(request.getUpdate(), UNBLOCK.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        boolean blocking = isCallbackStartsWith(request.getUpdate(), BLOCK.getCommand());

        Long id = messageService.getIdFromCallback(
                request,
                blocking ? BLOCK.getCommand() : UNBLOCK.getCommand());
        httpService.blockUser(request, id);

        telegramService.sendMessage(
                request.getChatId(),
                String.format("Вы <b>%s</b> пользователя", blocking ? "заблокировали" : "разблокировали"));
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
