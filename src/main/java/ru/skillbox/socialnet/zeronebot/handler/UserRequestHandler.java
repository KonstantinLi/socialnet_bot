package ru.skillbox.socialnet.zeronebot.handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;

public abstract class UserRequestHandler {
    public abstract boolean isApplicable(SessionRq request);
    public abstract void handle(SessionRq request) throws Exception;
    public abstract boolean isGlobal();

    public boolean isCommand(Update update) {
        return update.hasMessage() && update.getMessage().isCommand();
    }

    public boolean isCommand(Update update, String command) {
        return update.hasMessage() && update.getMessage().isCommand()
                && update.getMessage().getText().equals(command);
    }

    public boolean isTextMessage(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    public boolean isPhotoMessage(Update update) {
        return update.hasMessage() && update.getMessage().hasPhoto();
    }

    public boolean isTextMessage(Update update, String text) {
        return update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals(text);
    }

    public boolean isTextMessageStartsWith(Update update, String text) {
        return update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().startsWith(text);
    }

    public boolean isCallback(Update update, String text) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals(text);
    }

    public boolean isCallbackStartsWith(Update update, String start) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith(start);
    }
}
