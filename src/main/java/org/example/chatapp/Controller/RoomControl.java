package org.example.chatapp.Controller;

import org.example.chatapp.Model.Room;
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

    public RoomControl(RoomService service, MessageService messageService) {
        this.service = service;
        this.messageService = messageService;
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
        if (room == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @PostMapping("/room")
    public ResponseEntity<?> createRoom(@RequestBody Map<String, String> body) {
        String roomName = body.get("roomName");
        String createdBy=body.get("createdBy");
        if (roomName == null || roomName.isBlank()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Room room = new Room();
        room.setRoomId("chat-" + UUID.randomUUID().toString().substring(0, 6));
        room.setRoomName(roomName);
        room.setCreatedBy(createdBy);
        Room savedRoom = service.createRoom(room);
        return new ResponseEntity<>(savedRoom, HttpStatus.CREATED);
    }

    @DeleteMapping("/room/{id}")
    public ResponseEntity<?> deleteRoomById(@PathVariable String id) {
        if (service.deleteRoomById(id)) {
            messageService.deleteMessagesByRoom(id);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
