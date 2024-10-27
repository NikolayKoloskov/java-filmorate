package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.friendRequests.FriendRequestsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service("userService")
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FriendRequestsStorage friendRequestsStorage;
    private final EventStorage eventStorage;

    public User createUser(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        eventStorage.eventAddUser(user);
        return userStorage.createUser(user);
    }

    public void createFriend(Long userId, Long friendId) {
        friendRequestsStorage.addFriend(userId, getUserById(friendId).getId());

    }

    public Collection<User> getUserFriends(Long userId) {
        return userStorage.getUserFriends(userId);
    }

    public void removeFriend(User userFrom, User userTo) {
        friendRequestsStorage.deleteFriend(userFrom.getId(), userTo.getId());
    }

    public Collection<User> getMutualFriends(User userFrom, User userTo) {
        return userStorage.getMutualFriends(userFrom, userTo);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public User updateUser(User user) {
        eventStorage.eventUpdateUser(user);
        return userStorage.updateUser(user);
    }

    public String removeUser(User user) {
        eventStorage.eventDeleteUser(user);
        return userStorage.removeUser(user);
    }

    public Collection<Event> getEvent() {
    return eventStorage.getEvents();
    }
}