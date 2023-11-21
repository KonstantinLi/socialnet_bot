package ru.skillbox.socialnet.zeronebot.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import ru.skillbox.socialnet.zeronebot.dto.enums.ReadStatus;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DialogRs {
    private Long id;

    @JsonProperty("author_id")
    private Long authorId;

    @JsonProperty("last_message")
    private MessageRs lastMessage;

    @JsonProperty("read_status")
    private ReadStatus readStatus;

    @JsonProperty("recipient_id")
    private Long recipientId;

    @JsonProperty("unread_count")
    private Long unreadCount;
}
