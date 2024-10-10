package ru.yandex.practicum.filmorate.storage.filmGenre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Map;

public interface FilmGenreStorage {
    void addFilmGenre(Long filmId, Integer genreId);

    Collection<Genre> getAllFilmGenresByFilmId(Long filmId);

    void deleteAllFilmGenresByFilmId(Long filmId);

    Map<Long, Collection<Genre>> getAllFilmGenres(Collection<Film> films);
}
