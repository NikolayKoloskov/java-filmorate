package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
    Director createDirector(Director director);

    Optional<Director> updateDirector(Director director);

    Optional<Director> getDirectorById(Integer id);

    Collection<Director> getAllDirectors();

    Boolean deleteDirector(Integer id);

}
