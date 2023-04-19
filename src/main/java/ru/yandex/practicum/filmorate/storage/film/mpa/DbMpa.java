package ru.yandex.practicum.filmorate.storage.film.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("dbMpa")
public class DbMpa {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbMpa(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa get(Integer id) {
        String sqlGetAll = "SELECT * FROM MPA WHERE MPA_ID = ?";
        List<Mpa> mpa = jdbcTemplate.query(sqlGetAll, (rs, rowNum) -> getMpa(rs), id);
        if (mpa.size() == 1) {
            return mpa.get(0);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public List<Mpa> getAll() {
        String sqlGetAll = "SELECT * FROM MPA ";

        return jdbcTemplate.query(sqlGetAll, (rs, rowNum) -> getMpa(rs));
    }

    private Mpa getMpa(ResultSet rs) throws SQLException {
        return new Mpa(rs.getInt("MPA_ID"), rs.getString("NAME"));
    }
}
