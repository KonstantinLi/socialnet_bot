package socialnet.bot.handler.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import socialnet.bot.constant.Account;
import socialnet.bot.dto.request.EditRq;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.HttpService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.EditSessionService;

@Component
@RequiredArgsConstructor
public class ProfileEditApplyHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final TelegramService telegramService;
    private final EditSessionService editSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isTextMessage(request.getUpdate(), Account.SAVE_CHANGES) &&
                request.getEditSession().getEditRq() != null;
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();
        EditRq editRq = request.getEditSession().getEditRq();

        httpService.editProfile(request, editRq);

        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
        keyboardRemove.setRemoveKeyboard(true);
        telegramService.sendMessage(
                chatId,
                "Информация обновлена",
                keyboardRemove);

        editSessionService.deleteSession(chatId);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
