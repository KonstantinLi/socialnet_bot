package socialnet.bot.handler.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.response.PersonRs;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.FormatService;
import socialnet.bot.service.HttpService;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;

import java.net.URL;

import static socialnet.bot.constant.Menu.PROFILE;

@Component
@RequiredArgsConstructor
public class ProfileHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final FormatService formatService;
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCommand(request.getUpdate(), PROFILE.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();

        PersonRs personRs = httpService.profile(request);
        String caption = formatService.caption(personRs, false);

        InlineKeyboardMarkup markupInLine = keyboardService.buildProfileMenu(request);
        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
        keyboardRemove.setRemoveKeyboard(true);

        telegramService.sendMessage(
                chatId,
                "Вкладка <b>\"Профиль\"</b>",
                keyboardRemove);

        telegramService.sendPhotoURL(
                chatId,
                new URL(personRs.getPhoto()),
                caption,
                markupInLine);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
