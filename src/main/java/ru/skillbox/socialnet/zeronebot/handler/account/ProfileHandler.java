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
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import static ru.skillbox.socialnet.zeronebot.constant.Profile.PROFILE;

@Component
@RequiredArgsConstructor
public class ProfileHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final PersonService personService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isTextMessage(request.getUpdate(), PROFILE);
    }

    @Override
    public void handle(UserRq request) throws IOException {
        PersonRs personRs = httpService.profile(request);
        String caption = personService.caption(personRs);

        telegramService.sendPhotoURL(request.getChatId(),
                new URL(personRs.getPhoto()),
                caption);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
