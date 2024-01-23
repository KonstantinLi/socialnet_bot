package socialnet.bot.constant;

import lombok.Getter;

@Getter
public enum Like {
    LIKE_POST("/like_post", "♥\uFE0F"),
    UNLIKE_POST("/unlike_post", "💔"),

    LIKE_COMMENT("/like_comment", "♥\uFE0F"),
    UNLIKE_COMMENT("/unlike_comment", "💔");

    private final String command;
    private final String text;

    Like(String command, String text) {
        this.command = command;
        this.text = text;
    }
}
