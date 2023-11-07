package ru.skillbox.socialnet.zeronebot.sender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Component
public class ZeroneBotSender extends DefaultAbsSender {
    @Value("${bot.key}")
    private String token;

    protected ZeroneBotSender() {
        super(new DefaultBotOptions());
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
