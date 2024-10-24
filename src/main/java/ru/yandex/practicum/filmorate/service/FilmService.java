package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.util.Collection;

@Slf4j
@Service("filmService")
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;

    public void addLikeToFilm(Film film, User user) {
        likeStorage.addLike(film.getId(), user.getId());
        Film updatedFilm = filmStorage.getFilmByFilmId(film.getId());
        filmStorage.updateFilm(updatedFilm);
    }

    public boolean removeLike(Long filmId, Long userId) {
        return likeStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmByFilmId(id);
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public String removeFilm(Film film) {
        return filmStorage.removeFilm(film);
    }

    public Collection<Film> getPopularFilmsByGenreAndYear(int count, String genreId, String year) {
        var result = filmStorage.getPopularFilmsByGenreAndYear(count, genreId, year);
        return result;
    }

}