package ru.yandex.practicum.filmorate.storage.reviewLike;

import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.util.List;

public interface ReviewLikeStorage {

    void addLike(int reviewId, int userId);

    void addDislike(int reviewId, int userId);

    void removeLike(int reviewId, int userId);

    void removeDislike(int reviewId, int userId);

    List<ReviewLike> getAll();
}
