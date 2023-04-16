package ru.yandex.practicum.filmorate.storage.film.Genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component ("dbGenre")
public class DbGenre {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbGenre(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre get(Integer id) {
        String sqlGetAll = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
         List<Genre> genres = jdbcTemplate.query(sqlGetAll, (rs,rowNum) -> getGenre(rs), id);
         if (genres.size() == 1) {
             return genres.get(0);
         } else {
             throw new IllegalArgumentException();
         }
    }

    public List<Genre> getAll() {
        String sqlGetAll = "SELECT * FROM GENRE ";

        return jdbcTemplate.query(sqlGetAll, (rs,rowNum) -> getGenre(rs));
    }

    private Genre getGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("GENRE_ID"),rs.getString("NAME"));
    }
}
