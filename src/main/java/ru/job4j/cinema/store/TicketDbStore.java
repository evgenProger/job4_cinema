package ru.job4j.cinema.store;


import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.cinema.model.Session;
import ru.job4j.cinema.model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketDbStore {
    private static final String selectAll = "select * from ticket";
    private static final String insertTicket  = "insert into ticket (row, cell) values (?, ?)";
    private static final String findById = "select * from ticket where id = ? ";
    private static final String update = "update sessions set name = ? where id = ?";
    private static final String delete = "delete from sessions where id = ?";
    private final BasicDataSource pool;
    private static final Logger LOG = LoggerFactory.getLogger(Session.class.getName());

    public TicketDbStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public List<Ticket> findAll() {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(selectAll)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    tickets.add((createTicket(it)));
                }
            }
        } catch (Exception e) {
            LOG.error("Error", e);
        }
        return tickets;
    }

    public Ticket add(Ticket ticket) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(insertTicket,
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, ticket.getRow());
            ps.setInt(2, ticket.getCell());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    ticket.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Error", e);
        }
        return ticket;
    }

    public Ticket findById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(findById)
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return createTicket(it);
                }
            }
        } catch (Exception e) {
            LOG.error("Error", e);
        }
        return null;
    }

    private  Ticket createTicket(ResultSet it) throws SQLException {
        return new Ticket(it.getInt("id"),
                it.getInt("row"),
                it.getInt("cell"));

    }
}
