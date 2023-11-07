package ru.skillbox.socialnet.zeronebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.skillbox.socialnet.zeronebot.sender.ZeroneBotSender;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

@Component
@RequiredArgsConstructor
public class TelegramService {
    private final ZeroneBotSender botSender;

    public void sendMessage(Long chatId, String message) {
        sendMessage(chatId, message, null);
    }

    public void sendMessage(Long chatId, String message, ReplyKeyboard replyKeyboard) {
        SendMessage sendMessage = SendMessage.builder()
                .text(message)
                .chatId(chatId.toString())
                .parseMode(ParseMode.HTML)
                .replyMarkup(replyKeyboard)
                .build();

        try {
            botSender.execute(sendMessage);
        } catch (TelegramApiException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void sendPhotoURL(Long chatId, URL url, String caption) {
        sendPhotoURL(chatId, url, caption, null);
    }

    public void sendPhotoURL(
            Long chatId,
            URL url,
            String caption,
            ReplyKeyboard replyKeyboard) {

        try {
            SendPhoto sendPhoto = new SendPhoto();

            sendPhoto.setChatId(chatId.toString());
            sendPhoto.setCaption(caption);
            sendPhoto.setReplyMarkup(replyKeyboard);
            sendPhoto.setPhoto(new InputFile(url.toString()));

            botSender.execute(sendPhoto);

        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    public void sendPhotoBytes(
            Long chatId,
            byte[] bytes,
            String fileName,
            String caption,
            ReplyKeyboard replyKeyboard) {

        try {
            InputStream photoStream = new ByteArrayInputStream(bytes);

            SendPhoto sendPhoto = new SendPhoto();

            sendPhoto.setChatId(chatId.toString());
            sendPhoto.setCaption(caption);
            sendPhoto.setReplyMarkup(replyKeyboard);
            sendPhoto.setPhoto(new InputFile(photoStream, fileName));

            botSender.execute(sendPhoto);

        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }
}
