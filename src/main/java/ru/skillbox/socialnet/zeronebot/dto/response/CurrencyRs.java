package ru.skillbox.socialnet.zeronebot.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrencyRs {
    private String euro;
    private String usd;
}
