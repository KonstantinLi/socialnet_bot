package ru.skillbox.socialnet.zeronebot.handler.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.FriendsState;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.dto.session.FriendsSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.FriendsService;
import ru.skillbox.socialnet.zeronebot.service.PersonService;

import java.io.IOException;
import java.util.List;

import static ru.skillbox.socialnet.zeronebot.constant.Friends.FRIENDS_LIST;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.NEXT_PAGE_FRIENDS;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.PREV_PAGE_FRIENDS;

@Component
@RequiredArgsConstructor
public class FriendsListHandler extends UserRequestHandler {
    private final FriendsService friendsService;
    private final PersonService personService;

    @Override
    public boolean isApplicable(SessionRq request) {
        Update update  = request.getUpdate();

        return isTextMessage(update, FRIENDS_LIST) ||
                isCallback(update, PREV_PAGE_FRIENDS) ||
                isCallback(update, NEXT_PAGE_FRIENDS);
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Update update = request.getUpdate();
        FriendsSession friendsSession = request.getFriendsSession();

        if (isTextMessage(update, FRIENDS_LIST)) {
            friendsService.friendsList(request);

        } else if (friendsSession.getFriendsState() == FriendsState.FRIENDS) {
            personService.navigateButtons(request, PREV_PAGE_FRIENDS, NEXT_PAGE_FRIENDS);
        }

        List<PersonRs> friends = friendsSession.getFriends();

        if (friends == null ||
                friends.isEmpty() ||
                friendsSession.getFriendsState() != FriendsState.FRIENDS) {
            return;
        }

        friendsService.sendPaginatedFriends(
                request,
                friends,
                PREV_PAGE_FRIENDS,
                NEXT_PAGE_FRIENDS);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
