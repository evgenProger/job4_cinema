package ru.job4j.cinema.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.SessionService;
import ru.job4j.cinema.service.TicketService;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Set;

@Controller
public class BoughtSessionController {
    @Autowired private SessionService sessionService;
    @Autowired private TicketService ticketService;

    @GetMapping("/boughtSession")
    public String toByeTickests(Model model, HttpSession session) {
        int id = (int) session.getAttribute("id");
        model.addAttribute("bookedTickets", session.getAttribute("bookedTickets"));
        model.addAttribute("move", sessionService.findById(id));

        return "boughtSession";
    }

    @GetMapping("/checkSeat")
    public String checkSeat(Model model, HttpSession session) {
        Set<Ticket> bookedTickets = (Set<Ticket>) session.getAttribute("bookedTickets");
        int idSession = (int) session.getAttribute("id");
        for (Ticket ticket : bookedTickets) {
            int row = ticket.getRow();
            int cell = ticket.getCell();
            Ticket boughtTicket = ticketService.findByRowAndCellAndSessionId(row, cell, idSession);
            if (boughtTicket != null) {
                session.setAttribute("bookedTickets", new HashSet<Ticket>());
                return "redirect:/cinemaSeat/" + idSession + "?fail=true";
            }
        }
        bookedTickets.forEach(t -> ticketService.add(t));
        session.setAttribute("bookedTickets", new HashSet<Ticket>());
        return "redirect:/cinemaSeat/" + idSession;
    }
}
