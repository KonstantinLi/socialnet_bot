package socialnet.bot.constant;

import lombok.Getter;

@Getter
public enum Person {
    ADD("/add_friend", "Добавить в друзья"),
    CANCEL("/cancel_friend", "Отменить заявку"),
    CONFIRM("/confirm_friend", "Подтвердить"),
    DECLINE("/decline_friend", "Отклонить"),
    DELETE("/delete_friend", "Удалить из друзей"),
    BLOCK("/block_user", "Заблокировать"),
    UNBLOCK("/unblock_user", "Разблокировать"),
    MESSAGE("/user_message", "Написать сообщение"),
    EDIT("/profile_edit", "Редактировать");

    private final String command;
    private final String text;

    Person(String command, String text) {
        this.command = command;
        this.text = text;
    }
}
