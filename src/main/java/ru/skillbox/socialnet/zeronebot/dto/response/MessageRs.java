package ru.skillbox.socialnet.zeronebot.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.skillbox.socialnet.zeronebot.dto.enums.ReadStatus;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageRs {
    private Long id;
    private Boolean isSentByMe;
    private PersonRs recipient;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime time;

    @JsonProperty("author_id")
    private Long authorId;

    @JsonProperty("message_text")
    private String messageText;

    @JsonProperty("read_status")
    private ReadStatus readStatus;

    @JsonProperty("recipient_id")
    private Long recipientId;
}

