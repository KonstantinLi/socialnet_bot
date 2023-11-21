package ru.skillbox.socialnet.zeronebot.constant;

import lombok.Getter;

@Getter
public enum Menu {
    PROFILE("/profile", "\uD83D\uDC64 Моя страница"),
    FRIENDS("/friends", "\uD83D\uDC65 Друзья"),
    DIALOGS("/dialogs", "✉\uFE0F Диалоги"),
    NEWS("/news", "\uD83D\uDCF0 Новости"),
    EXIT("/exit", "❌ Выйти");

    private final String command;
    private final String text;

    Menu(String command, String text) {
        this.command = command;
        this.text = text;
    }
}
