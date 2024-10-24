package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director createDirector(Director director) {
        String sqlQuery = "insert into directors (name) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);

        Number key = keyHolder.getKey();
        director.setId(Objects.requireNonNull(key).intValue());
        return director;
    }

    @Override
    public Optional<Director> updateDirector(Director director) {
        String sqlQuery = "update directors set " +
                "name = ? " +
                "where director_id = ?";

        int status = jdbcTemplate.update(
                sqlQuery,
                director.getName(),
                director.getId()
        );

        if (status == 0) {
            return Optional.empty();
        }

        return Optional.of(director);
    }

    @Override
    public Optional<Director> getDirectorById(Integer id) {
        String sqlQuery = "select * " +
                "from directors  " +
                "where director_id = ?;";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, new DirectorMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Director> getAllDirectors() {
        String sqlQuery = "select * " +
                "from directors";

        return jdbcTemplate.query(sqlQuery, new DirectorMapper());
    }

    @Override
    public Boolean deleteDirector(Integer id) {
        String sqlQuery = "delete from directors " +
                "where director_id = ?";

        int status = jdbcTemplate.update(sqlQuery, id);

        return status != 0;
    }
}
