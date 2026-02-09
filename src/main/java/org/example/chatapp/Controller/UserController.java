package org.example.chatapp.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.chatapp.Model.User;
import org.example.chatapp.Repository.UserRepository;
import org.example.chatapp.Service.UserService;
import org.example.chatapp.Utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService service;
    private final UserRepository userRepo;

    public UserController(UserService service, UserRepository userRepo) {
        this.service = service;
        this.userRepo = userRepo;
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {

        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null ||
                username.isBlank() || password.isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body("Username and password required");
        }

        User user = service.register(username.toLowerCase(), password);

        if (user == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Username already exists");
        }

        return ResponseEntity.ok(user);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

        User user = service.login(
                body.get("username").toLowerCase(),
                body.get("password")
        );

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }

        String token = JwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(
                Map.of(
                        "token", token,
                        "user", user
                )
        );
    }


    @GetMapping("/me")
    public ResponseEntity<?> currentUser(HttpSession session) {

        User user = (User) session.getAttribute("USER");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(user);
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpSession session) {
        User user = (User) session.getAttribute("USER");

        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody Map<String, String> body,
            HttpSession session
    ) {
        User user = (User) session.getAttribute("USER");

        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        String newUsername = body.get("username");

        if (newUsername == null || !newUsername.matches("^[a-zA-Z0-9]{3,20}$")) {
            return ResponseEntity.badRequest().body("Invalid username");
        }

        user.setUsername(newUsername);
        userRepo.save(user);
        session.setAttribute("USER", user);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/join")
    public ResponseEntity<?> updateJoin(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String roomId = body.get("roomId");

        User user = service.updateJoinedRoom(username, roomId);
        return ResponseEntity.ok(user);
    }
}
