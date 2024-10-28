package ru.yandex.practicum.filmorate.storage.reviewLike;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewLikeDbStorage implements ReviewLikeStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final ReviewStorage reviewStorage;

    @Override
    public void addLike(int reviewId, int userId) {
        User user = userStorage.getUserById((long) userId);
        if (reviewStorage.getById((long)reviewId).isEmpty()) {
            throw new NotFoundException("Отзыв id = " + reviewId);
        }

        //this.removeLike(reviewId, userId);
        this.addReaction(1, reviewId, userId);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        User user = userStorage.getUserById((long) userId);
        if (reviewStorage.getById((long)reviewId).isEmpty()) {
            throw new NotFoundException("Отзыв id = " + reviewId);
        }

        //this.removeLike(reviewId, userId);
        this.addReaction(-1, reviewId, userId);
    }

    @Override
    public void removeLike(int reviewId, int userId) {
        User user = userStorage.getUserById((long) userId);
        if (reviewStorage.getById((long)reviewId).isEmpty()) {
            throw new NotFoundException("Отзыв id = " + reviewId);
        }

        if (this.getReaction(reviewId, userId).isPresent()) {
            this.removeReaction(reviewId, userId);
        }
    }

    @Override
    public void removeDislike(int reviewId, int userId) {
        User user = userStorage.getUserById((long) userId);
        if (reviewStorage.getById((long)reviewId).isEmpty()) {
            throw new NotFoundException("Отзыв id = " + reviewId);
        }

        if (this.getReaction(reviewId, userId).isPresent()) {
            this.removeReaction(reviewId, userId);
        }
    }

    public Optional<ReviewLike> getReaction(int reviewId, int userId) {
        User user = userStorage.getUserById((long) userId);
        if (reviewStorage.getById((long)reviewId).isEmpty()) {
            throw new NotFoundException("Отзыв id = " + reviewId);
        }

        try {
            ReviewLike reviewLike = jdbcTemplate.queryForObject("select * from review_likes where review_id = ? and user_id = ?",
                    new ReviewLikeMapper(),
                    reviewId,
                    userId
            );
            return Optional.of(reviewLike);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private void addReaction(int reaction, int reviewId, int userId) {
        User user = userStorage.getUserById((long) userId);
        if (reviewStorage.getById((long)reviewId).isEmpty()) {
            throw new NotFoundException("Отзыв id = " + reviewId);
        }

        final String sql = "insert into review_likes (reaction, review_id, user_id) values (?, ?, ?)";
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    sql,
                    new String[]{"id"}
            );
            preparedStatement.setInt(1, reaction);
            preparedStatement.setInt(2, reviewId);
            preparedStatement.setInt(3, userId);

            return preparedStatement;
        }, generatedKeyHolder);
    }

    private void removeReaction(int reviewId, int userId) {
        User user = userStorage.getUserById((long) userId);
        if (reviewStorage.getById((long)reviewId).isEmpty()) {
            throw new NotFoundException("Отзыв id = " + reviewId);
        }

        final String sql = "delete from review_likes where review_id = ? and user_id = ?";
        int result = jdbcTemplate.update(sql, reviewId, userId);
    }

    @Override
    public List<ReviewLike> getAll() {
        return jdbcTemplate.query("select * from review_likes", new ReviewLikeMapper());
    }
}
