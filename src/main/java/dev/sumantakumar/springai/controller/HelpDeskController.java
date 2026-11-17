package dev.sumantakumar.springai.controller;

import dev.sumantakumar.springai.tool.HelpDeskTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tools")
public class HelpDeskController {
    @Autowired
    @Qualifier("helpDeskChatClient")
    private ChatClient chatClient;

    @Autowired
    private HelpDeskTool helpDeskTool;

    @GetMapping("/help-desk")
    public ResponseEntity<String> helpDesk(@RequestHeader("username") String username, @RequestParam("message") String message) {
        String content = chatClient.prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, username))
                .user(message)
                .toolContext(Map.of("username", username))
                .call()
                .content();
        return ResponseEntity.ok(content);
    }

}



















