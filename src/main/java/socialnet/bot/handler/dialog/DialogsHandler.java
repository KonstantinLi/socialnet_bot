package socialnet.bot.handler.dialog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.response.DialogRs;
import socialnet.bot.dto.response.PersonRs;
import socialnet.bot.dto.session.DialogSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.DialogService;
import socialnet.bot.service.HttpService;

import java.util.List;
import java.util.Optional;

import static socialnet.bot.constant.Menu.DIALOGS;
import static socialnet.bot.constant.Navigate.NEXT_DIALOG;
import static socialnet.bot.constant.Navigate.PREV_DIALOG;

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
    public void handle(SessionRq request) throws Exception {
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
