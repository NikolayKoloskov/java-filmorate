package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.Timestamp;

@Component
@RequiredArgsConstructor
public class EventMaker {
    private final JdbcTemplate jdbcTemplate;

    public void makeFilmEvent(String eventType, String operation, Film film) {
        final String sql = "INSERT INTO events (times, event_type, event_operation, film_id, entity_name) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"event_id"});
                preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis())); // Изменение типа времени
                preparedStatement.setString(2, eventType);
                preparedStatement.setString(3, operation);
                preparedStatement.setLong(4, film.getId());
                preparedStatement.setString(5, film.getName());
                return preparedStatement;
            }, generatedKeyHolder);
        } catch (DataAccessException e) {
            throw new DataException("Что-то пошло не так при добавлении события для фильма");
        }
    }

    public void makeUserEvent(String eventType, String operation, User user) {
        final String sql = "INSERT INTO events (times, event_type, event_operation, user_id, entity_name) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"event_id"});
                preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis())); // Изменение типа времени
                preparedStatement.setString(2, eventType);
                preparedStatement.setString(3, operation);
                preparedStatement.setLong(4, user.getId());
                preparedStatement.setString(5, user.getName());
                return preparedStatement;
            }, generatedKeyHolder);
        } catch (DataAccessException e) {
            throw new DataException("Что-то пошло не так при добавлении события для пользователя");
        }
    }

    public void makeLikeEvent(String eventType, String operation, Long filmId, Long userId) {
        final String sql = "INSERT INTO events (times, event_type, event_operation, user_id, entity_name) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        String filmName = getFilmName(filmId);
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"event_id"});
                preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis())); // Изменение типа времени
                preparedStatement.setString(2, eventType);
                preparedStatement.setString(3, operation);
                preparedStatement.setLong(4, userId); // Сохранение в поле user_id
                preparedStatement.setString(5, "Пользователь с идентификатором " + userId + " поставил лайк фильму " + filmName);
                return preparedStatement;
            }, generatedKeyHolder);
        } catch (DataAccessException e) {
            throw new DataException("Что-то пошло не так при добавлении события для лайка");
        }
    }

    //TODO: +++Рефактор однотипных методов
    //TODO: +++Добавить раздел с отзывами после их реализации


    public String getFilmName(Long filmId) {
        String filmNameQuery = "SELECT name FROM films WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(filmNameQuery, new Object[]{filmId}, String.class);
        } catch (DataAccessException e) {
            throw new DataException("Фильм с идентификатором " + filmId + " не найден");
        }
    }


}
