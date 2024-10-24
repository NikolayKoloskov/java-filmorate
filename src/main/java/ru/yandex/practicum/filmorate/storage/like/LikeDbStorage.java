package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.film.FilmMapper;

import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long filmId, Long userId) {

        final String sql = "insert into film_likes (user_id, film_id) values (?, ?)";
        final String sql2 = "update films set rate = rate + 1 where id = ?";
        try {
            jdbcTemplate.update(sql, userId, filmId);
            jdbcTemplate.update(sql2, filmId);
            log.info("Пользователь {} добавил лайк на фильм {}", userId, filmId);
        } catch (Exception e) {
            log.error("Ошибка добавления лайка", e);
        }
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {
        final String sql = "delete from film_likes where  film_id= ? and user_id = ?";
        final String sql2 = "update films set rate = rate - 1 where id = ?";
        return ((jdbcTemplate.update(sql, filmId, userId) > 0) && (jdbcTemplate.update(sql2, filmId) > 0));
    }

    @Override
    public Collection<Like> getLikesFilmId(Long filmId) {
        final String sql = "select * from film_likes where film_id = ?";
        return jdbcTemplate.query(sql, new LikeMapper(), filmId);
    }


    @Override
    public Collection<Film> getLikedFilmsByUserId(Long userId) {
        String sql = "select films.*, mpa.id as mpa_id, mpa.name as mpa_name " +
                "from film_likes " +
                "join films on film_likes.film_id = films.id " +
                "left join mpa on films.mpa_rating = mpa.id " +
                "where film_likes.user_id = ?";

        return jdbcTemplate.query(sql, new FilmMapper(), userId);
    }
}
