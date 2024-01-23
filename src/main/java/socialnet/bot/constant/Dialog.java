package socialnet.bot.constant;

import lombok.Getter;

@Getter
public enum Dialog {
    MESSAGE("/dialog_message", "Написать сообщение"),
    READ("/read_dialog", "Отметить прочитанным"),
    CLOSE("/close_dialog", "❌ Закрыть диалог");

    private final String command;
    private final String text;

    Dialog(String command, String text) {
        this.command = command;
        this.text = text;
    }
}
