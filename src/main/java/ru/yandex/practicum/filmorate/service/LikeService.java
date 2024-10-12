package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeStorage likeStorage;

    public void addLike(Long filmId, Long userId) {
        likeStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        likeStorage.removeLike(filmId, userId);
    }
}
