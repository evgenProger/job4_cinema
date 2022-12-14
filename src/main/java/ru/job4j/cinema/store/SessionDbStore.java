package ru.job4j.cinema.store;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ThreadSafe
@Repository
public class SessionDbStore {
    private static final String selectAll = "select * from session";
    private static final String insertSession  = "insert into session (name) values (?)";
    private static final String findById = "select * from session where id = ? ";
    private static final String update = "update session set name = ? where id = ?";
    private static final String delete = "delete from session where id = ?";
    private final BasicDataSource pool;
    private static final Logger LOG = LoggerFactory.getLogger(Session.class.getName());

    public SessionDbStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public List<Session> findAll() {
        List<Session> sessions = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(selectAll)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    sessions.add(createSession(it));
                }
            }
        } catch (Exception e) {
            LOG.error("Error", e);
        }
        return sessions;
    }

    public Session add(Session session) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(insertSession,
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, session.getName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    session.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Error", e);
        }
        return session;
    }

    public Session findById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(findById)
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return createSession(it);
                }
            }
        } catch (Exception e) {
            LOG.error("Error", e);
        }
        return null;
    }

    public boolean removeSession(int id) {
        boolean result = false;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(delete)) {
            ps.setInt(1, id);
            result = ps.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public int saveSession(Session session) {
        int id;
        if (session.getId() == 0) {
            id =  add(session).getId();
        } else {
            updateSession(session);
            id = session.getId();
        }
        return id;
    }

    private  Session createSession(ResultSet it) throws SQLException {
        return new Session(it.getInt("id"),
                it.getString("name"));
    }

    private boolean updateSession(Session session) {
        boolean result = false;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(update)) {
            ps.setString(1, session.getName());
            ps.setInt(2, session.getId());
            result = ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOG.error("Error", e);
        }
        return result;
    }
}
