package skypro.homework.recommendation.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import skypro.homework.recommendation.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final JdbcTemplate jdbcTemplate;

    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<UserDto> findByUsername(String username) {
        String sql = "SELECT id, first_name, last_name FROM users WHERE username = ?";
        try {
            List<UserDto> result = jdbcTemplate.query(sql, (rs, rowNum) -> new UserDto(
                    UUID.fromString(rs.getString("id")),
                    rs.getString("first_name"),
                    rs.getString("last_name")
            ), username);

            if (result.size() != 1) {
                return Optional.empty();
            }
            return Optional.of(result.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}