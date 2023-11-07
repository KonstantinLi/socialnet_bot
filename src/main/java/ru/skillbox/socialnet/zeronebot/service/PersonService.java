package ru.skillbox.socialnet.zeronebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.helper.KeyboardHelper;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static ru.skillbox.socialnet.zeronebot.constant.Friends.PERSON_INFO;

@Service
@RequiredArgsConstructor
public class PersonService {
    private static final int PAGE_SIZE = 5;

    private final TelegramService telegramService;
    private final KeyboardHelper keyboardHelper;

    public void sendPaginatedFriends(UserRq userRq, List<PersonRs> personList, int page) {
        int start = Math.min(page * PAGE_SIZE, personList.size());
        int end = Math.min(start + PAGE_SIZE, personList.size());

        List<PersonRs> personsPage = personList.subList(start, end);

        if (personsPage.isEmpty()) {
            telegramService.sendMessage(userRq.getChatId(), "Список завершен");
            return;
        }

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        for (PersonRs person : personsPage) {
            InlineKeyboardButton button = new InlineKeyboardButton(getName(person));
            button.setCallbackData(PERSON_INFO + "_" + person.getId());
            rowsInLine.add(List.of(button));
        }

        InlineKeyboardMarkup navigate = keyboardHelper.buildNavigateMenu();
        rowsInLine.addAll(navigate.getKeyboard());

        markupInLine.setKeyboard(rowsInLine);

        telegramService.sendMessage(
                userRq.getChatId(),
                String.format("(%d-%d)", start + 1, end),
                markupInLine);
    }

    public void sendPersonDetails(UserRq userRq, PersonRs person) throws IOException {
        InlineKeyboardMarkup markupInLine = keyboardHelper.buildPersonMenu(person);
        telegramService.sendPhotoURL(userRq.getChatId(),
                new URL(person.getPhoto()),
                caption(person),
                markupInLine);
    }

    public String caption(PersonRs personRs) {
        String birthDate = personRs.getBirthDate();
        String country = personRs.getCountry();
        String city = personRs.getCity();
        String about = personRs.getAbout();

        StringBuilder builder = new StringBuilder();

        builder.append(personRs.getFirstName()).append(" ").append(personRs.getLastName());

        if (birthDate != null) {
            builder.append(", ").append(age(birthDate));
        }

        if (city != null) {
            builder.append(", ").append(city);
            if (country != null) {
                builder.append(" (").append(country).append(")");
            }
        } else if (country != null) {
            builder.append(", ").append(country);
        }

        if (about != null) {
            builder.append("\n\n").append(about);
        }

        return builder.toString();
    }

    private Integer age(String birthDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDate dateOfBirth = LocalDate.parse(birthDate, formatter);
        Period period = Period.between(dateOfBirth, LocalDate.now());
        return period.getYears();
    }

    private String getName(PersonRs personRs) {
        return personRs.getFirstName() +
                (personRs.getLastName() != null ? " " + personRs.getLastName() : "");
    }
}
