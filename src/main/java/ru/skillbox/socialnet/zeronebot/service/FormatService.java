package ru.skillbox.socialnet.zeronebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.zeronebot.dto.response.CommentRs;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.dto.response.PostRs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
public class FormatService {
    public String caption(PersonRs personRs, boolean shortInfo) {
        String birthDate = personRs.getBirthDate();
        String country = personRs.getCountry();
        String city = personRs.getCity();
        String about = personRs.getAbout();
        Boolean online = personRs.getOnline();

        StringBuilder builder = new StringBuilder();

        builder.append(getPersonName(personRs));

        if (birthDate != null) {
            builder.append(", ").append(age(birthDate));
        }

        if (!shortInfo) {
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
        }

        builder.append("\n\n");
        if (online == null || !online) {
            builder.append("\uD83D\uDD34 Не в сети");
        } else {
            builder.append("\uD83D\uDFE2 В сети");
        }

        return builder.toString();
    }

    public String formatPost(PostRs postRs) {
        String postType = postRs.getType();

        StringJoiner joiner = new StringJoiner("\n");
        if (postType != null && postType.equals("DELETED")) {
            joiner.add("<b><i>(Удалён)</i></b>");
        }
        joiner.add("<b>" + postRs.getTitle() + "</b>");
        joiner.add("Автор: " + getPersonName(postRs.getAuthor()));
        joiner.add("");
        joiner.add(postRs.getPostText());
        joiner.add("");
        joiner.add(formatDateTime(postRs.getTime(), "yyyy-MM-dd HH:mm:ss.SSSSSS"));

        return joiner.toString();
    }

    public String formatComment(CommentRs commentRs) {
        StringJoiner joiner = new StringJoiner("\n");
        if (commentRs.getIsDeleted()) {
            joiner.add("<b><i>(Удалён)</i></b>");
        }
        joiner.add(getPersonName(commentRs.getAuthor()));
        joiner.add("");
        joiner.add(commentRs.getCommentText());
        joiner.add("");
        joiner.add(formatDateTime(commentRs.getTime(), "yyyy-MM-dd'T'HH:mm:ss"));

        return joiner.toString();
    }

    public String getPersonName(PersonRs personRs) {
        return personRs.getFirstName() +
                (personRs.getLastName() != null ? " " + personRs.getLastName() : "");
    }

    private Integer age(String birthDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDate dateOfBirth = LocalDate.parse(birthDate, formatter);
        Period period = Period.between(dateOfBirth, LocalDate.now());
        return period.getYears();
    }

    private String formatDateTime(String inputDate, String pattern) {
        LocalDateTime dateTime = LocalDateTime.parse(
                inputDate,
                DateTimeFormatter.ofPattern(pattern));

        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm dd MMMM yyyy"));
    }
}
