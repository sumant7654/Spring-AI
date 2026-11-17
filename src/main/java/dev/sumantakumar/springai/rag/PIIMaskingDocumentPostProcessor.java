package dev.sumantakumar.springai.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;

import java.util.List;
import java.util.regex.Pattern;

public class PIIMaskingDocumentPostProcessor implements DocumentPostProcessor {

    // Regex patterns for common PII
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b"
            );

    private static final Pattern PHONE_PATTERN =
            Pattern.compile(
                    "\\b(?:\\+91[-.\\s]?)?[6-9]\\d{9}\\b"
            );

    private static final String EMAIL_REPLACEMENT = "[REDACTED_EMAIL]";
    private static final String PHONE_REPLACEMENT = "[REDACTED_PHONE]";

    private PIIMaskingDocumentPostProcessor() {
    }

    public static PIIMaskingDocumentPostProcessor builder() {
        return new PIIMaskingDocumentPostProcessor();
    }

    @Override
    public List<Document> process(Query query, List<Document> documents) {

        return documents.stream()
                .map(document -> {

                    String content = document.getText();

                    if (content == null || content.isBlank()) {
                        return document;
                    }

                    String maskedContent = EMAIL_PATTERN
                            .matcher(content)
                            .replaceAll(EMAIL_REPLACEMENT);

                    maskedContent = PHONE_PATTERN
                            .matcher(maskedContent)
                            .replaceAll(PHONE_REPLACEMENT);

                    return new Document(
                            maskedContent,
                            document.getMetadata()
                    );
                })
                .toList();
    }
}