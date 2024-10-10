package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;
import java.util.Collection;

@Data
@EqualsAndHashCode(exclude = "id")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private long id;

    @NotNull(message = "Email должен быть заполнен")
    @Email(message = "Ошибка валидации email")
    @NotBlank(message = "Email должен быть заполнен")
    private String email;

    @NotNull(message = "Логин должен быть заполнен")
    @NotBlank(message = "Логин должен быть заполнен")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения должна быть заполнена")
    @Past(message = "Ошибка валидации даты рождения, дата должна быть меньше текущей даты")
    private LocalDate birthday;

    private Collection<User> friends;
}
