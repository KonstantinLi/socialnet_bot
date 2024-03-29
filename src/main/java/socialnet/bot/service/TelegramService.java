package socialnet.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import socialnet.bot.sender.ZeroneBotSender;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class TelegramService {
    private final ZeroneBotSender botSender;

    public void sendMessage(Long chatId, String message) {
        sendMessage(chatId, message, null);
    }

    public void sendMessage(Long chatId, String message, ReplyKeyboard replyKeyboard) {
        String regex = "<(?!/?b|/?i|/?u|/?s|/?a)[^>]*>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        SendMessage sendMessage = SendMessage.builder()
                .text(matcher.replaceAll(""))
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

    public void sendPhotoURL(
            Long chatId,
            URL url,
            String caption,
            ReplyKeyboard replyKeyboard) {

        SendPhoto sendPhoto = new SendPhoto();

        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setCaption(caption);
        sendPhoto.setReplyMarkup(replyKeyboard);
        sendPhoto.setPhoto(new InputFile(url.toString()));

        try {
            botSender.execute(sendPhoto);
        } catch (TelegramApiException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void sendPhotoBytes(
            Long chatId,
            byte[] bytes,
            String fileName,
            String caption,
            ReplyKeyboard replyKeyboard) {

        InputStream photoStream = new ByteArrayInputStream(bytes);

        SendPhoto sendPhoto = new SendPhoto();

        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setCaption(caption);
        sendPhoto.setReplyMarkup(replyKeyboard);
        sendPhoto.setPhoto(new InputFile(photoStream, fileName));

        try {
            botSender.execute(sendPhoto);
        } catch (TelegramApiException ex) {
            throw new RuntimeException(ex);
        }
    }
}
