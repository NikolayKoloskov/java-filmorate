package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long filmId, Long userId) {

        final String sql = "insert into film_likes (user_id, film_id) values (?, ?)";

        try {
            jdbcTemplate.update(sql, userId, filmId);
            log.info("Пользователь {} добавил лайк на фильм {}", userId, filmId);
        } catch (Exception e) {
            log.error("Ошибка добавления лайка", e);
        }
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {
        final String sql = "delete from film_likes where  film_id= ? and user_id = ?";
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }

    @Override
    public Collection<Like> getLikesFilmId(Long filmId) {
        final String sql = "select * from film_likes where film_id = ?";
        return jdbcTemplate.query(sql, new LikeMapper(), filmId);
    }


}
