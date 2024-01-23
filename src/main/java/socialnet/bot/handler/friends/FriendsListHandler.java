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

import static socialnet.bot.constant.Friends.FRIENDS_LIST;
import static socialnet.bot.constant.Navigate.NEXT_PAGE_FRIENDS;
import static socialnet.bot.constant.Navigate.PREV_PAGE_FRIENDS;

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
