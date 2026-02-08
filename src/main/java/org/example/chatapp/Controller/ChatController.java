package org.example.chatapp.Controller;

import org.example.chatapp.Model.Message;
import org.example.chatapp.Service.MessageService;
import org.example.chatapp.Utils.CryptoUtil;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatController {

    private final MessageService messageService;

    public ChatController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/sendMessage/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public Message sendMessage(
            @DestinationVariable String roomId,
            Map<String, String> body
    ) {
        Message saved = messageService.saveMessage(
                roomId,
                body.get("sender"),
                body.get("content")
        );


        saved.setContent(
                CryptoUtil.decrypt(saved.getContent())
        );

        return saved;
    }

}
