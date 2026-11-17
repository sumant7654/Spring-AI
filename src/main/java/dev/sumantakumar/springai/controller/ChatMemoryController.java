package dev.sumantakumar.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatMemoryController {

    private ChatClient chatClient;

    @Autowired
    public void setChatClient(@Qualifier("chatMemoryChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat-memory/{username}")
    public ResponseEntity<String> getChatClient(@PathVariable String username, @RequestParam("message") String message) {
        String content = chatClient.prompt().user(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, username))
                .call()
                .content();
        return ResponseEntity.ok(content);
    }
}









