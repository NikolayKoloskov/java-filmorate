package ru.yandex.practicum.filmorate.storage.reviewLike;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewLikeDbStorage implements ReviewLikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final ReviewStorage reviewStorage;

    @Override
    public Review addLike(int reviewId, int userId) {
        return this.updateReview(reviewId, userId, 1, false);
    }

    @Override
    public Review addDislike(int reviewId, int userId) {
        return this.updateReview(reviewId, userId, -1, false);
    }

    @Override
    public Review removeLike(int reviewId, int userId) {
        return this.updateReview(reviewId, userId, 1, true);
    }

    @Override
    public Review removeDislike(int reviewId, int userId) {
        return this.updateReview(reviewId, userId, -1, true);
    }

    @Override
    public Optional<ReviewLike> getReaction(int reviewId, int userId) {
        userStorage.getUserById((long) userId);
        if (reviewStorage.getById((long) reviewId).isEmpty()) {
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
        userStorage.getUserById((long) userId);
        if (reviewStorage.getById((long) reviewId).isEmpty()) {
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
        userStorage.getUserById((long) userId);
        if (reviewStorage.getById((long) reviewId).isEmpty()) {
            throw new NotFoundException("Отзыв id = " + reviewId);
        }

        final String sql = "delete from review_likes where review_id = ? and user_id = ?";
        int result = jdbcTemplate.update(sql, reviewId, userId);
    }

    private Review updateReview(int reviewId, int userId, int reaction, boolean remove) {
        //Проверяем пользователя
        userStorage.getUserById((long) userId);

        //Проверяем отзыв
        Optional<Review> reviewOptional = reviewStorage.getById((long) reviewId);
        if (reviewOptional.isEmpty()) {
            throw new NotFoundException("Отзыв id = " + reviewId);
        } else {
            Review review = reviewOptional.get();

            Optional<ReviewLike> reviewLike = this.getReaction(reviewId, userId);
            if (remove) {
                if (reviewLike.isPresent()) {
                    review.setUseful(review.getUseful() - reaction);
                    this.removeReaction(reviewId, userId);
                } else {
                    throw new NotFoundException("Реакция от пользователя id = " + userId + " на отзыв id = " + reviewId);
                }
            } else {
                review.setUseful((review.getUseful() + reaction) == 0 ? +reaction : review.getUseful() + reaction);
                this.removeReaction(reviewId, userId);
                this.addReaction(reaction, reviewId, userId);
            }
            Review toReturn = reviewStorage.updateAllFields(review);

            return toReturn;
        }
    }
}
