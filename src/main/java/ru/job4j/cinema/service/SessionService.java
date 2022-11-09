package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Session;
import ru.job4j.cinema.store.SessionDbStore;

import java.util.Collection;

@ThreadSafe
@Service
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

    public boolean deleteSession(int id) {
        return store.removeSession(id);
    }

}
