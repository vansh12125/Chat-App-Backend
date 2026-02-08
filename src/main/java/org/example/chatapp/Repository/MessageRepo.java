package org.example.chatapp.Repository;

import org.example.chatapp.Model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepo extends MongoRepository<Message, String> {

    Page<Message> findByRoomIdOrderByTimeStampDesc(
            String roomId,
            Pageable pageable
    );

    void deleteByRoomId(String roomId);
}
