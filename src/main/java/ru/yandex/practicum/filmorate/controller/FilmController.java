package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
class FilmController {

    private FilmService filmService;
    private UserService userService;

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getTopFilmsLimited(@RequestParam(required = false, defaultValue = "10") String count) {
        return filmService.getTopFilmsLimited(Integer.parseInt(count));
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLikeToFilm(filmService.getFilmById(id), userService.getUserById(userId));
        return filmService.getFilmById(id);
    }

    @DeleteMapping
    public String delete(@Valid @RequestBody Film film) {
        return filmService.removeFilm(film);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film unlikeFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(filmService.getFilmById(id).getId(), userService.getUserById(userId).getId());
        log.info("Пользователь {} убрал лайк с фильма {}", userId, id);
        return filmService.getFilmById(id);
    }
}
