package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private static final String USER_SQL = "select * from users";
    private final JdbcTemplate jdbcTemplate;


    @Override
    public User createUser(User user) {
        final String sql = "insert into users (name, login, birthday, email) values (?, ?, ?, ?)";
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    sql,
                    new String[]{"id"}
            );
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setObject(3, user.getBirthday());
            preparedStatement.setString(4, user.getEmail());

            return preparedStatement;
        }, generatedKeyHolder);

        int userId = Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
        user.setId(userId);
        return user;
    }

    @Override
    public String removeUser(User user) {
        final String sql = "delete from users where id = ?";
        final String sqlFilms = "delete from film_likes where user_id = ?";
        final String sqlFriends = "delete from friend_requests where user_id = ?";

        int result;

        try {
            result = jdbcTemplate.update(sql, user.getId());
            jdbcTemplate.update(sqlFilms, user.getId());
            jdbcTemplate.update(sqlFriends, user.getId());
        } catch (DataAccessException e) {
            throw new DataException("Ошибка при удалении пользователя: " + e.getMessage());
        }
        if (result > 0) {
            return "{ \"message\": \"Пользователь удален\"}";
        } else {
            return "{\"message\": \"Пользователь не найден\"}";
        }
    }

    @Override
    public User updateUser(User user) {
        final String sql = "update users set name = ?, login = ?, birthday = ?, email = ? where id = ?";

        try {
            if (jdbcTemplate.update(
                    sql,
                    user.getName(), user.getLogin(), user.getBirthday(), user.getEmail(), user.getId()
            ) > 0) {
                return user;
            } else {
                throw new NotFoundException("Не нашли пользователя " + user.getId());
            }
        } catch (DataAccessException e) {
            throw new DataException("Ошибка при обновлении пользователя");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return jdbcTemplate.query(USER_SQL, new UserMapper());
    }

    @Override
    public User getUserById(Long id) {
        try {
            return jdbcTemplate.queryForObject(USER_SQL.concat(" where id = ?"), new UserMapper(), id);
        } catch (Exception e) {
            throw new NotFoundException("Не нашли пользователя с id=" + id);
        }
    }

    @Override
    public Collection<User> getMutualFriends(User userFrom, User userTo) {
        final String sql = "select * from users where id in (select friend_id from users u join friend_requests fr on " +
                "u.id = fr.user_id where u.id = ?) and id in (select friend_id from users u join friend_requests fr on " +
                "u.id = fr.user_id where u.id = ?)";
        return jdbcTemplate.query(sql, new UserMapper(), userFrom.getId(), userTo.getId());
    }

    @Override
    public Collection<User> getUserFriends(Long userId) {
        getUserById(userId);
        final String sql = "select * from users where id in" +
                " (select fr.friend_id from users u join friend_requests fr on u.id = fr.user_id where u.id = ?)";
        return jdbcTemplate.query(sql, new UserMapper(), userId);
    }
}
