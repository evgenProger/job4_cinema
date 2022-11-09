package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CinemaSeatControl {

    @GetMapping("/cinemaSeat")
    public String index2(Model model) {
        return "cinemaSeat";
    }

}
