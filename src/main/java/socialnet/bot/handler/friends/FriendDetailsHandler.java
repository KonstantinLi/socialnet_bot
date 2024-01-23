package socialnet.bot.handler.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.response.PersonRs;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.HttpService;
import socialnet.bot.service.MessageService;
import socialnet.bot.service.PersonService;

import static socialnet.bot.constant.Friends.PERSON_INFO;

@Component
@RequiredArgsConstructor
public class FriendDetailsHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final PersonService personService;
    private final MessageService messageService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCallbackStartsWith(request.getUpdate(), PERSON_INFO);
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long id = messageService.getIdFromCallback(request, PERSON_INFO);
        PersonRs personRs = httpService.getPersonById(request, id);
        personService.sendPersonDetails(request, personRs);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
