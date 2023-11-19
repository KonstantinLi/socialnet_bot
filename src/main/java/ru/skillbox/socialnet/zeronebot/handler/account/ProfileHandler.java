package ru.skillbox.socialnet.zeronebot.handler.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.FormatService;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;

import java.io.IOException;
import java.net.URL;

import static ru.skillbox.socialnet.zeronebot.constant.Menu.PROFILE;

@Component
@RequiredArgsConstructor
public class ProfileHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final FormatService formatService;
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isCommand(request.getUpdate(), PROFILE.getCommand());
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Long chatId = request.getChatId();

        PersonRs personRs = httpService.profile(request);
        String caption = formatService.caption(personRs, false);

        InlineKeyboardMarkup markupInLine = keyboardService.buildProfileMenu(request);

        telegramService.sendMessage(chatId, "Вкладка <b>\"Профиль\"</b>");

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
