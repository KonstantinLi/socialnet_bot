package socialnet.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.handler.UserRequestHandler;

import java.util.Comparator;
import java.util.List;

@Component
public class Dispatcher {
    private final List<UserRequestHandler> handlers;

    @Autowired
    public Dispatcher(List<UserRequestHandler> handlers) {
        this.handlers = handlers.stream()
                .sorted(Comparator.comparing(UserRequestHandler::isGlobal).reversed())
                .toList();
    }

    public void dispatch(SessionRq sessionRq) throws Exception {
        for (UserRequestHandler userRequestHandler : handlers) {
            if (userRequestHandler.isApplicable(sessionRq)) {
                userRequestHandler.handle(sessionRq);
                break;
            }
        }
    }
}
