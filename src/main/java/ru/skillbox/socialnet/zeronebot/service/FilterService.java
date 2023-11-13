package ru.skillbox.socialnet.zeronebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.zeronebot.dto.enums.FilterState;
import ru.skillbox.socialnet.zeronebot.dto.session.FilterSession;

@Service
@RequiredArgsConstructor
public class FilterService {
    public void setFilterProperty(
            FilterSession filterSession,
            FilterState filterState,
            String property) {

        switch (filterState) {
            case AGE_FROM_WAIT -> {
                Integer ageFrom = Integer.valueOf(property);
                filterSession.setAgeFrom(ageFrom);
            }
            case AGE_TO_WAIT -> {
                Integer ageTo = Integer.valueOf(property);
                filterSession.setAgeTo(ageTo);
            }
            case CITY_WAIT -> filterSession.setCity(property);
            case COUNTRY_WAIT -> filterSession.setCountry(property);
            case FIRST_NAME_WAIT -> filterSession.setFirstName(property);
            case LAST_NAME_WAIT -> filterSession.setLastName(property);
            default -> {}
        }
    }
}
