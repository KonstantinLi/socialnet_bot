package ru.skillbox.socialnet.zeronebot.handler.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.enums.FriendsState;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.dto.session.FriendsSession;
import ru.skillbox.socialnet.zeronebot.dto.session.UserSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.MessageService;
import ru.skillbox.socialnet.zeronebot.service.PersonService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.FriendsSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.UserSessionService;

import java.io.IOException;
import java.util.List;

import static ru.skillbox.socialnet.zeronebot.constant.Callback.NEXT_PAGE_FRIENDS;
import static ru.skillbox.socialnet.zeronebot.constant.Callback.PREV_PAGE_FRIENDS;
import static ru.skillbox.socialnet.zeronebot.constant.Friends.FRIENDS_LIST;

@Component
@RequiredArgsConstructor
public class FriendsListHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final PersonService personService;
    private final MessageService messageService;
    private final TelegramService telegramService;
    private final UserSessionService userSessionService;
    private final FriendsSessionService friendsSessionService;

    @Override
    public boolean isApplicable(UserRq request) {
        Update update  = request.getUpdate();

        return isTextMessage(update, FRIENDS_LIST) ||
                isCallback(update, PREV_PAGE_FRIENDS) ||
                isCallback(update, NEXT_PAGE_FRIENDS);
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Update update = request.getUpdate();
        FriendsSession friendsSession = request.getFriendsSession();

        if (isTextMessage(update, FRIENDS_LIST)) {
            friendsList(request);

        } else if (friendsSession.getFriendsState() == FriendsState.FRIENDS) {
            personService.navigateButtons(request, PREV_PAGE_FRIENDS, NEXT_PAGE_FRIENDS);
        }

        List<PersonRs> friends = friendsSession.getFriends();

        if (friends == null ||
                friends.isEmpty() ||
                friendsSession.getFriendsState() != FriendsState.FRIENDS) {
            return;
        }

        personService.sendPaginatedFriends(
                request,
                friends,
                PREV_PAGE_FRIENDS,
                NEXT_PAGE_FRIENDS);
    }

    private void friendsList(UserRq request) throws IOException {
        Long chatId = request.getChatId();

        UserSession userSession = request.getUserSession();
        FriendsSession friendsSession = request.getFriendsSession();

        List<PersonRs> friends = httpService.friends(request);

        friendsSession.setFriends(friends);
        friendsSession.setFriendsState(FriendsState.FRIENDS);
        friendsSessionService.saveSession(chatId, friendsSession);

        userSession.setPage(0);
        userSessionService.saveSession(chatId, userSession);

        telegramService.sendMessage(chatId, messageService.friends(friends.size()));
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
