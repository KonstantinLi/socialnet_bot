package socialnet.bot.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherRs {
    private String city;
    private String clouds;
    private String date;
    private String temp;

}
