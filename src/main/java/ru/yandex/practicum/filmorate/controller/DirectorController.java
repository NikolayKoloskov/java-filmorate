package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Director createDirector(@Valid @RequestBody Director director) {
        log.info("Создается режиссер: {}", director);

        return directorService.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("Обновляется режиссер: {}", director);

        return directorService.updateDirector(director);
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable Integer id) {
        log.info("Получение режиссера по id: {}", id);

        return directorService.getDirectorById(id);
    }

    @GetMapping
    public Collection<Director> getAllDirectors() {
        log.info("Получение всех режиссеров");

        return directorService.getAllDirectors();
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable Integer id) {
        log.info("Удаление режиссера по id: {}", id);

        directorService.deleteDirector(id);
    }
}
