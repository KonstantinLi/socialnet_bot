package socialnet.bot.handler.dialog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.HttpService;
import socialnet.bot.service.MessageService;
import socialnet.bot.service.TelegramService;

import static socialnet.bot.constant.Dialog.READ;

@Component
@RequiredArgsConstructor
public class DialogReadHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final MessageService messageService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCallbackStartsWith(request.getUpdate(), READ.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long id = messageService.getIdFromCallback(request, READ.getCommand());
        httpService.readDialog(request, id);

        telegramService.sendMessage(
                request.getChatId(),
                "Вы отметили диалог как <b>прочитанный</b>");
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
