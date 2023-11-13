package ru.skillbox.socialnet.zeronebot.exception;

public class OutOfListException extends RuntimeException {
    public OutOfListException() {
        super("Список завершен");
    }
}
