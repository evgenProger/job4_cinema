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
import ru.job4j.cinema.model.Session;
import ru.job4j.cinema.service.SessionService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

@ThreadSafe
@Controller
public class IndexControl {
    private final SessionService sessionService;

    public IndexControl(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/index")
    public String sessions(Model model) {
        model.addAttribute("sessions", sessionService.findAll());
        return "index";
    }

    @PostMapping("/createSession")
    public String createSession(@ModelAttribute Session session,
                                  @RequestParam("file") MultipartFile file) throws IOException {
        sessionService.add(session);
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
}
