package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Review> getAll(int filmId, int count) {
        String sql = "select r.id, r.content, r.film_id, r.user_id, SUM(rl.reaction) as useful from reviews r inner join review_likes rl on r.id = rl.review_id";

        if (filmId > 0) {
            sql += " where r.film_id = ?";
            sql += " group by r.id order by useful limit ?";
            return jdbcTemplate.query(sql, new ReviewMapper(), filmId, count);
        } else {
            sql += " group by r.id order by useful limit ?";
            return jdbcTemplate.query(sql, new ReviewMapper(), count);
        }
    }

    /*@Override
    public Optional<Review> getById(int reviewId) {
        String sql = "select * from reviews r where r.id = ?";
        List<Review> reviewList = jdbcTemplate.query(sql, new ReviewMapper(), reviewId);
        if (reviewList.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(reviewList.getFirst());
    }*/

    @Override
    public Review create(Review review) {
        User user = userStorage.getUserById((long) review.getUserId());
        Film film = filmStorage.getFilmByFilmId((long) review.getFilmId());
        if (film == null) {
            throw new NotFoundException("Не найден фильм с id = " + review.getFilmId());
        }

        final String sql = "insert into reviews (content, user_id, film_id) values (?, ?, ?)";
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    sql,
                    new String[]{"id"}
            );
            preparedStatement.setString(1, review.getContent());
            preparedStatement.setInt(2, review.getUserId());
            preparedStatement.setInt(3, review.getFilmId());

            return preparedStatement;
        }, generatedKeyHolder);

        int reviewId = Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
        review.setReviewId(reviewId);
        return review;
    }

    @Override
    public Review update(Review review) {
        User user = userStorage.getUserById((long) review.getUserId());
        Film film = filmStorage.getFilmByFilmId((long) review.getFilmId());
        if (film == null) {
            throw new NotFoundException("Не найден фильм с id = " + review.getFilmId());
        }

        final String sql = "update reviews set content = ?, user_id = ?, film_id = ? where id = ?";

        try {
            if (jdbcTemplate.update(
                    sql,
                    review.getContent(), review.getUserId(), review.getFilmId(), review.getReviewId()
            ) > 0) {
                return review;
            } else {
                throw new NotFoundException("Не нашли отзыв " + review.getReviewId());
            }
        } catch (DataAccessException e) {
            throw new DataException("Ошибка при обновлении отзыва");
        }
    }

    @Override
    public String remove(Long id) {
        final String sql = "delete from reviews where id = ?";
        int result = jdbcTemplate.update(sql, id);
        if (result > 0) {
            return "Отзыв удален";
        } else {
            return "Отзыв не найден";
        }
    }
}
