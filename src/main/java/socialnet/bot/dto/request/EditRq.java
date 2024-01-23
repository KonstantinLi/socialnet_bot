package socialnet.bot.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EditRq {
    private String about;
    private String city;
    private String country;
    private String phone;
    private String birthDate;
    private String firstName;
    private String lastName;
    private String photoId;
}
