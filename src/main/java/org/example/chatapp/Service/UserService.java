package org.example.chatapp.Service;

import org.example.chatapp.Model.JoinedRoom;
import org.example.chatapp.Model.Room;
import org.example.chatapp.Model.User;
import org.example.chatapp.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

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


}
