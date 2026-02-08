package org.example.chatapp.Model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "messages")
@Data
@NoArgsConstructor
public class Message {

    @Id
    private String id;

    @NotBlank
    private String content;

    @NotBlank
    private String sender;

    @NotBlank
    private String roomId;

    @NotNull
    private LocalDateTime timeStamp = LocalDateTime.now();
}
