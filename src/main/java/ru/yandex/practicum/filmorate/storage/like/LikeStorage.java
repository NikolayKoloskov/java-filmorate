package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;

public interface LikeStorage {
    void addLike(Long filmId, Long userId);

    boolean removeLike(Long filmId, Long userId);

    Collection<Like> getLikesFilmId(Long filmId);

    Collection<Film> getLikedFilmsByUserId(Long userId);
}