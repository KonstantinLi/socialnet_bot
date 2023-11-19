package ru.skillbox.socialnet.zeronebot.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import ru.skillbox.socialnet.zeronebot.dto.enums.LikeType;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LikeRq {
    private LikeType type;
    private Long itemId;
}
