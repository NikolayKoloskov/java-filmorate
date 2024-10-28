package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {
    Collection<Review> getAll(int filmId, int count);

    Review create(Review review);

    void remove(Long id);

    Review update(Review review);

    Optional<Review> getById(Long id);
}
