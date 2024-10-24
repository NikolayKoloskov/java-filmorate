package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorService {
    public static final String NOT_FOUND = "Режиссер с id: '%d' не найден";

    private final DirectorStorage directorStorage;

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director).orElseThrow(() -> new NotFoundException(String.format(
                NOT_FOUND, director.getId())));
    }

    public Director getDirectorById(Integer id) {
        return directorStorage.getDirectorById(id).orElseThrow(() -> new NotFoundException(String.format(
                NOT_FOUND, id)));
    }

    public Collection<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public void deleteDirector(Integer id) {
        if (!directorStorage.deleteDirector(id)) {
            throw new NotFoundException(String.format(
                    NOT_FOUND, id));
        }
    }

}
