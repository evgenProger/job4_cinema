package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.TicketService;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Set;

@Controller
public class CinemaSeatController {
    private final TicketService ticketService;

    public CinemaSeatController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @RequestMapping("/selectSeat/{row}/{seat}")
    public String selected(Model model,
                           @PathVariable("row") int row,
                           @PathVariable("seat") int seat,
                           HttpSession req) {

        Ticket ticket = ticketService.findByRowAndCell(row, seat);
        if (ticket == null) {
            ticket = new Ticket(row, seat);
            ticket.setSessionId((Integer) req.getAttribute("id"));
            Set<Ticket> bookedTickets = (Set<Ticket>) req.getAttribute("bookedTickets");
            if (bookedTickets == null) {
                bookedTickets = new HashSet<>();
            }

            if (!bookedTickets.add(ticket)) {
                bookedTickets.remove(ticket);
            }
            req.setAttribute("bookedTickets", bookedTickets);
            model.addAttribute("bookedTickets", bookedTickets);
        }
        model.addAttribute("tickets", ticketService.findAll());
        model.addAttribute("message", "Это место уже занято");
        return "cinemaSeat";
    }
}