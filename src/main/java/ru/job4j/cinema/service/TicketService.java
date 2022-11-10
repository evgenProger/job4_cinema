package ru.job4j.cinema.service;

import ru.job4j.cinema.model.Session;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.store.TicketDbStore;

import java.util.Collection;

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
}
