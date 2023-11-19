package ru.skillbox.socialnet.zeronebot.handler.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.FriendsState;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.dto.session.FriendsSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.FriendsService;
import ru.skillbox.socialnet.zeronebot.service.PersonService;

import java.io.IOException;
import java.util.List;

import static ru.skillbox.socialnet.zeronebot.constant.Friends.FRIENDS_INCOMING;
import static ru.skillbox.socialnet.zeronebot.constant.Friends.FRIENDS_OUTGOING;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.NEXT_PAGE_REQUEST;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.PREV_PAGE_REQUEST;

@Component
@RequiredArgsConstructor
public class FriendsRequestHandler extends UserRequestHandler {
    private final FriendsService friendsService;
    private final PersonService personService;

    @Override
    public boolean isApplicable(UserRq request) {
        Update update  = request.getUpdate();

        return isTextMessage(update, FRIENDS_INCOMING) ||
                isTextMessage(update, FRIENDS_OUTGOING) ||
                isCallback(update, PREV_PAGE_REQUEST) ||
                isCallback(update, NEXT_PAGE_REQUEST);
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Update update = request.getUpdate();
        FriendsSession friendsSession = request.getFriendsSession();

        if (isTextMessage(update)) {
            friendsService.friendsRequest(request);

        } else if (friendsSession.getFriendsState() == FriendsState.REQUEST) {
            personService.navigateButtons(request, PREV_PAGE_REQUEST, NEXT_PAGE_REQUEST);
        }

        List<PersonRs> friendsRequest = friendsSession.getFriends();

        if (friendsRequest == null ||
                friendsRequest.isEmpty() ||
                friendsSession.getFriendsState() != FriendsState.REQUEST) {
            return;
        }

        personService.sendPaginatedFriends(
                request,
                friendsRequest,
                PREV_PAGE_REQUEST,
                NEXT_PAGE_REQUEST);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
