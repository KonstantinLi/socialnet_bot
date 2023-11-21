package ru.skillbox.socialnet.zeronebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.FriendsState;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.dto.session.FriendsSession;
import ru.skillbox.socialnet.zeronebot.dto.session.UserSession;
import ru.skillbox.socialnet.zeronebot.service.session.FriendsSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.UserSessionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.skillbox.socialnet.zeronebot.constant.Friends.FRIENDS_INCOMING;
import static ru.skillbox.socialnet.zeronebot.constant.Friends.PERSON_INFO;

@Service
@RequiredArgsConstructor
public class FriendsService {
    private final HttpService httpService;
    private final FormatService formatService;
    private final MessageService messageService;
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final UserSessionService userSessionService;
    private final FriendsSessionService friendsSessionService;

    @Value("${zerone.page_size}")
    private Integer pageSize;

    public void sendPaginatedFriends(
            UserRq userRq,
            List<PersonRs> personList,
            String prevPage,
            String nextPage) {

        int page = Optional.ofNullable(userRq.getUserSession().getPage()).orElse(0);

        int start = Math.min(page * pageSize, personList.size());
        int end = Math.min(start + pageSize, personList.size());

        List<PersonRs> personsPage = personList.subList(start, end);

        if (personsPage.isEmpty()) {
            telegramService.sendMessage(userRq.getChatId(), "Список завершен");
            return;
        }

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        for (PersonRs person : personsPage) {
            InlineKeyboardButton button =
                    new InlineKeyboardButton(formatService.caption(person, true));
            button.setCallbackData(PERSON_INFO + "_" + person.getId());
            rowsInLine.add(List.of(button));
        }

        InlineKeyboardMarkup navigate = keyboardService.buildNavigateMenu(prevPage, nextPage);
        rowsInLine.addAll(navigate.getKeyboard());

        markupInLine.setKeyboard(rowsInLine);

        telegramService.sendMessage(
                userRq.getChatId(),
                String.format("(%d-%d)", start + 1, end),
                markupInLine);
    }

    public void friendsList(UserRq request) throws IOException {
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

    public void friendsRecommend(UserRq request) throws IOException {
        Long chatId = request.getChatId();
        FriendsSession friendsSession = request.getFriendsSession();

        List<PersonRs> recommendations = httpService.recommendations(request);

        friendsSession.setIndex(0);
        friendsSession.setFriends(recommendations);
        friendsSession.setFriendsState(FriendsState.RECOMMENDS);
        friendsSessionService.saveSession(chatId, friendsSession);

        telegramService.sendMessage(chatId, messageService.recommend(recommendations.size()));
    }

    public void friendsRequest(UserRq request) throws IOException {
        Long chatId = request.getChatId();
        Update update = request.getUpdate();
        Message message = update.getMessage();

        UserSession userSession = request.getUserSession();
        FriendsSession friendsSession = request.getFriendsSession();

        String formatRequest;
        List<PersonRs> friendsRequest;

        if (message != null && message.hasText() && message.getText().equals(FRIENDS_INCOMING)) {
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
}
