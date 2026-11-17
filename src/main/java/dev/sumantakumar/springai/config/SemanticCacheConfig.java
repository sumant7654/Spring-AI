package dev.sumantakumar.springai.config;

import io.qdrant.client.QdrantClient;
import org.springframework.ai.chat.cache.semantic.SemanticCache;
import org.springframework.ai.chat.cache.semantic.SemanticCacheAdvisor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.ai.vectorstore.redis.cache.semantic.DefaultSemanticCache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.csc.CacheConfig;

@Configuration
public class SemanticCacheConfig {

    // Redis based
    /*@Bean
    public RedisClient redisClient(@Value("${spring.data.redis.host:localhost}") String redisHost,
                                   @Value("${spring.data.redis.port:6379}") int redisPort) {
        return RedisClient.builder().hostAndPort(redisHost, redisPort).build();
    }

    @Bean
    public SemanticCache semanticCache(RedisClient redisClient, EmbeddingModel embeddingModel) {
        return DefaultSemanticCache.builder()
                .jedisClient(redisClient)
                .embeddingModel(embeddingModel)
                .similarityThreshold(0.8)
                .indexName("spring-ai-semantic-cache")
                .prefix("cache:")
                .build();
    }
*/
    @Bean
    public SemanticCacheAdvisor semanticCacheAdvisor(SemanticCache semanticCache) {
        return SemanticCacheAdvisor.builder().cache(semanticCache).build();
    }

    // Qdrant based
    @Bean("cacheVectorStore")
    public VectorStore vectorStore(QdrantClient qdrantClient, EmbeddingModel embeddingModel) {
        return QdrantVectorStore.builder(qdrantClient, embeddingModel)
                .collectionName("spring-ai-semantic-cache")
                .initializeSchema(true)
                .build();
    }

    @Bean
    public SemanticCache semanticCache(@Qualifier("cacheVectorStore") VectorStore vectorStore, EmbeddingModel embeddingModel) {
        return DefaultSemanticCache.builder()
                .vectorStore(vectorStore)
                .embeddingModel(embeddingModel)
                .similarityThreshold(0.8)
                .build();
    }
}












