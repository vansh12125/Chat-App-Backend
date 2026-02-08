package org.example.chatapp.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class JoinedRoom {

    private String roomId;
    private String roomName;

    public JoinedRoom() {}

    public JoinedRoom(String roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }

}
