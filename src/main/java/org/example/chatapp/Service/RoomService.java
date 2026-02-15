package org.example.chatapp.Service;

import org.example.chatapp.Model.Room;
import org.example.chatapp.Repository.RoomRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {
    private final RoomRepo repo;

    public RoomService(RoomRepo repo) {
        this.repo = repo;
    }

    public Room createRoom(Room room){
        return repo.save(room);
    }

    public Room getRoomById(String id){
        return repo.findById(id).orElse(null);
    }

    public void deleteRoomById(String id){
        Room room=getRoomById(id);
        if(room!=null){
            repo.delete(room);
        }
    }

    public List<Room> getAllRooms() {
        return repo.findAll();
    }
}
