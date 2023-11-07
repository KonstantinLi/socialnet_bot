package ru.skillbox.socialnet.zeronebot.handler.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.PersonService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static ru.skillbox.socialnet.zeronebot.constant.Common.NEXT_PAGE;
import static ru.skillbox.socialnet.zeronebot.constant.Common.PREV_PAGE;
import static ru.skillbox.socialnet.zeronebot.constant.Profile.FRIENDS;

@Component
@RequiredArgsConstructor
public class FriendsListHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final PersonService personService;

    @Override
    public boolean isApplicable(UserRq request) {
        Update update  = request.getUpdate();

        return isTextMessage(update, FRIENDS) ||
                isCallback(update, PREV_PAGE) ||
                isCallback(update, NEXT_PAGE);
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Update update = request.getUpdate();
        int page = Optional.ofNullable(request.getUserSession().getPage()).orElse(0);

        if (isTextMessage(update, FRIENDS)) {
            page = 0;
        } else if (isCallback(update, PREV_PAGE)) {
            request.getUserSession().setPage(Math.max(--page, 0));
        } else if (isCallback(update, NEXT_PAGE)) {
            request.getUserSession().setPage(++page);
        }

        List<PersonRs> friends = httpService.friends(request);
        personService.sendPaginatedFriends(request, friends, page);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
