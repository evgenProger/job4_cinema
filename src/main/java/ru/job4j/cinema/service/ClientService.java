package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Client;
import ru.job4j.cinema.store.ClientDbStore;

import java.util.Optional;

@ThreadSafe
@Service
public class ClientService {
    private final ClientDbStore clientDbStore;

    public ClientService(ClientDbStore clientDbStore) {
        this.clientDbStore = clientDbStore;
    }

    public Optional<Client> findUserByEmail(String email) {
        return clientDbStore.findUserByEmail(email);
    }

    public Optional<Client> add(Client client) {
        return clientDbStore.add(client);
    }

    public Optional<Client> findByEmailAndPhone(String email, String phone) {
        return clientDbStore.findUserByEmailAndPhone(email, phone);
    }

}
