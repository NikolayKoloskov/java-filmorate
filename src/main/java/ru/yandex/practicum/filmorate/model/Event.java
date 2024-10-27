package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@EqualsAndHashCode
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private Long eventId;
    private Long userId;
    private Long filmId;
    private String entityName;
    private Long timestamp;
    private String eventType;
    private String operation;
}
