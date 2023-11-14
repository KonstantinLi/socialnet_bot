package ru.skillbox.socialnet.zeronebot.handler.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.PersonService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;

import java.io.IOException;
import java.net.URL;

import static ru.skillbox.socialnet.zeronebot.dto.enums.Menu.PROFILE;

@Component
@RequiredArgsConstructor
public class ProfileHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final PersonService personService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isCommand(request.getUpdate(), PROFILE.getCommand());
    }

    @Override
    public void handle(UserRq request) throws IOException {
        PersonRs personRs = httpService.profile(request);
        String caption = personService.caption(personRs, true);

        telegramService.sendPhotoURL(request.getChatId(),
                new URL(personRs.getPhoto()),
                caption);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
