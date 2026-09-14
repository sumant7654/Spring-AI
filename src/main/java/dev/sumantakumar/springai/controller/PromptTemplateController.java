package dev.sumantakumar.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PromptTemplateController {

    private ChatClient chatClient;

    @Autowired
    public void setChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    @Value("classpath:/promptTemplates/userPromptTemplate.st")
    Resource userPromptTemplate;

    @GetMapping("/email")
    public String email(@RequestParam("customerName") String customerName,
                        @RequestParam("customerMessage") String customerMessage) {
        return chatClient.prompt()
                .system("""
                        You are an professional customer service assistant which helps drafting email
                         responses to improve the productivity of the customer support team.
                        If a user requests help wih anything outside of these responsibilities,
                        respond politely and inform them that your are only able to assist with
                        IT support tasks within your defined scope.""")
                .user(promptTemplateSpec ->
                        promptTemplateSpec.text(userPromptTemplate)
                                .param("customerName", customerName)
                                .param("customerMessage", customerMessage))
                .call().content();

    }
}
