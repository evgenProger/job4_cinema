package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.job4j.cinema.model.Client;
import ru.job4j.cinema.service.ClientService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static ru.job4j.cinema.util.UtilClient.getUser;

@Controller
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/loginPage")
    public String loginPage(Model model, @RequestParam(name = "fail", required = false) Boolean fail) {
        model.addAttribute("fail", fail != null);
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute Client client, HttpServletRequest req) {
        Optional<Client> userDb = clientService.findByEmailAndPhone(
                client.getEmail(), client.getPhone()
        );
        if (userDb.isEmpty()) {
            return "redirect:/loginPage?fail=true";
        }
        HttpSession session = req.getSession();
        session.setAttribute("client", userDb.get());
        return "redirect:/index";
    }

    @PostMapping("/registration")
    public String registration(Model model, @ModelAttribute Client client, HttpServletRequest req) {
        Optional<Client> regUser = clientService.findByEmailAndPhone(client.getEmail(), client.getPhone());
        if (regUser.isPresent()) {
            return "redirect:/registerPage?fail=true";
        }
        clientService.add(client);
        return "redirect:/loginPage";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/loginPage";
    }

    @GetMapping("/registerPage")
    public String registrPage(Model model,  HttpServletRequest req,
                              @RequestParam(name = "fail", required = false) Boolean fail) {
        HttpSession session = req.getSession();
        model.addAttribute("client", getUser(session));
        model.addAttribute("fail", fail != null);
        model.addAttribute("message", "Пользователь с такой почтой уже существует");
        return "registration";
    }

}
