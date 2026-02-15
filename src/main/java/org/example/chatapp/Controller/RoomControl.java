package org.example.chatapp.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.chatapp.Model.Room;
import org.example.chatapp.Model.User;
import org.example.chatapp.Repository.UserRepository;
import org.example.chatapp.Service.MessageService;
import org.example.chatapp.Service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/rooms")
public class RoomControl {
    private final RoomService service;
    private final MessageService messageService;
    private final UserRepository userService;

    public RoomControl(RoomService service, MessageService messageService, UserRepository userService) {
        this.service = service;
        this.messageService = messageService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getAllRooms() {
        List<Room> rooms = service.getAllRooms();
        if (rooms == null || rooms.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/room/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable String id) {
        Room room = service.getRoomById(id);
        User user = userService.findById(room.getCreatedByUserId()).orElse(null);
        if (room == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (user != null) {
            room.setCreatedByUsername(user.getUsername());
            return new ResponseEntity<>(room, HttpStatus.OK);
        }
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @PostMapping("/room")
    public ResponseEntity<?> createRoom(@RequestBody Map<String, String> body) {
        String roomName = body.get("roomName");
        String createdBy = body.get("createdBy");
        String username = body.get("username");
        if (roomName == null || roomName.isBlank()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Room room = new Room();
        room.setRoomId(UUID.randomUUID().toString().substring(0, 6));
        room.setRoomName(roomName);
        room.setCreatedByUserId(createdBy);
        room.setCreatedByUsername(username);
        Room savedRoom = service.createRoom(room);
        return new ResponseEntity<>(savedRoom, HttpStatus.CREATED);
    }

    @DeleteMapping("/room/{id}")
    public ResponseEntity<?> deleteRoomById(
            @PathVariable String id,
            HttpServletRequest request
    ) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Room room = service.getRoomById(id);
        if (room == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!room.getCreatedByUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to delete this room");
        }
        service.deleteRoomById(id);
        messageService.deleteMessagesByRoom(id);
        return ResponseEntity.ok("Room deleted");
    }

}
