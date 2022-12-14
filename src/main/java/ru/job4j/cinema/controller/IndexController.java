package ru.job4j.cinema.controller;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cinema.model.Client;
import ru.job4j.cinema.model.Session;
import ru.job4j.cinema.service.SessionService;
import ru.job4j.cinema.service.TicketService;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;

@ThreadSafe
@Controller
public class IndexController {
    private final SessionService sessionService;
    private final TicketService ticketService;

    public IndexController(SessionService sessionService, TicketService ticketService) {
        this.sessionService = sessionService;
        this.ticketService = ticketService;
    }

    @GetMapping("/index")
    public String sessions(Model model, HttpSession session) {
        model.addAttribute("sessions", sessionService.findAll());
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            client = new Client();
            client.setName("Гость");
        }
        model.addAttribute("client", client);
        return "index";
    }

    @GetMapping("/")
    public String index2(Model model) {
        return "redirect:/index";
    }

    @PostMapping("/createSession")
    public String createSession(@ModelAttribute Session session,
                                  @RequestParam("file") MultipartFile file) throws IOException {
        sessionService.add(session);
        createPoster(session, file);
        return "redirect:/index";
    }

    @GetMapping("/poster/{sessionId}")
    public ResponseEntity<Resource> download(@PathVariable("sessionId") Integer sessionId) throws IOException {
        Session session = sessionService.findById(sessionId);
        String photoName = Arrays.stream(Objects.requireNonNull(new File("src/main/resources/posters")
                        .listFiles())).filter(f ->
                        f.getName().split("\\.")[0].equals(String.valueOf(sessionId)))
                .findFirst().get().getName();
        File photo = new File("src/main/resources/posters" + File.separator + photoName);
        byte[] arr = Files.readAllBytes(photo.toPath());
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .contentLength(arr.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new ByteArrayResource(arr));
    }

    @GetMapping("/addSession")
    public String formAddPost(Model model) {
        return "addSession";
    }

    @RequestMapping("/deleteSession/{sessionId}")
    public String deleteSession(@PathVariable("sessionId") Integer sessionId)  {
        Session session = sessionService.findById(sessionId);
        deletePoster(sessionId);
        sessionService.deleteSession(sessionId);
        return "redirect:/index";
    }

    @GetMapping("/formUpdateSession/{SessionId}")
    public String formUpdateCandidate(Model model, @PathVariable("SessionId") int id) {
        model.addAttribute("session", sessionService.findById(id));
        deletePoster(id);
        return "updateSession";
    }

    @PostMapping("/updateSession")
    public String updateCandidate(@ModelAttribute Session session,
                                  @RequestParam("file") MultipartFile file) throws IOException {
        createPoster(session, file);
        sessionService.saveSession(session);
        return "redirect:/index";
    }

    private void deletePoster(@PathVariable Integer sessionId) {
        File file = new File("src/main/resources/posters");
        Arrays.stream(Objects.requireNonNull(file.listFiles()))
                .filter(f -> FilenameUtils.removeExtension(f.getName())
                        .equals(String.valueOf(sessionId)))
                .findFirst().ifPresent(File::delete);
    }

    private void createPoster(@ModelAttribute Session session,
                              @RequestParam("file") MultipartFile file) throws IOException {
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        File photo = new File("src/main/resources/posters"
                + File.separator
                + session.getId()
                + "."
                + ext);
        photo.createNewFile();
        try (FileOutputStream out = new FileOutputStream(photo)) {
            out.write(file.getBytes());
        }
    }

    @GetMapping("/cinemaSeat/{id}")
    public String cinemaSeat(Model model, @PathVariable("id") int id, HttpSession session) {
        model.addAttribute("tickets", ticketService.findAll());
        session.setAttribute("id", id);
        return "cinemaSeat";
    }
}
