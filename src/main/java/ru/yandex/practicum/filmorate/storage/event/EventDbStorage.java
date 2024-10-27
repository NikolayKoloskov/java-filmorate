package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;
    private final EventMaker eventMaker;
    //я у себя в проекте использовал NamedParameterJdbcOperations, очень удобная штука)

    @Override
    public void eventAddFilm(Film film) {
        eventMaker.makeFilmEvent("Film","Add",film);
    }

    @Override
    public void eventUpdateFilm(Film film) {
        eventMaker.makeFilmEvent("Film","Update",film);
    }

    @Override
    public void eventDeleteFilm(Film film) {
        eventMaker.makeFilmEvent("Film","Delete",film);
    }

    @Override
    public void eventAddUser(User user) {
        eventMaker.makeUserEvent("User","Add",user);
    }

    @Override
    public void eventUpdateUser(User user) {
        eventMaker.makeUserEvent("User","Update",user);
    }

    @Override
    public void eventDeleteUser(User user) {
        eventMaker.makeUserEvent("User","Delete",user);
    }

    @Override
    public void eventAddLike(Long film, Long user) {
        eventMaker.makeLikeEvent("Like","Add", film, user);
    }

    @Override
    public void eventDeleteLike(Long film, Long user) {
        eventMaker.makeLikeEvent("Like","Delete",film, user);
    }

    @Override
    public Collection<Event> getEvents() {
        final String sql = "select * from events";
        return jdbcTemplate.query(sql, new EventMapper());
    }
}
