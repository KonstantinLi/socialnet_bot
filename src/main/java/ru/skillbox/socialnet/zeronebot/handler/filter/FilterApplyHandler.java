package ru.skillbox.socialnet.zeronebot.handler.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.enums.FilterState;
import ru.skillbox.socialnet.zeronebot.dto.enums.FriendsState;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.dto.session.FilterSession;
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

import static ru.skillbox.socialnet.zeronebot.constant.Callback.NEXT_PAGE_SEARCH;
import static ru.skillbox.socialnet.zeronebot.constant.Callback.PREV_PAGE_SEARCH;
import static ru.skillbox.socialnet.zeronebot.constant.Filter.APPLY;

@Component
@RequiredArgsConstructor
public class FilterApplyHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final PersonService personService;
    private final MessageService messageService;
    private final TelegramService telegramService;
    private final UserSessionService userSessionService;
    private final FriendsSessionService friendsSessionService;

    @Override
    public boolean isApplicable(UserRq request) {
        Update update = request.getUpdate();

        return isTextMessage(request.getUpdate(), APPLY) ||
                isCallback(update, PREV_PAGE_SEARCH) ||
                isCallback(update, NEXT_PAGE_SEARCH);
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Update update = request.getUpdate();
        FriendsSession friendsSession = request.getFriendsSession();

        if (hasNoFilter(request)) {
            telegramService.sendMessage(request.getChatId(), "Установите хотя бы один фильтр");
            return;
        }

        if (isTextMessage(update, APPLY)) {
            applyFilter(request);

        } else if (friendsSession.getFriendsState() == FriendsState.SEARCH) {
            personService.navigateButtons(request, PREV_PAGE_SEARCH, NEXT_PAGE_SEARCH);
        }

        List<PersonRs> persons = request.getFriendsSession().getFriends();

        if (persons == null ||
                persons.isEmpty() ||
                friendsSession.getFriendsState() != FriendsState.SEARCH) {
            return;
        }

        personService.sendPaginatedFriends(
                request,
                persons,
                PREV_PAGE_SEARCH,
                NEXT_PAGE_SEARCH);
    }

    private void applyFilter(UserRq request) throws IOException {
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

    private boolean hasNoFilter(UserRq request) {
        Update update = request.getUpdate();
        FilterSession filterSession = request.getFilterSession();

        return filterSession.getFilterState() != FilterState.FILTERED &&
                (isTextMessage(update, APPLY) ||
                        ((isCallback(update, PREV_PAGE_SEARCH) || isCallback(update, NEXT_PAGE_SEARCH)) &&
                                request.getFriendsSession().getFriendsState() == FriendsState.SEARCH));
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
