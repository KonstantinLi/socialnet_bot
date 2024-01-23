package socialnet.bot.handler.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import socialnet.bot.dto.enums.state.FriendsState;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.response.PersonRs;
import socialnet.bot.dto.session.FriendsSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.FriendsService;
import socialnet.bot.service.PersonService;

import java.util.List;

import static socialnet.bot.constant.Friends.FRIENDS_INCOMING;
import static socialnet.bot.constant.Friends.FRIENDS_OUTGOING;
import static socialnet.bot.constant.Navigate.NEXT_PAGE_REQUEST;
import static socialnet.bot.constant.Navigate.PREV_PAGE_REQUEST;

@Component
@RequiredArgsConstructor
public class FriendsRequestHandler extends UserRequestHandler {
    private final PersonService personService;
    private final FriendsService friendsService;

    @Override
    public boolean isApplicable(SessionRq request) {
        Update update  = request.getUpdate();

        return isTextMessage(update, FRIENDS_INCOMING) ||
                isTextMessage(update, FRIENDS_OUTGOING) ||
                isCallback(update, PREV_PAGE_REQUEST) ||
                isCallback(update, NEXT_PAGE_REQUEST);
    }

    @Override
    public void handle(SessionRq request) throws Exception {
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

        friendsService.sendPaginatedFriends(
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
