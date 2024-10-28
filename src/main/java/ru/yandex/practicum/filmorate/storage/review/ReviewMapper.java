package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {

        Integer valueObj = rs.getInt("useful");
        int useful = valueObj != null ? valueObj : 0;
        boolean isPositive;
        if (useful != 0) {
            isPositive = useful >= 0;
        } else {
            isPositive = rs.getBoolean("is_positive");
        }

        return new Review()
                .toBuilder()
                .reviewId(rs.getLong("id"))
                .content(rs.getString("content"))
                .isPositive(isPositive)
                .userId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id"))
                .useful(useful)
                .build();
    }
}
