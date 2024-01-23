package socialnet.bot.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import socialnet.bot.dto.enums.MessagePermission;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonRs {
    private String about;
    private String city;
    private String country;
    private CurrencyRs currency;
    private String email;
    private Long id;
    private Boolean online;
    private String phone;
    private String photo;
    private String token;
    private WeatherRs weather;
    private String birthDate;
    private String firstName;
    private String friendStatus;
    private Boolean isBlocked;
    private Boolean isBlockedByCurrentUser;
    private String lastName;
    private String lastOnlineTime;
    private MessagePermission messagesPermission;
    private String regDate;
    private Boolean userDeleted;
}
