package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service("filmService")
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final EventStorage eventStorage;

    public void addLikeToFilm(Film film, User user) {
        likeStorage.addLike(film.getId(), user.getId());
        eventStorage.eventAddLike(film.getId(),user.getId());
        Film updatedFilm = filmStorage.getFilmByFilmId(film.getId());
        filmStorage.updateFilm(updatedFilm);
    }

    public boolean removeLike(Long filmId, Long userId) {
        eventStorage.eventDeleteLike(filmId, userId);
        return likeStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getTopFilmsLimited(int limit) {
        return filmStorage.getAllFilms().stream()
                .filter(film -> film.getRate() > 0)
                .filter(film -> film.getLikes().size() > 0)
                .sorted((f1, f2) -> f2.getRate() - f1.getRate())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmByFilmId(id);
    }

    public Film addFilm(Film film) {
        eventStorage.eventAddFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        eventStorage.eventUpdateFilm(film);
        return filmStorage.updateFilm(film);
    }

    public String removeFilm(Film film) {
        eventStorage.eventDeleteFilm(film);
        return filmStorage.removeFilm(film);
    }
}