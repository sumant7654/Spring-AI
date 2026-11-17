package dev.sumantakumar.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rag")
public class RagController {
    @Autowired
    @Qualifier("chatMemoryChatClient")
    private ChatClient chatClient;

    @Autowired
    @Qualifier("webSearchRAGChatClient")
    private ChatClient webSearchRAGChatClient;

    @Autowired
    private VectorStore vectorStore;

    @Value("classpath:/promptTemplates/systemPromptRandomDataTemplate.st")
    private Resource promptTemplate;

    @Value("classpath:/promptTemplates/policySystemPromptTemplate.st")
    private Resource policyTemplate;

    @GetMapping("/random/chat")
    public ResponseEntity<String> randomChat(@RequestHeader("username") String username, @RequestParam("message") String message) {
        SearchRequest searchRequest = SearchRequest.builder().query(message).topK(3).similarityThreshold(0.5).build();
        List<Document> similarDocs = vectorStore.similaritySearch(searchRequest);
        String similarContext = similarDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));

        String answer = chatClient.prompt().system(promptSystemSpec -> promptSystemSpec.text(promptTemplate)
                        .param("documents", similarContext))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, username))
                .user(message)
                .call()
                .content();
        return ResponseEntity.ok(answer);
    }

    @GetMapping("/document/chat")
    public ResponseEntity<String> documentChat(@RequestHeader("username") String username, @RequestParam("message") String message) {
        SearchRequest searchRequest = SearchRequest.builder().query(message).topK(3).similarityThreshold(0.5).build();
        List<Document> similarDocs = vectorStore.similaritySearch(searchRequest);
        String similarContext = similarDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));
        String answer = chatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec.text(policyTemplate)
                        .param("documents", similarContext))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, username))
                .user(message)
                .call()
                .content();
        return ResponseEntity.ok(answer);
    }

    @GetMapping("/websearch/chat")
    public ResponseEntity<String> webSearchChat(@RequestHeader("username") String username, @RequestParam("message") String message) {
        String answer = webSearchRAGChatClient.prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, username))
                .user(message)
                .call()
                .content();
        return ResponseEntity.ok(answer);
    }















}
