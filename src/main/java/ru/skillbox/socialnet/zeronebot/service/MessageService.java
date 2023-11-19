package ru.skillbox.socialnet.zeronebot.service;

import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;

@Service
public class MessageService {
    public String incoming(int count) {
        if (count == 0) {
            return "У вас нет входящих заявок в друзья";
        }

        String numberString = String.valueOf(count);
        return switch (numberString.charAt(numberString.length() - 1)) {
            case '1' -> String.format("У вас %s входящая заявка", numberString);
            case '2', '3', '4' -> String.format("У вас %s входящие заявки", numberString);
            default -> String.format("У вас %s входящих заявок", numberString);
        };
    }

    public String outgoing(int count) {
        if (count == 0) {
            return "У вас нет исходящих заявок в друзья";
        }

        String numberString = String.valueOf(count);
        return switch (numberString.charAt(numberString.length() - 1)) {
            case '1' -> String.format("У вас %s исходящая заявка", numberString);
            case '2', '3', '4' -> String.format("У вас %s исходящие заявки", numberString);
            default -> String.format("У вас %s исходящих заявок", numberString);
        };
    }

    public String recommend(int count) {
        if (count == 0) {
            return "Рекомендуемых друзей не найдено";
        }

        String numberString = String.valueOf(count);
        return switch (numberString.charAt(numberString.length() - 1)) {
            case '1' -> String.format("Найден %s рекомендуемый друг", numberString);
            case '2', '3', '4' -> String.format("Найдены %s рекомендуемых друга", numberString);
            default -> String.format("Найдено %s рекомендуемых друзей", numberString);
        };
    }


    public String friends(int count) {
        if (count == 0) {
            return "У вас нету друзей";
        }

        String numberString = String.valueOf(count);
        return switch (numberString.charAt(numberString.length() - 1)) {
            case '1' -> String.format("У вас %s друг", numberString);
            case '2', '3', '4' -> String.format("У вас %s друга", numberString);
            default -> String.format("У вас %s друзей", numberString);
        };
    }
    public String search(int count) {
        if (count == 0) {
            return "Людей не найдено по указанным фильтрам";
        }

        String numberString = String.valueOf(count);
        return switch (numberString.charAt(numberString.length() - 1)) {
            case '1' -> String.format("Найден %s пользователь", numberString);
            case '2', '3', '4' -> String.format("Найдены %s пользователя", numberString);
            default -> String.format("Найдено %s пользователей", numberString);
        };
    }

    public Long getIdFromCallback(UserRq request, String callback) {
        return Long.valueOf(request.getUpdate()
                .getCallbackQuery()
                .getData()
                .replace(callback + "_", ""));
    }
}
