package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashMap;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class User {
    private int id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name = "";
    @NotNull
    private LocalDate birthday;
    private HashMap<Integer,Boolean> friends;

}
