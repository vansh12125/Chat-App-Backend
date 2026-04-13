package org.example.chatapp.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {
    @GetMapping("/health")
    public ResponseEntity<?> getHealth() {
        return new ResponseEntity<>("Server is running!",HttpStatus.OK);
    }
}
