package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Session;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.store.TicketDbStore;

import java.util.Collection;

@ThreadSafe
@Service
public class TicketService {
    private final TicketDbStore store;

    public TicketService(TicketDbStore store) {
        this.store = store;
    }

    public Collection<Ticket> findAll() {
        return store.findAll();
    }

    public Ticket add(Ticket ticket) {
        return store.add(ticket);
    }

    public Ticket findById(int id) {
        return store.findById(id);
    }

    public Ticket findByRowAndCell(int row, int cell) {
        return store.findByRowAndCell(row, cell);
    }
}
