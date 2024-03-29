package socialnet.bot.handler.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import socialnet.bot.dto.enums.state.FilterState;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.FilterSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.session.FilterSessionService;

import static socialnet.bot.constant.Filter.*;

@Service
@RequiredArgsConstructor
public class FilterHandler extends UserRequestHandler {
    private final FilterSessionService filterSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        Update update = request.getUpdate();

        return isTextMessageStartsWith(update, AGE_FROM) ||
                isTextMessageStartsWith(update, AGE_TO) ||
                isTextMessageStartsWith(update, CITY) ||
                isTextMessageStartsWith(update, COUNTRY) ||
                isTextMessageStartsWith(update, FIRST_NAME) ||
                isTextMessageStartsWith(update, LAST_NAME);
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Update update = request.getUpdate();
        FilterSession filterSession = request.getFilterSession();

        if (isTextMessageStartsWith(update, AGE_FROM)) {
            filterSession.setFilterState(FilterState.AGE_FROM_WAIT);
        } else if (isTextMessageStartsWith(update, AGE_TO)) {
            filterSession.setFilterState(FilterState.AGE_TO_WAIT);
        } else if (isTextMessageStartsWith(update, CITY)) {
            filterSession.setFilterState(FilterState.CITY_WAIT);
        } else if (isTextMessageStartsWith(update, COUNTRY)) {
            filterSession.setFilterState(FilterState.COUNTRY_WAIT);
        } else if (isTextMessageStartsWith(update, FIRST_NAME)) {
            filterSession.setFilterState(FilterState.FIRST_NAME_WAIT);
        } else if (isTextMessageStartsWith(update, LAST_NAME)) {
            filterSession.setFilterState(FilterState.LAST_NAME_WAIT);
        }

        filterSessionService.saveSession(request.getChatId(), filterSession);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
