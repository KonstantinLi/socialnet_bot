package ru.skillbox.socialnet.zeronebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.FilterState;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.FriendsState;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.dto.session.FilterSession;
import ru.skillbox.socialnet.zeronebot.dto.session.FriendsSession;
import ru.skillbox.socialnet.zeronebot.dto.session.UserSession;
import ru.skillbox.socialnet.zeronebot.service.session.FriendsSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.UserSessionService;

import java.io.IOException;
import java.util.List;

import static ru.skillbox.socialnet.zeronebot.constant.Filter.APPLY;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.NEXT_PAGE_SEARCH;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.PREV_PAGE_SEARCH;

@Service
@RequiredArgsConstructor
public class FilterService {
    private final HttpService httpService;
    private final MessageService messageService;
    private final TelegramService telegramService;
    private final UserSessionService userSessionService;
    private final FriendsSessionService friendsSessionService;

    public void setFilterProperty(
            FilterSession filterSession,
            FilterState filterState,
            String property) {

        switch (filterState) {
            case AGE_FROM_WAIT -> {
                Integer ageFrom = Integer.valueOf(property);
                filterSession.setAgeFrom(ageFrom);
            }
            case AGE_TO_WAIT -> {
                Integer ageTo = Integer.valueOf(property);
                filterSession.setAgeTo(ageTo);
            }
            case CITY_WAIT -> filterSession.setCity(property);
            case COUNTRY_WAIT -> filterSession.setCountry(property);
            case FIRST_NAME_WAIT -> filterSession.setFirstName(property);
            case LAST_NAME_WAIT -> filterSession.setLastName(property);
            default -> {}
        }
    }

    public void applyFilter(SessionRq request) throws IOException {
        Long chatId = request.getChatId();

        UserSession userSession = request.getUserSession();
        FriendsSession friendsSession = request.getFriendsSession();

        List<PersonRs> persons = httpService.search(request);

        friendsSession.setFriends(persons);
        friendsSession.setFriendsState(FriendsState.SEARCH);
        friendsSessionService.saveSession(chatId, friendsSession);

        userSession.setPage(0);
        userSessionService.saveSession(chatId, userSession);

        telegramService.sendMessage(chatId, messageService.search(persons.size()));
    }

    public boolean hasNoFilter(SessionRq request) {
        Update update = request.getUpdate();
        Message message = update.getMessage();
        String callbackData = update.hasCallbackQuery() ? update.getCallbackQuery().getData() : null;
        FilterSession filterSession = request.getFilterSession();

        return filterSession.getFilterState() != FilterState.FILTERED &&
                (message != null && message.hasText() && message.getText().equals(APPLY) ||
                        (callbackData != null && (callbackData.equals(PREV_PAGE_SEARCH) ||
                                callbackData.equals(NEXT_PAGE_SEARCH)) &&
                                request.getFriendsSession().getFriendsState() == FriendsState.SEARCH));
    }
}
