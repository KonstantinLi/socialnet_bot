package ru.skillbox.socialnet.zeronebot.handler.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.PersonService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Friends.PERSON_INFO;

@Component
@RequiredArgsConstructor
public class FriendDetailsHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final PersonService personService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isCallbackStartsWith(request.getUpdate(), PERSON_INFO);
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Long id = Long.valueOf(request.getUpdate()
                .getCallbackQuery()
                .getData()
                .replace(PERSON_INFO + "_", ""));

        PersonRs personRs = httpService.getPersonById(request, id);
        personService.sendPersonDetails(request, personRs);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
