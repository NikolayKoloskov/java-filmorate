package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@EqualsAndHashCode
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Like {
    private long userId;
    private long filmId;
}
