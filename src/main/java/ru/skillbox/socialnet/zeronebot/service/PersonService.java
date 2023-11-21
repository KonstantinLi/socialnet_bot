package ru.skillbox.socialnet.zeronebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.dto.session.FriendsSession;
import ru.skillbox.socialnet.zeronebot.dto.session.UserSession;
import ru.skillbox.socialnet.zeronebot.exception.OutOfListException;
import ru.skillbox.socialnet.zeronebot.service.session.FriendsSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.UserSessionService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

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

    public void sendPersonDetails(SessionRq sessionRq, PersonRs person) throws IOException {
        Long chatId = sessionRq.getChatId();
        String caption = formatService.caption(person, false);
        InlineKeyboardMarkup markupInLine = keyboardService.buildPersonMenu(person);

        try {
            telegramService.sendPhotoURL(chatId, new URL(person.getPhoto()), caption, markupInLine);
        } catch (Exception ex) {
            telegramService.sendMessage(chatId, caption, markupInLine);
        }
    }

    public void sendPersonDetailsNavigate(SessionRq sessionRq, PersonRs person) throws IOException {
        Long chatId = sessionRq.getChatId();
        String caption = formatService.caption(person, false);
        InlineKeyboardMarkup markupInLine = keyboardService.buildPersonMenuNavigate(person);

        try {
            telegramService.sendPhotoURL(chatId, new URL(person.getPhoto()), caption, markupInLine);
        } catch (Exception ex) {
            telegramService.sendMessage(chatId, caption, markupInLine);
        }
    }

    public void navigateButtons(SessionRq request, String prevCallback, String nextCallback) {
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

    public void navigatePerson(SessionRq request) {
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

        if (index >= recommendations.size()) {
            friendsSessionService.deleteSession(chatId);
            throw new OutOfListException();
        }

        friendsSessionService.saveSession(chatId, friendsSession);
    }

    public boolean isOverPage(int page, int length) {
        return page > length / pageSize;
    }
}
