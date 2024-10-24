package ru.yandex.practicum.filmorate.storage.filmDirector;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmDirectorStorage {
    void setFilmDirectors(Collection<Director> directors, Long filmId);

    Collection<Director> getFilmDirectors(Long filmId);

    Map<Long, Collection<Director>> getFilmDirectors(Collection<Film> films);

    void deleteFilmDirectors(Long filmId);
}
