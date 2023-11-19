package ru.skillbox.socialnet.zeronebot.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostRs {
    private Long id;
    private Long likes;
    private String time;
    private String type;
    private String title;
    private Boolean myLike;
    private PersonRs author;
    private String postText;
    private Set<String> tags;
    private boolean isBlocked;
    private List<CommentRs> comments;
}
