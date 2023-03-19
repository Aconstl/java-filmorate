package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @PostMapping
    public User add(@Valid  @RequestBody User user) {
       return userStorage.add(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userStorage.update(user);
    }

    @GetMapping ("/{id}")
    public User get(@PathVariable Integer id){
        return userStorage.get(id);
    }

    @PutMapping ("/{id}/friends/{friendId}")
    public List<Integer> addFriend(@PathVariable Integer id,@PathVariable Integer friendId) {
        return userService.addFriends(id,friendId);
    }

    @DeleteMapping ("/{id}/friends/{friendId}")
    public List<Integer> removeFriend (@PathVariable Integer id,@PathVariable Integer friendId) {
        return userService.removeFriend(id,friendId);
    }

    @GetMapping ("/{id}/friends")
    public List<User> getFriends (@PathVariable Integer id) {
        return userStorage.getFriends(id);
    }

    @GetMapping ("/{id}/friends/common/{otherId}")
    public List<User> getJointFriends (@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getJointFriends(id,otherId);
    }
}