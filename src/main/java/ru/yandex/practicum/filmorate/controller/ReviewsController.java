package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewLikeService;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewsController {

    private final ReviewService reviewsService;
    private final ReviewLikeService reviewLikeService;

    @GetMapping
    public Collection<Review> getAll(@RequestParam(required = false, defaultValue = "0") int filmId, @RequestParam(required = false, defaultValue = "10") int count) {
        return reviewsService.getAll(filmId, count);
    }

    @GetMapping("/{reviewId}")
    public Review getById(@PathVariable Long reviewId) {
        return reviewsService.getById(reviewId);
    }

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return reviewsService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewsService.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reviewsService.remove(id);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public Review addLike(@PathVariable int reviewId, @PathVariable int userId) {
        return reviewLikeService.addLike(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public Review addDislike(@PathVariable int reviewId, @PathVariable int userId) {
        return reviewLikeService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public Review removeLike(@PathVariable int reviewId, @PathVariable int userId) {
        return reviewLikeService.removeLike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public Review removeDislike(@PathVariable int reviewId, @PathVariable int userId) {
        return reviewLikeService.removeDislike(reviewId, userId);
    }
}
