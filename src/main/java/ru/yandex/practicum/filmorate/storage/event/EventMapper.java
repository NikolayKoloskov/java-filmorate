package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Event().toBuilder().eventId(rs.getLong("event_id"))
                .userId(rs.getLong("user_id")).filmId(rs.getLong("film_id"))
                .entityName(rs.getString("entity_name")).timestamp(rs.getLong("times"))
                .eventType(rs.getString("event_type")).operation(rs.getString("event_operation"))
                .build();
    }
}
