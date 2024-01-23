package socialnet.bot.handler.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import socialnet.bot.dto.enums.state.EditState;
import socialnet.bot.dto.request.EditRq;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.EditSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.FormatService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.EditSessionService;

@Component
@RequiredArgsConstructor
public class ProfileEditEnterHandler extends UserRequestHandler {
    private final FormatService formatService;
    private final TelegramService telegramService;
    private final EditSessionService editSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        Update update = request.getUpdate();
        EditState editState = request.getEditSession().getEditState();
        return editState != null && isTextMessage(update) && !isCommand(update);
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();
        Update update = request.getUpdate();

        EditSession editSession = request.getEditSession();
        EditState editState = editSession.getEditState();
        EditRq editRq = editSession.getEditRq();

        try {
            String message = update.getMessage().getText().trim();
            switch (editState) {
                case ABOUT_WAIT -> editRq.setAbout(message);
                case CITY_WAIT -> editRq.setCity(message);
                case COUNTRY_WAIT -> editRq.setCountry(message);
                case PHONE_WAIT -> editRq.setPhone(message);
                case FIRST_NAME_WAIT -> editRq.setFirstName(message);
                case LAST_NAME_WAIT -> editRq.setLastName(message);
                case BIRTHDATE_WAIT -> {
                    if (message.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
                        editRq.setBirthDate(formatService.formatDate(
                                message,
                                "dd.MM.yyyy",
                                "yyyy-MM-dd'T'HH:mm:ss"));
                    } else {
                        telegramService.sendMessage(chatId, "Неправильный формат даты");
                        editSession.setEditState(null);
                        editSessionService.saveSession(chatId, editSession);
                        return;
                    }
                }
            }

            telegramService.sendMessage(chatId, "Данные введены");

        } catch (Exception ex) {
            throw new RuntimeException(ex);

        } finally {
            editSession.setEditRq(editRq);
            editSession.setEditState(null);
            editSessionService.saveSession(chatId, editSession);
        }
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
