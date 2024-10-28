package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@EqualsAndHashCode(exclude = "reviewId")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @NotNull
    private long reviewId;

    @NotNull
    private String content;

    @NotNull
    private Boolean isPositive;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer filmId;

    @NotNull
    private Integer useful;
}
