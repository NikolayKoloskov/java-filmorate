package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service("userService")
public class UserService {

    private InMemoryUserStorage userStorage;


    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(User userFrom, User userTo) {
        if (userFrom.getFriends() == null) {
            userFrom.setFriends(new HashSet<>());
        }
        if (userTo.getFriends() == null) {
            userTo.setFriends(new HashSet<>());
        }
        if ((userFrom.getFriends().contains(userTo.getId())) | (userTo.getFriends().contains(userFrom.getId()))) {
            log.error("Пользователи в друзьях");
            throw new DuplicatedDataException("Пользователи уже в друзьях");
        }
        Set<Long> friends1 = userFrom.getFriends();
        Set<Long> Friends2 = userTo.getFriends();
        friends1.add(userTo.getId());
        Friends2.add(userFrom.getId());
        userFrom.setFriends(friends1);
        userTo.setFriends(Friends2);
        userStorage.updateUser(userFrom);
        userStorage.updateUser(userTo);
        log.info("Пользователь {} добавлен в друзья к пользовалю {}", userFrom, userTo);

        return userFrom;
    }

    public User removeFriend(User userFrom, User userTo) {
        if (userFrom.getFriends() == null) {
            log.error("Пользователь не имеет друзей");
            userFrom.setFriends(new HashSet<>());
        }
        if (userTo.getFriends() == null) {
            log.error("Пользователь не имеет друзей");
            userTo.setFriends(new HashSet<>());
        }
        if ((userFrom.getFriends().contains(userTo.getId())) & (userTo.getFriends().contains(userFrom.getId()))) {
            userFrom.getFriends().remove(userTo.getId());
            userTo.getFriends().remove(userFrom.getId());
            userStorage.updateUser(userFrom);
            userStorage.updateUser(userTo);
            log.info("Пользователь {} удален из друзей пользователя {}", userFrom, userTo);
            return userFrom;
        } else {
            log.error("Пользователи не в друзьях");
            throw new NotFoundUserException("Пользователи не в друзьях");
        }
    }

    public Collection<User> getMutualFriends(User userFrom, User userTo) {
        log.info("Общие друщья пользователя {} и {}", userFrom, userTo);
        HashSet<Long> userFriends = (HashSet<Long>) userFrom.getFriends();
        HashSet<Long> otherFriends = (HashSet<Long>) userTo.getFriends();
        HashSet<Long> commonFriends = new HashSet<>(userFriends);
        commonFriends.retainAll(otherFriends);
        log.trace("Общие друзья пользователя {}", commonFriends.retainAll(otherFriends));
        return commonFriends.stream()
                .map(friendId -> userStorage.getUserById(friendId))
                .collect(Collectors.toSet());
    }
}
