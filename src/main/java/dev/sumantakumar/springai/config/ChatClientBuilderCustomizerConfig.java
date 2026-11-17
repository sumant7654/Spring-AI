package dev.sumantakumar.springai.config;

import dev.sumantakumar.springai.advisor.TokenUsageAuditAdvisor;
import org.springframework.ai.chat.client.ChatClientBuilderCustomizer;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientBuilderCustomizerConfig {
    @Bean
    public ChatClientBuilderCustomizer loggerAdvisor() {
        return builder -> builder.defaultAdvisors(new SimpleLoggerAdvisor());
    }

    @Bean
    @ConditionalOnBooleanProperty(name = "audit.token-usag.enabled", havingValue = true)
    public ChatClientBuilderCustomizer auditAdvisor() {
        return builder -> builder.defaultAdvisors(new TokenUsageAuditAdvisor());
    }
}

















