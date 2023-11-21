package ru.skillbox.socialnet.zeronebot.dto.session;

import lombok.Builder;
import lombok.Data;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.EditState;
import ru.skillbox.socialnet.zeronebot.dto.request.EditRq;

@Data
@Builder
public class EditSession {
    private Long chatId;
    private EditRq editRq;
    private EditState editState;
}
