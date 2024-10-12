package ru.yandex.practicum.filmorate.storage.friendRequests;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendRequestsDbStorage implements FriendRequestsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Long userId, Long friendId) {
        try {
            final String sql = "INSERT INTO friend_requests (user_id, friend_id) VALUES (?, ?)";

            jdbcTemplate.update(sql, userId, friendId);
        } catch (Exception e) {
            log.error("Ошибка добавления запроса отправки дружбы: {}", e.getMessage());
        }

    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {

        final String sql = "DELETE FROM friend_requests WHERE user_id = ? AND friend_id = ?";
        try {
            jdbcTemplate.update(sql, userId, friendId);
            return true;
        } catch (Exception e) {
            log.error("Пользователи не в друзьях или удаление не удалось");
            return false;
        }
    }


}
