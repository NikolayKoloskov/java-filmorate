package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service("reviewService")
@RequiredArgsConstructor
public class ReviewService {

    private final EventService eventService;
    private final ReviewStorage reviewStorage;


    public Collection<Review> getAll(int filmId, int count) {
        return reviewStorage.getAll(filmId, count);
    }

    public Review create(Review review) {
        log.info("Запрос CreateReview: {}", review);
        Review reviewToReturn = reviewStorage.create(review);
        eventService.createEvent(Long.valueOf(reviewToReturn.getUserId()),
                EventType.REVIEW, EventOperation.ADD, reviewToReturn.getReviewId());
        log.info("CreatedReview: {}, timestamp {}", reviewToReturn, System.currentTimeMillis());
        return reviewToReturn;
    }

    public void remove(Long id) {
        log.info("Запрос RemoveReview: {}", id);
        Review review = getById(id);
        log.info("Отзыв на удаление: {}", review);
        reviewStorage.remove(id);
        eventService.createEvent(Long.valueOf(review.getUserId()),
                EventType.REVIEW, EventOperation.REMOVE, review.getReviewId());

    }

    public Review update(Review review) {
        log.info("Запрос UpdateReview: {}", review);
        Review reviewToReturn = reviewStorage.update(review);
        eventService.createEvent(Long.valueOf(reviewToReturn.getUserId()),
                EventType.REVIEW, EventOperation.UPDATE, reviewToReturn.getReviewId());
        log.info("UpdatedReviewReturn: {}", reviewToReturn);
        return reviewToReturn;
    }

    public Review getById(Long id) {
        Optional<Review> review = reviewStorage.getById(id);
        if (review.isEmpty()) {
            throw new NotFoundException("отзыв id = " + id);
        }

        return review.get();
    }
}