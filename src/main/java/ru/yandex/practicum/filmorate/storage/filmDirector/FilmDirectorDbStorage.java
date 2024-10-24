package ru.yandex.practicum.filmorate.storage.filmDirector;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorMapper;

import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Component
@AllArgsConstructor
public class FilmDirectorDbStorage implements FilmDirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void setFilmDirectors(Collection<Director> directors, Long filmId) {
        if (Objects.isNull(directors) || directors.isEmpty()) {
            return;
        }

        String sqlQuery = "insert into film_directors(director_id, film_id) " +
                "values ( ?, ? )";

        try {
            jdbcTemplate.batchUpdate(
                    sqlQuery, directors, directors.size(), (PreparedStatement ps, Director director) -> {
                        ps.setInt(1, director.getId());
                        ps.setLong(2, filmId);
                    });
        } catch (DataIntegrityViolationException e) {
            log.error(e.getMessage());
        }

    }

    @Override
    public Collection<Director> getFilmDirectors(Long filmId) {
        String sqlQuery = "select d.* " +
                "from film_directors fd " +
                "join directors d on d.director_id = fd.director_id " +
                "where film_id = ?";

        return jdbcTemplate.query(sqlQuery, new DirectorMapper(), filmId);
    }

    @Override
    public Map<Long, Collection<Director>> getFilmDirectors(Collection<Film> films) {
        Map<Long, Collection<Director>> directorsByFilmId = new HashMap<>();

        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        String sqlQuery = "select fd.film_id, d.* " +
                "from film_directors fd " +
                "join directors d on d.director_id = fd.director_id " +
                "where film_id in (%s);";

        DirectorMapper directorMapper = new DirectorMapper();

        jdbcTemplate.query(
                String.format(sqlQuery, inSql),
                (rs, rowNum) -> {
                    Long filmId = rs.getLong("film_id");
                    Director director = directorMapper.mapRow(rs, rowNum);
                    Collection<Director> directors = directorsByFilmId.getOrDefault(filmId, new ArrayList<>());
                    directors.add(director);

                    return directorsByFilmId.put(
                            filmId,
                            directors
                    );
                },
                films.stream().map(film -> Long.toString(film.getId())).toArray()
        );

        return directorsByFilmId;
    }

    @Override
    public void deleteFilmDirectors(Long filmId) {
        String sqlQuery = "delete from film_directors " +
                "where film_id = ?";

        jdbcTemplate.update(sqlQuery, filmId);
    }
}

