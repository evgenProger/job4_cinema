package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

//@Controller
//public class UserController {
//    private UserService userService;
//
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @PostMapping("/login")
//    public String login(@ModelAttribute User user, HttpServletRequest req) {
//        Optional<User> userDb = userService.findByEmailAndPhone(
//                user.getEmail(), user.getPhone()
//        );
//        if (userDb.isEmpty()) {
//            return "redirect:/loginPage?fail=true";
//        }
//        HttpSession session = req.getSession();
//        session.setAttribute("user", userDb.get());
//        return "redirect:/index";
//    }
//}
