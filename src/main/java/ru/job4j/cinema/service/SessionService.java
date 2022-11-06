package ru.job4j.cinema.service;

import ru.job4j.cinema.model.Session;
import ru.job4j.cinema.store.SessionDbStore;

import java.util.Collection;

public class SessionService {
    private final SessionDbStore store;

    public SessionService(SessionDbStore store) {
        this.store = store;
    }

    public Collection<Session> findAll() {
        return store.findAll();
    }

    public Session add(Session session) {
        return store.add(session);
    }

    public Session findById(int id) {
        return store.findById(id);
    }

    public boolean updateCandidate(Session session) {
        return store.updateSession(session);
    }

}
