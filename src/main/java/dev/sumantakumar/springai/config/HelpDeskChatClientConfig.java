package dev.sumantakumar.springai.config;

import dev.sumantakumar.springai.advisor.TokenUsageAuditAdvisor;
import dev.sumantakumar.springai.tool.HelpDeskTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.ai.tool.execution.ToolExecutionException;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class HelpDeskChatClientConfig {

    @Value("classpath:/promptTemplates/helpDeskSystemPromptTemplate.st")
    Resource helpDeskPromptTemplate;

    @Bean("helpDeskChatClient")
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder,
                                 ChatMemory chatMemory, HelpDeskTool helpDeskTool) {
        Advisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        Advisor tokenUsageAuditAdvisor = new TokenUsageAuditAdvisor();
        Advisor simpleLoggerAdvisor = new SimpleLoggerAdvisor();
        return chatClientBuilder
                .defaultSystem(helpDeskPromptTemplate)
                .defaultTools(helpDeskTool)
                .defaultAdvisors(simpleLoggerAdvisor,  messageChatMemoryAdvisor, tokenUsageAuditAdvisor)
                .build();
    }

    @Bean
    public ToolExecutionExceptionProcessor toolExecutionException(){
        return new DefaultToolExecutionExceptionProcessor(true);
    }
}
