package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface EventStorage {
    void eventAddFilm(Film film);

    void eventUpdateFilm(Film film);

    void eventDeleteFilm(Film film);

    void eventAddUser(User user);

    void eventUpdateUser(User user);

    void eventDeleteUser(User user);

    void eventAddLike(Long film, Long user);

    void eventDeleteLike(Long film, Long user);

    Collection<Event> getEvents();

}


