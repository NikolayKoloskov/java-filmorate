package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.reviewLike.ReviewLikeStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service("reviewService")
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final ReviewLikeStorage reviewLikeStorage;

    public Collection<Review> getAll(int filmId, int count) {
        return reviewStorage.getAll(filmId, count);
    }

    public Review create(Review review) {
        return reviewStorage.create(review);
    }

    public void remove(Long id) {
        reviewStorage.remove(id);
    }

    public Review update(Review review) {
        return reviewStorage.update(review);
    }

    public Review getById(Long id) {
        Optional<Review> review = reviewStorage.getById(id);
        if (review.isEmpty()) {
            throw new NotFoundException("отзыв id = " + id);
        }

        return review.get();
    }
}