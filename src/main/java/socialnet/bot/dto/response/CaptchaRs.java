package socialnet.bot.dto.response;

import lombok.Data;

@Data
public class CaptchaRs {
    private String code;
    private String image;
}
