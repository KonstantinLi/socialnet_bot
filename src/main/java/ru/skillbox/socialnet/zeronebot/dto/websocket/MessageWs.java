package ru.skillbox.socialnet.zeronebot.dto.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageWs {

    private Long id;

    @JsonProperty("dialog_id")
    private Long dialogId;

    @JsonProperty("author_id")
    private Long authorId;

    @JsonProperty("recipient_id")
    private Long recipientId;

    @JsonProperty("message_text")
    private String messageText;

    @JsonProperty("read_status")
    private String readStatus;

    private Long time;

    private String token;
}