package ru.yandex.practicum.filmorate.storage.friendRequests;

public interface FriendRequestsStorage {
    void addFriend(Long userId, Long friendId);

    boolean deleteFriend(Long userId, Long friendId);
}
