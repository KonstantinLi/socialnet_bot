package ru.skillbox.socialnet.zeronebot.constant;

import lombok.Getter;

@Getter
public enum Post {
    POST_DELETE("/delete_post", "❌"),
    POST_RECOVER("/recover_post", "\uD83D\uDD04"),
    WALL("/wall", "Публикации");

    private final String command;
    private final String text;

    Post(String command, String text) {
        this.command = command;
        this.text = text;
    }
}
