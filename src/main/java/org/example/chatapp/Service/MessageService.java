package org.example.chatapp.Service;

import org.example.chatapp.Model.Message;
import org.example.chatapp.Repository.MessageRepo;
import org.example.chatapp.Utils.CryptoUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class MessageService {

    private final MessageRepo messageRepo;

    public MessageService(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    public Message saveMessage(String roomId, String sender, String content) {
        Message msg = new Message();
        msg.setRoomId(roomId);
        msg.setSender(sender);
        msg.setContent(
                CryptoUtil.encrypt(content)
        );
        return messageRepo.save(msg);
    }

    public List<Message> getMessages(String roomId, int page, int size) {
        List<Message> messages = messageRepo
                .findByRoomIdOrderByTimeStampDesc(
                        roomId,
                        PageRequest.of(page, size)
                )
                .getContent();

        messages.forEach(message -> {
            message.setContent(
                    CryptoUtil.decrypt(message.getContent())
            );
        });
        return messages;

    }

    public void deleteMessagesByRoom(String roomId) {
        messageRepo.deleteByRoomId(roomId);
    }
}
