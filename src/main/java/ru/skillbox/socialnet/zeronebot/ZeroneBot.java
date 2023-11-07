package ru.skillbox.socialnet.zeronebot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.config.BotProperties;
import ru.skillbox.socialnet.zeronebot.dto.session.LoginSession;
import ru.skillbox.socialnet.zeronebot.dto.session.RegisterSession;
import ru.skillbox.socialnet.zeronebot.dto.session.UserSession;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.service.session.LoginSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.RegisterSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.UserSessionService;

@Component
@RequiredArgsConstructor
public class ZeroneBot extends TelegramLongPollingBot {

    private final UserSessionService userSessionService;
    private final LoginSessionService loginSessionService;
    private final RegisterSessionService registerSessionService;

    private final BotProperties botProperties;
    private final Dispatcher dispatcher;

    @Override
    public String getBotToken() {
        return botProperties.getKey();
    }

    @Override
    public String getBotUsername() {
        return botProperties.getName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() ||
            update.hasCallbackQuery()) {

            Long chatId = update.getMessage() != null ? update.getMessage().getChatId() :
                    update.getCallbackQuery().getMessage().getChatId();

            UserSession userSession = userSessionService.getSession(chatId);
            LoginSession loginSession = loginSessionService.getSession(chatId);
            RegisterSession registerSession = registerSessionService.getSession(chatId);

            UserRq userRq = UserRq
                    .builder()
                    .update(update)
                    .userSession(userSession)
                    .loginSession(loginSession)
                    .registerSession(registerSession)
                    .chatId(chatId)
                    .build();

            dispatcher.dispatch(userRq);
        }
    }
}
