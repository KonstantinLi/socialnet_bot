package ru.skillbox.socialnet.zeronebot.handler.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.FriendsState;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.dto.session.FriendsSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.FilterService;
import ru.skillbox.socialnet.zeronebot.service.FriendsService;
import ru.skillbox.socialnet.zeronebot.service.PersonService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;

import java.io.IOException;
import java.util.List;

import static ru.skillbox.socialnet.zeronebot.constant.Filter.APPLY;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.NEXT_PAGE_SEARCH;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.PREV_PAGE_SEARCH;

@Component
@RequiredArgsConstructor
public class FilterApplyHandler extends UserRequestHandler {
    private final PersonService personService;
    private final FilterService filterService;
    private final FriendsService friendsService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(UserRq request) {
        Update update = request.getUpdate();

        return isTextMessage(request.getUpdate(), APPLY) ||
                isCallback(update, PREV_PAGE_SEARCH) ||
                isCallback(update, NEXT_PAGE_SEARCH);
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Long chatId = request.getChatId();
        Update update = request.getUpdate();
        FriendsSession friendsSession = request.getFriendsSession();

        if (filterService.hasNoFilter(request)) {
            telegramService.sendMessage(chatId, "Установите хотя бы один фильтр");
            return;
        }

        if (isTextMessage(update, APPLY)) {
            filterService.applyFilter(request);

        } else if (friendsSession.getFriendsState() == FriendsState.SEARCH) {
            personService.navigateButtons(request, PREV_PAGE_SEARCH, NEXT_PAGE_SEARCH);
        }

        List<PersonRs> persons = request.getFriendsSession().getFriends();

        if (persons == null ||
                persons.isEmpty() ||
                friendsSession.getFriendsState() != FriendsState.SEARCH) {
            return;
        }

        friendsService.sendPaginatedFriends(
                request,
                persons,
                PREV_PAGE_SEARCH,
                NEXT_PAGE_SEARCH);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
