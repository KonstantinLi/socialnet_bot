package socialnet.bot.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRq {
    @JsonProperty("email")
    String login;
    String password;
}
