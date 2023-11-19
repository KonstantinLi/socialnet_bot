package ru.skillbox.socialnet.zeronebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.dto.session.FriendsSession;
import ru.skillbox.socialnet.zeronebot.dto.session.UserSession;
import ru.skillbox.socialnet.zeronebot.exception.OutOfListException;
import ru.skillbox.socialnet.zeronebot.service.session.FriendsSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.UserSessionService;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.skillbox.socialnet.zeronebot.constant.Friends.PERSON_INFO;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.NEXT_PERSON;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.PREV_PERSON;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final FormatService formatService;
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

    public void sendPersonDetails(UserRq userRq, PersonRs person) throws IOException {
        InlineKeyboardMarkup markupInLine = keyboardService.buildPersonMenu(person);
        telegramService.sendPhotoURL(userRq.getChatId(),
                new URL(person.getPhoto()),
                formatService.caption(person, false),
                markupInLine);
    }

    public void sendPersonDetailsNavigate(UserRq userRq, PersonRs person) throws IOException {
        InlineKeyboardMarkup markupInLine = keyboardService.buildPersonMenuNavigate(person);
        telegramService.sendPhotoURL(userRq.getChatId(),
                new URL(person.getPhoto()),
                formatService.caption(person, false),
                markupInLine);
    }

    public void navigateButtons(UserRq request, String prevCallback, String nextCallback) {
        Long chatId = request.getChatId();
        Update update = request.getUpdate();

        UserSession userSession = request.getUserSession();
        FriendsSession friendsSession = request.getFriendsSession();

        List<PersonRs> friends = friendsSession.getFriends();
        int page = Optional.ofNullable(userSession.getPage()).orElse(0);

        if (update.getCallbackQuery().getData().equals(prevCallback)) {
            page = Math.max(--page, 0);

        } else if (update.getCallbackQuery().getData().equals(nextCallback) &&
                isOverPage(++page, friends.size())) {

            userSession.setPage(0);
            friendsSessionService.deleteSession(chatId);
            userSessionService.saveSession(chatId, userSession);

            throw new OutOfListException();
        }

        userSession.setPage(page);
        userSessionService.saveSession(chatId, userSession);
    }

    public void navigatePerson(UserRq request) {
        Long chatId = request.getChatId();
        Update update = request.getUpdate();

        FriendsSession friendsSession = request.getFriendsSession();

        List<PersonRs> recommendations = friendsSession.getFriends();
        int index = Optional.ofNullable(friendsSession.getIndex()).orElse(0);

        if (update.getCallbackQuery().getData().equals(PREV_PERSON)) {
            friendsSession.setIndex(Math.max(--index, 0));

        } else if (update.getCallbackQuery().getData().equals(NEXT_PERSON)) {
            friendsSession.setIndex(Math.min(recommendations.size() - 1, ++index));
        }

        if (index == recommendations.size() - 1) {
            friendsSessionService.deleteSession(chatId);
            throw new OutOfListException();
        }

        friendsSessionService.saveSession(chatId, friendsSession);
    }

    public boolean isOverPage(int page, int length) {
        return page > length / pageSize;
    }
}
