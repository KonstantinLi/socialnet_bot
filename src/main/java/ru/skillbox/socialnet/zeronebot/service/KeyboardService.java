package ru.skillbox.socialnet.zeronebot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.skillbox.socialnet.zeronebot.constant.Common;
import ru.skillbox.socialnet.zeronebot.constant.Filter;
import ru.skillbox.socialnet.zeronebot.dto.enums.FriendShipStatus;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.CommentRs;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.dto.response.PostRs;
import ru.skillbox.socialnet.zeronebot.dto.session.FilterSession;

import java.util.ArrayList;
import java.util.List;

import static ru.skillbox.socialnet.zeronebot.constant.Comment.*;
import static ru.skillbox.socialnet.zeronebot.constant.Common.LOGIN;
import static ru.skillbox.socialnet.zeronebot.constant.Common.REGISTER;
import static ru.skillbox.socialnet.zeronebot.constant.Filter.*;
import static ru.skillbox.socialnet.zeronebot.constant.Friends.*;
import static ru.skillbox.socialnet.zeronebot.constant.Like.*;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.*;
import static ru.skillbox.socialnet.zeronebot.constant.Person.DELETE;
import static ru.skillbox.socialnet.zeronebot.constant.Person.*;
import static ru.skillbox.socialnet.zeronebot.constant.Post.*;

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

    public InlineKeyboardMarkup buildProfileMenu(UserRq userRq) {
        Long id = userRq.getUserSession().getId();

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        InlineKeyboardButton wallButton = new InlineKeyboardButton();
        wallButton.setText(WALL.getText());
        wallButton.setCallbackData(WALL.getCommand() + "_" + id);

        rowsInLine.add(List.of(wallButton));

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
        InlineKeyboardMarkup markupInLine = buildPersonMenu(personRs);
        List<List<InlineKeyboardButton>> rowsInLine = markupInLine.getKeyboard();
        rowsInLine.addAll(buildNavigateMenu(PREV_PERSON, NEXT_PERSON).getKeyboard());

        return markupInLine;
    }

    public InlineKeyboardMarkup buildPostMenuNavigate(PostRs postRs) {
        InlineKeyboardMarkup markupInLine = buildPostMenu(postRs);
        List<List<InlineKeyboardButton>> rowsInLine = markupInLine.getKeyboard();
        rowsInLine.addAll(buildNavigateMenu(PREV_POST, NEXT_POST).getKeyboard());

        return markupInLine;
    }

    public InlineKeyboardMarkup buildUserPostMenuNavigate(PostRs postRs, Long authorId) {
        InlineKeyboardMarkup markupInLine = postRs.getAuthor().getId().equals(authorId) ?
                buildMyPostMenu(postRs) :
                buildPostMenu(postRs);

        List<List<InlineKeyboardButton>> rowsInLine = markupInLine.getKeyboard();
        rowsInLine.addAll(buildNavigateMenu(
                PREV_USER_POST + "_" + authorId,
                NEXT_USER_POST + "_" + authorId)
                .getKeyboard());

        return markupInLine;
    }

    public InlineKeyboardMarkup buildCommentMenuNavigate(CommentRs commentRs, Long authorId) {
        InlineKeyboardMarkup markupInLine = buildCommentMenu(commentRs, authorId, false);
        List<List<InlineKeyboardButton>> rowsInLine = markupInLine.getKeyboard();
        rowsInLine.addAll(buildNavigateMenu(PREV_COMMENT, NEXT_COMMENT).getKeyboard());

        return markupInLine;
    }

    public InlineKeyboardMarkup buildCommentCommentMenuNavigate(CommentRs commentRs, Long authorId) {
        InlineKeyboardMarkup markupInLine = buildCommentMenu(commentRs, authorId, true);
        List<List<InlineKeyboardButton>> rowsInLine = markupInLine.getKeyboard();
        rowsInLine.addAll(buildNavigateMenu(PREV_COMMENT_COMMENT, NEXT_COMMENT_COMMENT).getKeyboard());

        return markupInLine;
    }

    public InlineKeyboardMarkup buildCommentMenu(
            CommentRs commentRs,
            Long authorId,
            boolean isSubComment) {

        Long id = commentRs.getId();

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        if (commentRs.getAuthor().getId().equals(authorId)) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            if (!commentRs.getIsDeleted()) {
                InlineKeyboardButton deleteButton = new InlineKeyboardButton(COMMENT_DELETE.getText());
                deleteButton.setCallbackData(COMMENT_DELETE.getCommand() + "_" + id);

                InlineKeyboardButton editButton = new InlineKeyboardButton(COMMENT_EDIT.getText());
                editButton.setCallbackData(COMMENT_EDIT.getCommand() + "_" + id);
                row.add(editButton);
                row.add(deleteButton);
            } else {
                InlineKeyboardButton recoverButton = new InlineKeyboardButton(COMMENT_RECOVER.getText());
                recoverButton.setCallbackData(COMMENT_RECOVER.getCommand() + "_" + id);
                row.add(recoverButton);
            }

            rowsInLine.add(row);
        }

        if (!commentRs.getIsDeleted()) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            InlineKeyboardButton likeButton =
                    new InlineKeyboardButton(commentRs.getMyLike() ?
                            UNLIKE_COMMENT.getText() :
                            LIKE_COMMENT.getText());
            likeButton.setCallbackData((commentRs.getMyLike() ?
                    UNLIKE_COMMENT.getCommand() :
                    LIKE_COMMENT.getCommand())
                    + "_" + id);

            row.add(likeButton);

            if (!isSubComment) {
                InlineKeyboardButton commentButton =
                        new InlineKeyboardButton(
                                COMMENT_COMMENT.getText() +
                                        " (" + commentRs.getSubComments().size() + ")");
                commentButton.setCallbackData(COMMENT_COMMENT.getCommand() + "_" + id);
                row.add(commentButton);
            }

            rowsInLine.add(row);
        }

        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    public InlineKeyboardMarkup buildPostMenu(PostRs postRs) {
        Long id = postRs.getId();

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        InlineKeyboardButton likeButton =
                new InlineKeyboardButton((postRs.getMyLike() ?
                        UNLIKE_POST.getText() :
                        LIKE_POST.getText())
                        + " (" + postRs.getLikes() + ")");
        likeButton.setCallbackData((postRs.getMyLike() ?
                UNLIKE_POST.getCommand() :
                LIKE_POST.getCommand())
                + "_" + id);

        InlineKeyboardButton commentButton =
                new InlineKeyboardButton(COMMENT.getText() + " (" + postRs.getComments().size() + ")");
        commentButton.setCallbackData(COMMENT.getCommand() + "_" + id);

        rowsInLine.add(List.of(likeButton, commentButton));
        markupInLine.setKeyboard(rowsInLine);

        return markupInLine;
    }

    public InlineKeyboardMarkup buildMyPostMenu(PostRs postRs) {
        Long id = postRs.getId();

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        InlineKeyboardButton deleteButton;

        if (postRs.getType().equals("DELETED")) {
            deleteButton = new InlineKeyboardButton(POST_RECOVER.getText());
            deleteButton.setCallbackData(POST_RECOVER.getCommand() + "_" + id);
        } else {
            deleteButton = new InlineKeyboardButton(POST_DELETE.getText());
            deleteButton.setCallbackData(POST_DELETE.getCommand() + "_" + id);
        }

        rowsInLine.add(List.of(deleteButton));
        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    public InlineKeyboardMarkup buildPersonMenu(PersonRs person) {
        Long id = person.getId();

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();

        String friendshipStatus = person.getFriendStatus();

        if (!FriendShipStatus.BLOCKED.toString().equals(friendshipStatus)) {
            InlineKeyboardButton messageButton = new InlineKeyboardButton(MESSAGE);
            messageButton.setCallbackData(MESSAGE + "_" + id);
            row1.add(messageButton);

            InlineKeyboardButton wallButton = new InlineKeyboardButton(WALL.getText());
            wallButton.setCallbackData(WALL.getCommand() + "_" + id);
            row1.add(wallButton);

            InlineKeyboardButton friendButton;

            if (FriendShipStatus.FRIEND.toString().equals(friendshipStatus)) {
                friendButton = new InlineKeyboardButton(DELETE);
                friendButton.setCallbackData(DELETE + "_" + id);

            } else if (FriendShipStatus.UNKNOWN.toString().equals(friendshipStatus)) {
                friendButton = new InlineKeyboardButton(ADD);
                friendButton.setCallbackData(ADD + "_" + id);

            } else if (FriendShipStatus.REQUEST.toString().equals(friendshipStatus)) {
                friendButton = new InlineKeyboardButton(CANCEL);
                friendButton.setCallbackData(CANCEL + "_" + id);

            } else {
                friendButton = new InlineKeyboardButton(CONFIRM);
                InlineKeyboardButton rejectButton = new InlineKeyboardButton(DECLINE);

                friendButton.setCallbackData(CONFIRM + "_" + id);
                rejectButton.setCallbackData(DECLINE + "_" + id);

                row2.add(rejectButton);
            }

            row2.add(friendButton);

            InlineKeyboardButton block = new InlineKeyboardButton(BLOCK);
            block.setCallbackData(BLOCK + "_" + id);
            row2.add(block);

        } else {
            InlineKeyboardButton unblock = new InlineKeyboardButton(UNBLOCK);
            unblock.setCallbackData(UNBLOCK + "_" + id);
            row2.add(unblock);
        }

        rowsInLine.add(row1);
        rowsInLine.add(row2);

        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    public ReplyKeyboardMarkup buildTagsMenu() {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(Common.WITHOUT_TAGS);
        keyboardRow.add(Common.CANCEL);

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(keyboardRow))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
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
