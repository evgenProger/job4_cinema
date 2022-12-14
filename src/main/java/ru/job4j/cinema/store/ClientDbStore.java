package ru.job4j.cinema.store;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.Client;

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
public class ClientDbStore {
    private  static final String findUserByEmail = "SELECT * FROM  client where email like ?";
    private static final String insertUsers = "INSERT INTO client (name, email, phone)"
            + " VALUES (?, ?, ?)";
    private static final String selectAll = "SELECT * FROM client";
    private static final String findUserByEmailAndPhone = "SELECT * FROM  client where email like ? and phone like ?";
    private final BasicDataSource pool;
    private static final Logger LOG = LoggerFactory.getLogger(Client.class.getName());

    public ClientDbStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Optional<Client> findUserByEmail(String email) {
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

    public Optional<Client> findUserByEmailAndPhone(String email, String phone) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(findUserByEmailAndPhone)
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

    public Optional<Client> add(Client client) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(insertUsers,
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, client.getName());
            ps.setString(2, client.getEmail());
            ps.setString(3, client.getPhone());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    client.setId(id.getInt(1));
                    return Optional.of(client);
                }
            }
        } catch (Exception e) {
            LOG.error("Error", e);
        }
        return Optional.empty();
    }

    public Collection<Client> findAllUsers() {
        List<Client> clients = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(selectAll)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    if (createUser(it).isPresent()) {
                        clients.add(createUser(it).get());
                    }
                }
            }
        } catch (SQLException throwables) {
            LOG.error("Error", throwables);
        }
        return clients;
    }

    private  Optional<Client> createUser(ResultSet it) throws SQLException {
        return Optional.of(new Client(it.getInt("id"),
                it.getString("name"),
                it.getString("email"),
                it.getString("phone")));
    }
}



