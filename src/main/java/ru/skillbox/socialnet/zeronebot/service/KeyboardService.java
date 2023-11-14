package ru.skillbox.socialnet.zeronebot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.skillbox.socialnet.zeronebot.constant.Common;
import ru.skillbox.socialnet.zeronebot.constant.Filter;
import ru.skillbox.socialnet.zeronebot.dto.enums.FriendShipStatus;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.dto.session.FilterSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.skillbox.socialnet.zeronebot.constant.Callback.NEXT_PERSON;
import static ru.skillbox.socialnet.zeronebot.constant.Callback.PREV_PERSON;
import static ru.skillbox.socialnet.zeronebot.constant.Common.LOGIN;
import static ru.skillbox.socialnet.zeronebot.constant.Common.REGISTER;
import static ru.skillbox.socialnet.zeronebot.constant.Filter.*;
import static ru.skillbox.socialnet.zeronebot.constant.Friends.*;
import static ru.skillbox.socialnet.zeronebot.constant.Person.DELETE;
import static ru.skillbox.socialnet.zeronebot.constant.Person.*;

@Service
public class KeyboardService {
    public ReplyKeyboardMarkup buildFriendsMenu() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(FRIENDS_LIST);
        row1.add(FRIENDS_RECOMMENDS);
        row1.add(FRIENDS_SEARCH);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(FRIENDS_INCOMING);
        row2.add(FRIENDS_OUTGOING);

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row1, row2))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }

    public ReplyKeyboardMarkup buildFilterMenu(FilterSession filter) {
        Integer ageFrom = filter.getAgeFrom();
        Integer ageTo = filter.getAgeTo();
        String city = filter.getCity();
        String country = filter.getCountry();
        String firstName = filter.getFirstName();
        String lastName = filter.getLastName();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(FIRST_NAME + (firstName != null ? " " + firstName : ""));
        row1.add(LAST_NAME + (lastName != null ? " " + lastName : ""));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(CITY + (city != null ? " " + city : ""));
        row2.add(COUNTRY + (country != null ? " " + country : ""));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(AGE_FROM + (ageFrom != null ? " " + ageFrom : ""));
        row3.add(AGE_TO + (ageTo != null ? " " + ageTo : ""));

        KeyboardRow row4 = new KeyboardRow();
        row4.add(APPLY);
        row4.add(Filter.DELETE);

        KeyboardRow row5 = new KeyboardRow();
        row5.add(SEARCH_EXIT);

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row1, row2, row3, row4, row5))
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

    public InlineKeyboardMarkup buildNavigateMenu(String prevPage, String nextPage) {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        InlineKeyboardButton prev = new InlineKeyboardButton("◀\uFE0F");
        prev.setCallbackData(prevPage);

        InlineKeyboardButton next = new InlineKeyboardButton("▶\uFE0F");
        next.setCallbackData(nextPage);

        rowsInLine.add(List.of(prev, next));

        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    public InlineKeyboardMarkup buildPersonMenuNavigate(PersonRs personRs) {
        InlineKeyboardButton prev = new InlineKeyboardButton("◀\uFE0F");
        prev.setCallbackData(PREV_PERSON);

        InlineKeyboardButton next = new InlineKeyboardButton("▶\uFE0F");
        next.setCallbackData(NEXT_PERSON);

        InlineKeyboardMarkup markupInLine = buildPersonMenu(personRs);
        markupInLine.getKeyboard().add(List.of(prev, next));

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
