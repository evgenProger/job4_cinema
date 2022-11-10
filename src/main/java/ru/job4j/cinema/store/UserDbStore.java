package ru.job4j.cinema.store;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ThreadSafe
@Repository
public class UserDbStore {
    private static final String findUserByEmail = "SELECT * FROM  users where email like ?";
    private static final String insertUsers = "INSERT INTO users (name, email, password)"
            + " VALUES (?, ?, ?)";
    private static final String selectAll = "SELECT * FROM users";
    private static final String findUserByEmailAndPwd = "SELECT * FROM  users where email like ? and password like ?";
    private final BasicDataSource pool;
    private static final Logger LOG = LoggerFactory.getLogger(User.class.getName());

    public UserDbStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Optional<User> findUserByEmail(String email) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(findUserByEmail)
        ) {
            ps.setString(1, email);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return createUser(it);
                }
            }
        } catch (Exception e) {
            LOG.error("Error", e);

        }
        return Optional.empty();
    }

    public Optional<User> findUserByEmailAndPhone(String email, String phone) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(findUserByEmailAndPwd)
        ) {
            ps.setString(1, email);
            ps.setString(2, phone);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return createUser(it);
                }
            }
        } catch (Exception e) {
            LOG.error("Error", e);
        }
        return Optional.empty();
    }

    public Optional<User> add(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(insertUsers,
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                    return Optional.of(user);
                }
            }
        } catch (Exception e) {
            LOG.error("Error", e);
        }
        return Optional.empty();
    }

    public Collection<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(selectAll)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    if (createUser(it).isPresent()) {
                        users.add(createUser(it).get());
                    }
                }
            }
        } catch (SQLException throwables) {
            LOG.error("Error", throwables);
        }
        return users;
    }

    private  Optional<User> createUser(ResultSet it) throws SQLException {
        return Optional.of(new User(it.getInt("id"),
                it.getString("name"),
                it.getString("email"),
                it.getString("phone")));
    }
}



