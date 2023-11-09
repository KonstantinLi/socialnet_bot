package ru.skillbox.socialnet.zeronebot.helper;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.skillbox.socialnet.zeronebot.constant.Common;
import ru.skillbox.socialnet.zeronebot.dto.enums.FriendShipStatus;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.skillbox.socialnet.zeronebot.constant.Common.*;
import static ru.skillbox.socialnet.zeronebot.constant.Friends.*;
import static ru.skillbox.socialnet.zeronebot.constant.Person.CANCEL;
import static ru.skillbox.socialnet.zeronebot.constant.Person.*;
import static ru.skillbox.socialnet.zeronebot.constant.Profile.*;

@Component
public class KeyboardHelper {
    public ReplyKeyboardMarkup buildMainMenu() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(PROFILE);
        row1.add(FRIENDS);
        row1.add(MESSAGES);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(LOGOUT);

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row1, row2))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }

    public ReplyKeyboardMarkup buildFriendsMenu() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(FRIENDS_LIST);
        row1.add(FRIENDS_RECOMMENDS);
        row1.add(FRIENDS_SEARCH);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(FRIEND_INCOMING);
        row2.add(FRIENDS_OUTGOING);

        KeyboardRow row3 = new KeyboardRow();
        row3.add(RETURN);

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row1, row2, row3))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }

    public InlineKeyboardMarkup buildAuthMenu() {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        InlineKeyboardButton authButton = new InlineKeyboardButton();
        authButton.setText(LOGIN);
        authButton.setCallbackData(LOGIN);

        InlineKeyboardButton regButton = new InlineKeyboardButton();
        regButton.setText(REGISTER);
        regButton.setCallbackData(REGISTER);

        rowsInLine.add(List.of(authButton, regButton));

        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    public InlineKeyboardMarkup buildNavigateMenu() {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        InlineKeyboardButton prev = new InlineKeyboardButton("<");
        prev.setCallbackData(PREV_PAGE);

        InlineKeyboardButton next = new InlineKeyboardButton(">");
        next.setCallbackData(NEXT_PAGE);

        rowsInLine.add(List.of(prev, next));

        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    public InlineKeyboardMarkup buildPersonMenu(PersonRs person) {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();

        InlineKeyboardButton message = new InlineKeyboardButton(MESSAGE);
        message.setCallbackData(MESSAGE);
        row1.add(message);

        InlineKeyboardButton friendButton = null;
        String friendshipStatus = person.getFriendStatus();

        if (FriendShipStatus.FRIEND.toString().equals(friendshipStatus)) {
            friendButton = new InlineKeyboardButton(DELETE);
            friendButton.setCallbackData(DELETE);

        } else if (FriendShipStatus.UNKNOWN.toString().equals(friendshipStatus)) {
            friendButton = new InlineKeyboardButton(ADD);
            friendButton.setCallbackData(ADD);

        } else if (FriendShipStatus.REQUEST.toString().equals(friendshipStatus)) {
            friendButton = new InlineKeyboardButton(CANCEL);
            friendButton.setCallbackData(CANCEL);

        } else if (FriendShipStatus.RECEIVED_REQUEST.toString().equals(friendshipStatus)) {
            friendButton = new InlineKeyboardButton(CONFIRM);
            InlineKeyboardButton rejectButton = new InlineKeyboardButton(REJECT);

            friendButton.setCallbackData(CONFIRM);
            rejectButton.setCallbackData(REJECT);

            row1.add(rejectButton);
        }

        row1.add(friendButton);
        rowsInLine.add(row1);

        boolean isBlocked = Optional.ofNullable(person.getIsBlockedByCurrentUser()).orElse(false);
        if (isBlocked) {
            InlineKeyboardButton unblock = new InlineKeyboardButton(UNBLOCK);
            unblock.setCallbackData(UNBLOCK);
            rowsInLine.add(List.of(unblock));
        } else {
            InlineKeyboardButton block = new InlineKeyboardButton(BLOCK);
            block.setCallbackData(BLOCK);
            rowsInLine.add(List.of(block));
        }

        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    public ReplyKeyboardMarkup buildMenuWithCancel() {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(Common.CANCEL);

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(keyboardRow))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }
}
