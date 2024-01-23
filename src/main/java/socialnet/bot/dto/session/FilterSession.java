package socialnet.bot.dto.session;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import socialnet.bot.dto.enums.state.FilterState;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FilterSession {
    private Long chatId;
    private Integer ageFrom;
    private Integer ageTo;
    private String city;
    private String country;
    private String firstName;
    private String lastName;
    private FilterState filterState;
}
