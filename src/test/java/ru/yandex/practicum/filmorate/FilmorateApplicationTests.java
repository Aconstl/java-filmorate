package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.couchbase.AutoConfigureDataCouchbase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import java.time.LocalDate;
import java.util.List;


@SpringBootTest
@AutoConfigureDataCouchbase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
	private final FilmController filmController;
	private final UserController userController;
/*	@Test
	void contextLoads() {
	}*/

	@Test
	public void testFilm() {
		Film film = filmController.add(Film.builder()
				.name("Кино1")
				.description("описание кино")
				.releaseDate(LocalDate.of(1895, 12, 28))
				.duration(20)
				.mpa(new Mpa(1, "G"))
				.build());

		assertThat(film.toString(), containsString("Кино1"));

		Film film1 = filmController.get(film.getId());
		assertThat(film1.toString(), containsString("Кино1"));

		List<Film> films = filmController.getAll();
		assertThat(films.toString(), containsString("Кино1"));


		Film filmUPD = filmController.update(Film.builder()
				.id(1)
				.name("КиноОбновленное")
				.description("описание кино")
				.releaseDate(LocalDate.of(1895, 12, 28))
				.duration(20)
				.mpa(new Mpa(1, "G"))
				.build());

		assertThat(filmUPD.toString(), containsString("КиноОбновленное"));
	}

	@Test
	public void testUser() {
		User user = userController.add(User.builder()
				.name("Пользователь1")
				.email("mail@mail.ru")
				.login("login1")
				.birthday(LocalDate.of(1895, 12, 28))
				.build());

		assertThat(user.toString(), containsString("Пользователь1"));

		User film1 = userController.get(user.getId());
		assertThat(film1.toString(), containsString("Пользователь1"));

		List<User> users = userController.getAll();
		assertThat(users.toString(), containsString("Пользователь1"));


		User userUPD = userController.update(User.builder()
				.id(1)
				.name("ПользовательОбновленный")
				.email("mail@mail.ru")
				.login("login1")
				.birthday(LocalDate.of(1895, 12, 28))
				.build());

		assertThat(userUPD.toString(), containsString("ПользовательОбновленный"));
	}
}
