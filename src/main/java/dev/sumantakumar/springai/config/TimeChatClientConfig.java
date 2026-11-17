package dev.sumantakumar.springai.config;

import dev.sumantakumar.springai.advisor.TokenUsageAuditAdvisor;
import dev.sumantakumar.springai.tool.TimeTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeChatClientConfig {

    @Bean("timeChatClient")
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory,
                                 TimeTool timeTool) {
        Advisor simpleLoggerAdvisor = new SimpleLoggerAdvisor();
        Advisor tokenUsageAuditAdvisor = new TokenUsageAuditAdvisor();
        Advisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return chatClientBuilder
                .defaultTools(timeTool)
                .defaultAdvisors(simpleLoggerAdvisor, tokenUsageAuditAdvisor, messageChatMemoryAdvisor)
                .build();


    }
}
