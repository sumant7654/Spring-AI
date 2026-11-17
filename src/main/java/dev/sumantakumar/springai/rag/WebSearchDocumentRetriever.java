package dev.sumantakumar.springai.rag;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.common.net.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

public class WebSearchDocumentRetriever implements DocumentRetriever {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSearchDocumentRetriever.class);
    private static final String TAVILY_API_KEY = "TAVILY_API_KEY";
    private static final String TAVILY_BASE_URL = "https://api.tavily.com/search___";
    private static final int DEFAULT_RESULTLIMIT = 5;
    private final int resultLimit;
    private final RestClient restClient;

    public WebSearchDocumentRetriever(RestClient.Builder restClientBuilder, int resultLimit) {
        Assert.notNull(restClientBuilder, "restClientBuilder must not be null");
        String apiKey = System.getenv(TAVILY_API_KEY);
        Assert.hasText(apiKey, "TAVILY_API_KEY must not be empty");
        this.restClient = restClientBuilder
                .baseUrl(TAVILY_BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Beaere "+apiKey)
                .build();
        if(resultLimit <= 0){
            throw new IllegalArgumentException("resultLimit must be greater than 0");
        }
        this.resultLimit = resultLimit;
    }
    @Override
    public List<Document> retrieve(Query query) {
        LOGGER.info("Processing query text :{}", query.text());
        String text = query.text();
        Assert.hasText(text, "text must not be empty");
        TavilyResponsePayload responsePayload = restClient.post()
                .body(new TavilyRequestPayload(text, "advanced", resultLimit))
                .retrieve()
                .body(TavilyResponsePayload.class);

        if(responsePayload == null || CollectionUtils.isEmpty(responsePayload.results())){
            return List.of();
        }
        List<Document> documents = new ArrayList<>(responsePayload.results().size());
        for(TavilyResponsePayload.Hit hit : responsePayload.results()){
            Document doc = Document.builder()
                    .text(hit.content())
                    .metadata("title", hit.title())
                    .metadata("url", hit.url())
                    .score(hit.score())
                    .build();
            documents.add(doc);
        }
        return documents;
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    record TavilyRequestPayload(String query, String searchDepth, int maxResults){}

    record TavilyResponsePayload(List<Hit> results){
        record Hit(String title, String url, String content, Double score){}
    }

    public static class Builder {
        private RestClient.Builder restClientBuilder;
        private int resultLimit = DEFAULT_RESULTLIMIT;
        private Builder(){}

        public Builder restClientBuilder(RestClient.Builder restClientBuilder){
            this.restClientBuilder = restClientBuilder;
            return this;
        }

        public Builder maxResults(int resultLimit){
            if(resultLimit <= 0){
                throw new IllegalArgumentException("resultLimit must be greater than 0");
            }
            this.resultLimit = resultLimit;
            return this;
        }

        public WebSearchDocumentRetriever build(){
            return new WebSearchDocumentRetriever(restClientBuilder, resultLimit);
        }
    }
    public static Builder builder() {
        return new Builder();
    }


}
