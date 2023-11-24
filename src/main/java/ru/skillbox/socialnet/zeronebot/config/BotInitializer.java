package ru.skillbox.socialnet.zeronebot.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.skillbox.socialnet.zeronebot.ZeroneBot;
import ru.skillbox.socialnet.zeronebot.session.ZeroneBotSession;

@Component
@RequiredArgsConstructor
public class BotInitializer {
    private final ZeroneBot bot;

    @EventListener({ ContextRefreshedEvent.class })
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(ZeroneBotSession.class);
        telegramBotsApi.registerBot(bot);
    }
}
