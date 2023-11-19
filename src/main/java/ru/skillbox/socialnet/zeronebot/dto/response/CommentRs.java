package ru.skillbox.socialnet.zeronebot.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CommentRs {
    private Long id;
    private Long likes;
    private String time;
    private Long postId;
    private Long parentId;
    private Boolean myLike;
    private PersonRs author;
    private Boolean isBlocked;
    private Boolean isDeleted;
    private String commentText;
    private List<CommentRs> subComments;
}
