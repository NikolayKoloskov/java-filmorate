package ru.yandex.practicum.filmorate.storage.reviewLike;

import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewLikeStorage {

    Review addLike(int reviewId, int userId);

    Review addDislike(int reviewId, int userId);

    Review removeLike(int reviewId, int userId);

    Review removeDislike(int reviewId, int userId);
}
