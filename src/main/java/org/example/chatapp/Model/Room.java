package org.example.chatapp.Model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
@NoArgsConstructor
@ToString
public class Room {
    @Id
    private String roomId;

    @NotBlank
    private String roomName;

    @NonNull
    private LocalDateTime createdAt=LocalDateTime.now();

    @NonNull
    private String createdByUserId;

    @NonNull
    private String createdByUsername;

}
