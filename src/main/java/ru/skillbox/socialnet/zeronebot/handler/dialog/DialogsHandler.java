package ru.skillbox.socialnet.zeronebot.handler.dialog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.response.DialogRs;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.dto.session.DialogSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.DialogService;
import ru.skillbox.socialnet.zeronebot.service.HttpService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static ru.skillbox.socialnet.zeronebot.constant.Menu.DIALOGS;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.NEXT_DIALOG;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.PREV_DIALOG;

@Component
@RequiredArgsConstructor
public class DialogsHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final DialogService dialogService;

    @Override
    public boolean isApplicable(SessionRq request) {
        Update update  = request.getUpdate();

        return isCommand(update, DIALOGS.getCommand()) ||
                isCallback(update, PREV_DIALOG) ||
                isCallback(update, NEXT_DIALOG);
    }

    @Override
    public void handle(SessionRq request) throws IOException {
        Long id = request.getUserSession().getId();
        Update update = request.getUpdate();
        DialogSession dialogSession = request.getDialogSession();

        if (isCommand(update, DIALOGS.getCommand())) {
            dialogService.dialogs(request);

        } else if (dialogSession.getDialogs() != null) {
            dialogService.navigateDialog(request);
        }

        List<DialogRs> dialogs = dialogSession.getDialogs();

        if (dialogs == null || dialogs.isEmpty()) {
            return;
        }

        int index = Optional.ofNullable(dialogSession.getIndex()).orElse(0);
        DialogRs dialog = dialogs.get(index);

        Long companionId = id.equals(dialog.getAuthorId()) ?
                dialog.getRecipientId() :
                dialog.getAuthorId();
        PersonRs companion = httpService.getPersonById(request, companionId);

        dialogService.sendDialogDetailsNavigate(request.getChatId(), dialog, companion);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
