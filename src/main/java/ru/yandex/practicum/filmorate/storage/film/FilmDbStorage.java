package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.filmDirector.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.storage.filmGenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private static final String FILMS_MPA_SQL =
            "select f.*, m.id as mpa_id, m.name as mpa_name from films f left join mpa m on f.mpa_rating = m.id";

    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreStorage filmGenreStorage;
    private final MpaStorage mpaStorage;
    private final LikeDbStorage likeDbStorage;
    private final FilmDirectorStorage filmDirectorStorage;

    @Override
    public Film addFilm(Film film) {
        final String sql = "insert into films (name, release_date, description, duration, mpa_rating, rate) " +
                "values (?, ?, ?, ?, ?, ?)";

        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
                preparedStatement.setString(1, film.getName());
                preparedStatement.setObject(2, film.getReleaseDate());
                preparedStatement.setString(3, film.getDescription());
                preparedStatement.setInt(4, film.getDuration());
                preparedStatement.setInt(5, film.getMpa().getId());
                preparedStatement.setInt(6, film.getRate());
                return preparedStatement;
            }, generatedKeyHolder);
        } catch (DataAccessException e) {
            throw new DataException("Что-то пошло не так при добавлении фильма");
        }


        long filmId = Objects.requireNonNull(generatedKeyHolder.getKey()).longValue();

        film.setId(filmId);
        log.info("Добавили фильм: {}", film);

        return addFields(film);
    }

    @Override
    public String removeFilm(Film film) {
        final String sql = "delete from films where id = ?";
        final String sqlGenre = "delete from film_genres where film_id = ?";
        final String sqlLike = "delete from film_likes where film_id = ?";
        final String sqlDirector = "delete from film_directors where film_id = ?";

        try {
            jdbcTemplate.update(sql, film.getId());
            jdbcTemplate.update(sqlGenre, film.getId());
            jdbcTemplate.update(sqlLike, film.getId());
            jdbcTemplate.update(sqlDirector, film.getId());
        } catch (DataAccessException e) {
            throw new DataException("Что-то пошло не так при удалении фильма" + e.getMessage());
        }
        return "Фильм с ID " + film.getId() + " удален";
    }

    @Override
    public Film updateFilm(Film film) {
        final String sql = "update films set name = ?, release_date = ?, description = ?, duration = ?, " +
                "mpa_rating = ?, rate = ? where id = ?";
        filmGenreStorage.deleteAllFilmGenresByFilmId(film.getId());
        int result = jdbcTemplate.update(sql,
                film.getName(),
                film.getReleaseDate(),
                film.getDescription(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getRate(),
                film.getId()
        );
        if (result == 0) {
            throw new NotFoundException("Что-то пошло не так при обновлении фильма");
        }
        return addFields(film);
    }

    @Override
    public Collection<Film> getAllFilms() {
        Collection<Film> films = jdbcTemplate.query(FILMS_MPA_SQL, new FilmMapper());
        return setFilmGenres(films);
    }

    @Override
    public Film getFilmByFilmId(Long id) {
        List<Film> films = jdbcTemplate.query(FILMS_MPA_SQL.concat(" where f.id = ?"), new FilmMapper(), id);
        if (!films.isEmpty()) {
            Collection<Genre> filmGenres = filmGenreStorage.getAllFilmGenresByFilmId(id);
            return films.getFirst().toBuilder().genres(filmGenres).build();
        }

        return null;
    }

    @Override
    public Collection<Film> getDirectorFilms(Integer directorId, String sortBy) {
        String yearOrderSql = "SELECT f.*, " +
                "m.id as mpa_id, m.name as mpa_name " +
                "FROM film_directors fd " +
                "join films f on f.id = fd.film_id " +
                "join mpa m on f.mpa_rating = m.id " +
                "where director_id = ? " +
                "order by year(f.release_date);";

        String likesOrderSql = "select f.*," +
                "  m.id as mpa_id, m.name as mpa_name, " +
                "(select count(*) from film_likes where fd.film_id = film_likes.film_id) as likes " +
                "from film_directors fd " +
                "join films f on f.id = fd.film_id  " +
                "join mpa m on f.mpa_rating = m.id " +
                "where director_id = ? " +
                "order by likes desc;";

        Collection<Film> films = jdbcTemplate.query(
                sortBy.equals("likes") ? likesOrderSql : yearOrderSql,
                new FilmMapper(),
                directorId
        );

        if (films.isEmpty()) {
            return Collections.emptyList();
        }

        return setFilmGenres(films);
    }

    private Film addFields(Film film) {
        long filmId = film.getId();
        int mpaId = film.getMpa().getId();
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> filmGenreStorage.addFilmGenre(filmId, genre.getId()));
        }
        Collection<Genre> filmGenres = filmGenreStorage.getAllFilmGenresByFilmId(film.getId());
        Mpa filmMpa = mpaStorage.getMpaById(mpaId);
        Collection<Like> filmLikes = likeDbStorage.getLikesFilmId(filmId);
        filmDirectorStorage.setFilmDirectors(film.getDirectors(), filmId);
        Collection<Director> directors = filmDirectorStorage.getFilmDirectors(filmId);
        return film.toBuilder().mpa(filmMpa).genres(filmGenres).likes(filmLikes).directors(directors).build();
    }

    private Collection<Film> setFilmGenres(Collection<Film> films) {
        Map<Long, Collection<Genre>> filmGenresMap = filmGenreStorage.getAllFilmGenres(films);
        Map<Long, Collection<Director>> filmDirectors = filmDirectorStorage.getFilmDirectors(films);

        films.forEach(film -> {
            Long filmId = film.getId();
            film.setGenres(filmGenresMap.getOrDefault(filmId, new ArrayList<>()));
            film.setLikes(likeDbStorage.getLikesFilmId(filmId));
            film.setDirectors(filmDirectors.getOrDefault(filmId, new ArrayList<>()));
        });

        return films;
    }
}

