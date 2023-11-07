package ru.skillbox.socialnet.zeronebot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.handler.ExceptionHandler;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Component
public class Dispatcher {
    private final List<UserRequestHandler> handlers;
    private final ExceptionHandler exceptionHandler;

    @Autowired
    public Dispatcher(List<UserRequestHandler> handlers, ExceptionHandler exceptionHandler) {
        this.handlers = handlers.stream()
                .sorted(Comparator.comparing(UserRequestHandler::isGlobal).reversed())
                .toList();
        this.exceptionHandler = exceptionHandler;
    }

    public void dispatch(UserRq userRq) {
        try {
            for (UserRequestHandler userRequestHandler : handlers) {
                if (userRequestHandler.isApplicable(userRq)) {
                    userRequestHandler.handle(userRq);
                    break;
                }
            }
        } catch (IOException ex) {
            exceptionHandler.handle(userRq);
        }
    }
}
