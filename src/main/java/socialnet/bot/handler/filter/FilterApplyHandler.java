package socialnet.bot.handler.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import socialnet.bot.dto.enums.state.FriendsState;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.response.PersonRs;
import socialnet.bot.dto.session.FriendsSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.FilterService;
import socialnet.bot.service.FriendsService;
import socialnet.bot.service.PersonService;
import socialnet.bot.service.TelegramService;

import java.util.List;

import static socialnet.bot.constant.Filter.APPLY;
import static socialnet.bot.constant.Navigate.NEXT_PAGE_SEARCH;
import static socialnet.bot.constant.Navigate.PREV_PAGE_SEARCH;

@Component
@RequiredArgsConstructor
public class FilterApplyHandler extends UserRequestHandler {
    private final PersonService personService;
    private final FilterService filterService;
    private final FriendsService friendsService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(SessionRq request) {
        Update update = request.getUpdate();

        return isTextMessage(request.getUpdate(), APPLY) ||
                isCallback(update, PREV_PAGE_SEARCH) ||
                isCallback(update, NEXT_PAGE_SEARCH);
    }

    @Override
    public void handle(SessionRq request) throws Exception {
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
