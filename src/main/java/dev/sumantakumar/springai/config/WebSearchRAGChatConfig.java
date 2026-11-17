package dev.sumantakumar.springai.config;

import dev.sumantakumar.springai.advisor.TokenUsageAuditAdvisor;
import dev.sumantakumar.springai.rag.WebSearchDocumentRetriever;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.List;

@Configuration
public class WebSearchRAGChatConfig {
    @Bean("webSearchRAGChatClient")
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory,
                                 RestClient.Builder restClientBuilder) {
        Advisor simpleLoggerAdvisor = new SimpleLoggerAdvisor();
        Advisor tokenUsageAuditAdvisor = new TokenUsageAuditAdvisor();
        Advisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(WebSearchDocumentRetriever.builder()
                        .restClientBuilder(restClientBuilder).maxResults(5).build())
                .build();

        return chatClientBuilder
                .defaultAdvisors(List.of(simpleLoggerAdvisor, tokenUsageAuditAdvisor,
                        messageChatMemoryAdvisor, retrievalAugmentationAdvisor))
                .build();

    }
}
