package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {
    Collection<Review> getAll(int filmId, int count);

    Review create(Review review);

    String remove(Long id);

    Review update(Review review);

    //Optional<Review> getById(int reviewId);
}
