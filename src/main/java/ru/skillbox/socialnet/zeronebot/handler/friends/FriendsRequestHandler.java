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

import static ru.skillbox.socialnet.zeronebot.constant.Callback.NEXT_PAGE_REQUEST;
import static ru.skillbox.socialnet.zeronebot.constant.Callback.PREV_PAGE_REQUEST;
import static ru.skillbox.socialnet.zeronebot.constant.Friends.FRIENDS_INCOMING;
import static ru.skillbox.socialnet.zeronebot.constant.Friends.FRIENDS_OUTGOING;

@Component
@RequiredArgsConstructor
public class FriendsRequestHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final PersonService personService;
    private final MessageService messageService;
    private final TelegramService telegramService;
    private final UserSessionService userSessionService;
    private final FriendsSessionService friendsSessionService;

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
            friendsRequest(request);

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

    private void friendsRequest(UserRq request) throws IOException {
        Long chatId = request.getChatId();
        Update update = request.getUpdate();

        UserSession userSession = request.getUserSession();
        FriendsSession friendsSession = request.getFriendsSession();

        String formatRequest;
        List<PersonRs> friendsRequest;

        if (isTextMessage(update, FRIENDS_INCOMING)) {
            friendsRequest = httpService.incoming(request);
            formatRequest = messageService.incoming(friendsRequest.size());
        } else {
            friendsRequest = httpService.outgoing(request);
            formatRequest = messageService.outgoing(friendsRequest.size());
        }

        friendsSession.setFriends(friendsRequest);
        friendsSession.setFriendsState(FriendsState.REQUEST);
        friendsSessionService.saveSession(chatId, friendsSession);

        userSession.setPage(0);
        userSessionService.saveSession(chatId, userSession);

        telegramService.sendMessage(chatId, formatRequest);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
