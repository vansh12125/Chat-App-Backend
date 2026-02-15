package org.example.chatapp.Service;

import org.example.chatapp.Model.JoinedRoom;
import org.example.chatapp.Model.Room;
import org.example.chatapp.Model.User;
import org.example.chatapp.Repository.UserRepository;
import org.example.chatapp.Utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private static final String PASSWORD_REGEX = "^\\S{6,20}$";
    private static final String USERNAME_REGEX =
            "^(?=.{3,20}$)(?!.*\\.\\.)(?!.*__)[a-zA-Z0-9](?:[a-zA-Z0-9._]*[a-zA-Z0-9])$";

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final RoomService roomService;

    public UserService(UserRepository repo, PasswordEncoder encoder, RoomService roomService) {
        this.repo = repo;
        this.encoder = encoder;
        this.roomService = roomService;
    }


    public User register(String username, String password) {

        if (repo.findByUsername(username).isPresent()) {
            return null;
        }
        if (!username.matches(USERNAME_REGEX)) {
            throw new IllegalArgumentException(
                    "Invalid username format"
            );
        }

        if (!password.matches(PASSWORD_REGEX)) {
            throw new IllegalArgumentException(
                    "Password must be 6–20 characters with no spaces"
            );
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));

        return repo.save(user);
    }


    public User login(String username, String password) {

        User user = repo.findByUsername(username).orElse(null);
        if (user==null||!encoder.matches(password, user.getPassword())) {
            return null;
        }
        return user;
    }

    public User updateJoinedRoom(String username, String roomId) {

        User user = repo.findByUsername(username).orElse(null);
        Room room = roomService.getRoomById(roomId);

        if (user == null || room == null)
            return null;

        List<JoinedRoom> rooms = user.getRoomsJoined();

        boolean alreadyJoined = rooms.stream()
                .anyMatch(r -> r.getRoomId().equals(roomId));

        if (!alreadyJoined) {
            rooms.add(new JoinedRoom(
                    room.getRoomId(),
                    room.getRoomName()
            ));
            repo.save(user);
        }

        return user;
    }

    public Map<String, Object> updateProfile(
            String usernameFromToken,
            Map<String, String> body
    ) {
        User user = repo.findByUsername(usernameFromToken).orElse(null);
        if (user == null) {
            throw new RuntimeException("UNAUTHORIZED");
        }

        boolean updated = false;
        String newUsername = body.get("username");
        if (newUsername != null) {

            if (!newUsername.matches(USERNAME_REGEX)) {
                throw new IllegalArgumentException("Invalid username");
            }
            String normalized = newUsername.toLowerCase();
            if (!normalized.equals(user.getUsername())) {
                if (repo.existsByUsername(normalized)) {
                    throw new IllegalArgumentException("Username already taken");
                }
                user.setUsername(normalized);
                updated = true;
            }
        }
        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");
        if (currentPassword != null || newPassword != null) {
            if (currentPassword == null || newPassword == null) {
                throw new IllegalArgumentException("Current and new password required");
            }
            if (!encoder.matches(currentPassword, user.getPassword())) {
                throw new SecurityException("Current password is incorrect");
            }
            if (!newPassword.matches(PASSWORD_REGEX)) {
                throw new IllegalArgumentException(
                        "Password must be 6–20 characters with no spaces"
                );
            }
            user.setPassword(encoder.encode(newPassword));
            updated = true;
        }
        if (!updated) {
            throw new IllegalArgumentException("Nothing to update");
        }
        repo.save(user);
        String newToken = JwtUtil.generateToken(user.getUsername());
        return Map.of(
                "token", newToken,
                "user", user
        );
    }

    public User removeJoinedRoom(String username, String roomId) {

        User user = repo.findByUsername(username).orElse(null);
        if (user == null) return null;

        List<JoinedRoom> rooms = user.getRoomsJoined();

        boolean removed = rooms.removeIf(
                r -> r.getRoomId().equals(roomId)
        );

        if (removed) {
            repo.save(user);
        }

        return user;
    }

    public void deleteAccount(String usernameFromToken, String password) {

        User user = repo.findByUsername(usernameFromToken)
                .orElseThrow(() -> new RuntimeException("UNAUTHORIZED"));

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (!encoder.matches(password, user.getPassword())) {
            throw new SecurityException("Incorrect password");
        }

        repo.delete(user);
    }
}
