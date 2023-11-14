package ru.skillbox.socialnet.zeronebot.dto.enums;

import lombok.Getter;

@Getter
public enum Menu {
    PROFILE("/profile", "\uD83D\uDC64 Моя страница"),
    FRIENDS("/friends", "\uD83D\uDC65 Друзья"),
    MESSAGES("/messages", "✉\uFE0F Сообщения"),
    NEWS("/news", "\uD83D\uDCF0 Новости"),
    SETTINGS("/settings", "⚙\uFE0F Настройки"),
    EXIT("/exit", "❌ Выйти");

    private final String command;
    private final String text;

    Menu(String command, String text) {
        this.command = command;
        this.text = text;
    }
}