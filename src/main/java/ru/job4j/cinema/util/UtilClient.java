package ru.job4j.cinema.util;

import ru.job4j.cinema.model.Client;

import javax.servlet.http.HttpSession;

public class UtilClient {
    private UtilClient() {
    }

    public static Client getUser(HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            client = new Client();
            client.setName("Гость");
        }
        return client;
    }

}
