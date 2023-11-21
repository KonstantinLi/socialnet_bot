package ru.skillbox.socialnet.zeronebot.constant;

import lombok.Getter;

@Getter
public enum Comment {
    COMMENT("/comment_show", "💬"),
    COMMENT_COMMENT("/comment_comment", "💬"),
    COMMENT_ADD("/comment_add", "\uD83D\uDCDD Добавить комментарий"),
    COMMENT_COMMENT_ADD("/comment_comment_add", "\uD83D\uDCDD Добавить комментарий"),
    COMMENT_EDIT("/comment_edit", "✏\uFE0F"),
    COMMENT_DELETE("/comment_delete", "❌"),
    COMMENT_RECOVER("/comment_recover", "\uD83D\uDD04");

    private final String command;
    private final String text;

    Comment(String command, String text) {
        this.command = command;
        this.text = text;
    }
}
