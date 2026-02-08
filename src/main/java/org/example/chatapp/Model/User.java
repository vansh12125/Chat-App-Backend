package org.example.chatapp.Model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
@ToString
public class User {

    @Id
    private String id;

    private String username;

    private String password;

    private List<JoinedRoom> roomsJoined = new ArrayList<>();
}
