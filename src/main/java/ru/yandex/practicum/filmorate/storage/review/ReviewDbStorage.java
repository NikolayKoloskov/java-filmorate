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
import java.util.*;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Review> getAll(int filmId, int count) {
        String sql = "select r.id, r.content, r.is_positive, r.film_id, r.user_id, r.useful from reviews r";

        if (filmId > 0) {
            sql += " where r.film_id = ?";
            sql += " order by r.useful desc limit ?";
            return jdbcTemplate.query(sql, new ReviewMapper(), filmId, count);
        } else {
            sql += " group by r.id order by useful desc limit ?";
            return jdbcTemplate.query(sql, new ReviewMapper(), count);
        }
    }

    @Override
    public Optional<Review> getById(Long id) {
        final String sql = "SELECT * FROM reviews where id = ?";
        List<Review> reviewList = jdbcTemplate.query(sql, new ReviewMapper(), id);
        if (reviewList.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(reviewList.getFirst());
    }

    @Override
    public Collection<Review> getByUserIdAndFilmId(Long filmId, Long userId) {
        final String sql = "SELECT * FROM reviews where film_id = ? and user_id = ?";
        Collection<Review> reviewList = jdbcTemplate.query(sql, new ReviewMapper(), filmId, userId);
        if (reviewList.isEmpty()) {
            return Collections.emptyList();
        }
        return reviewList;
    }

    private boolean isReviewExist(int userId, int filmId) {
        final String sql = "SELECT * FROM reviews where user_id = ? and film_id = ?";
        List<Review> reviewList = jdbcTemplate.query(sql, new ReviewMapper(), userId, filmId);
        return !reviewList.isEmpty();
    }

    @Override
    public Review create(Review review) {
        if (this.isReviewExist(review.getUserId(), review.getFilmId())) {
            throw new DataException("отзыв уже существует от пользователя id = " + review.getUserId() + " на фильм id = " + review.getFilmId());
        }

        User user = userStorage.getUserById((long) review.getUserId());
        Film film = filmStorage.getFilmByFilmId((long) review.getFilmId());
        if (film == null) {
            throw new NotFoundException("Не найден фильм с id = " + review.getFilmId());
        }

        final String sql = "insert into reviews (content, is_positive, user_id, film_id, useful) values (?, ?, ?, ?, ?)";
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, review.getContent());
            preparedStatement.setBoolean(2, !Objects.isNull(review.getIsPositive()) && review.getIsPositive());
            preparedStatement.setInt(3, review.getUserId());
            preparedStatement.setInt(4, review.getFilmId());
            preparedStatement.setInt(5, Objects.isNull(review.getUseful()) ? 0 : review.getUseful());

            return preparedStatement;
        }, generatedKeyHolder);

        int reviewId = Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
        review.setReviewId(reviewId);
        Optional<Review> ans = this.getById(review.getReviewId());
        if (ans.isPresent()) {
            return ans.get();
        } else {
            throw new NotFoundException("Не нашли отзыв " + review.getReviewId());
        }
    }

    @Override
    public Review update(Review review) {

        Optional<Review> currentReview = this.getById(review.getReviewId());
        if (currentReview.isEmpty()) {
            throw new NotFoundException("Не нашли отзыв " + review.getReviewId());
        }
        User user = userStorage.getUserById((long) review.getUserId());
        Film film = filmStorage.getFilmByFilmId((long) review.getFilmId());
        if (film == null) {
            throw new NotFoundException("Не найден фильм с id = " + review.getFilmId());
        }

        final String sql = "update reviews set content = ?, is_positive = ?, user_id = ?, film_id = ?, useful = ? where id = ?";

        try {
            if (jdbcTemplate.update(
                    sql,
                    review.getContent(),
                    review.getIsPositive(),
                    currentReview.get().getUserId(),
                    currentReview.get().getFilmId(),
                    Objects.isNull(review.getUseful()) ? currentReview.get().getUseful() : 0,
                    currentReview.get().getReviewId()) > 0) {
                Optional<Review> ans = this.getById(currentReview.get().getReviewId());
                if (ans.isPresent()) {
                    return ans.get();
                } else {
                    throw new NotFoundException("Не нашли отзыв " + review.getReviewId());
                }
            } else {
                throw new NotFoundException("Не нашли отзыв " + review.getReviewId());
            }
        } catch (DataAccessException e) {
            throw new DataException("Ошибка при обновлении отзыва");
        }
    }

    @Override
    public Review updateAllFields(Review review) {
        final String sql = "update reviews " +
                "set content = ?, is_positive = ?, user_id = ?, film_id = ?, useful = ? where id = ?";
        try {
            jdbcTemplate.update(
                    sql,
                    review.getContent(),
                    review.getIsPositive(),
                    review.getUserId(),
                    review.getFilmId(),
                    review.getUseful(),
                    review.getReviewId()
            );
        } catch (DataAccessException e) {
            throw new DataException("Ошибка при обновлении отзыва" + e.getMessage());
        }
        return getById(review.getReviewId()).get();
    }

    @Override
    public void remove(Long id) {
        final String sql = "delete from reviews where id = ?";
        int result = jdbcTemplate.update(sql, id);
        if (result <= 0) {
            throw new NotFoundException("отзыв id = " + id);
        }
    }
}
