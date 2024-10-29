package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.reviewLike.ReviewLikeStorage;

@Slf4j
@Service("reviewLikeService")
@RequiredArgsConstructor
public class ReviewLikeService {
    private final ReviewLikeStorage reviewLikeStorage;

    public Review addLike(int reviewId, int userId) {
        return reviewLikeStorage.addLike(reviewId, userId);
    }

    public Review addDislike(int reviewId, int userId) {
        return reviewLikeStorage.addDislike(reviewId, userId);
    }

    public Review removeLike(int reviewId, int userId) {
        return reviewLikeStorage.removeLike(reviewId, userId);
    }

    public Review removeDislike(int reviewId, int userId) {
        return reviewLikeStorage.removeDislike(reviewId, userId);
    }
}