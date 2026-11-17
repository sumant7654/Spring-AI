package dev.sumantakumar.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/open")
public class OpenChatController {
    @Autowired
    @Qualifier("openChatClient")
    private ChatClient chatClient;

    @GetMapping("/chat")
    public ResponseEntity<String> chat(@RequestParam("message") String message) {
        String content = chatClient.prompt()
                .user(message)
                .call().content();
        return ResponseEntity.ok(content);
    }
}
