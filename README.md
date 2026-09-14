# Spring AI + Ollama

A Spring Boot application demonstrating how to integrate **Spring AI** with a locally running **Ollama LLM**.

The project covers several practical Spring AI features, including:

* `ChatClient`
* Ollama integration
* System prompts
* User prompts
* Prompt templates
* Prompt stuffing
* Streaming responses
* Structured output
* Bean output conversion
* List and Map output conversion
* Custom `CallAdvisor`
* Token usage logging
* `SimpleLoggerAdvisor`

## Tech Stack

* Java 21
* Spring Boot 4.1.1
* Spring AI 2.0.1
* Ollama
* Llama 3.2
* Maven
* Spring Web MVC

## Project Structure

```text
src/
└── main/
    ├── java/
    │   └── dev/sumantakumar/springai/
    │       ├── SpringAiApplication.java
    │       │
    │       ├── advisor/
    │       │   └── TokenUsageAuditAdvisor.java
    │       │
    │       ├── config/
    │       │   └── ChatClientConfig.java
    │       │
    │       ├── controller/
    │       │   ├── ChatController.java
    │       │   ├── PromptStuffingController.java
    │       │   ├── PromptTemplateController.java
    │       │   ├── StreamController.java
    │       │   └── StructureOutPutController.java
    │       │
    │       └── model/
    │           └── CountryCities.java
    │
    └── resources/
        ├── application.properties
        └── promptTemplates/
            ├── systemPromptTemplate.st
            └── userPromptTemplate.st
```

## Prerequisites

Make sure the following are installed:

* Java 21
* Maven
* Ollama

Check the installations:

```bash
java -version
mvn -version
ollama --version
```

## Ollama Setup

Install Ollama and download the Llama 3.2 model.

```bash
ollama pull llama3.2:latest
```

Start Ollama:

```bash
ollama serve
```

By default, Ollama runs on:

```text
http://localhost:11434
```

The application is configured to use:

```properties
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.model=llama3.2:latest
```

## Configuration

`application.properties`:

```properties
spring.application.name=Spring AI

spring.ai.model.chat=ollama
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.model=llama3.2:latest

logging.level.org.springframework.ai.chat.client.advisor=DEBUG
```

The application also uses a custom console logging pattern for easier debugging.

## Running the Application

Clone the repository and navigate to the project directory:

```bash
git clone <repository-url>
cd SpringAI
```

Run the application:

```bash
mvn spring-boot:run
```

The application will start on the default Spring Boot port:

```text
http://localhost:8080
```

---

# API Examples

All APIs are available under:

```text
/api
```

## 1. Basic Chat

### Endpoint

```http
GET /api/chat
```

### Example

```text
http://localhost:8080/api/chat?message=What is Spring AI?
```

### Implementation

The `ChatController` uses `ChatClient` to send a user message to Ollama.

```java
chatClient.prompt()
        .user(message)
        .call()
        .chatClientResponse();
```

This demonstrates the basic Spring AI request/response flow.

---

## 2. Prompt Stuffing

### Endpoint

```http
GET /api/prompt
```

### Example

```text
http://localhost:8080/api/prompt?message=Explain the leave policy
```

A system prompt is loaded from:

```text
src/main/resources/promptTemplates/systemPromptTemplate.st
```

The prompt is supplied dynamically using:

```java
.system(systemPromptTemplate)
```

This demonstrates how external prompt files can be used instead of keeping large prompts directly inside Java code.

---

## 3. Prompt Templates

### Endpoint

```http
GET /api/email
```

### Parameters

* `customerName`
* `customerMessage`

### Example

```text
http://localhost:8080/api/email?customerName=John&customerMessage=How many paid leaves do I get?
```

The user prompt is loaded from:

```text
src/main/resources/promptTemplates/userPromptTemplate.st
```

Template parameters are populated using:

```java
.promptTemplateSpec
    .param("customerName", customerName)
    .param("customerMessage", customerMessage)
```

This is useful when prompts need dynamic values.

---

## 4. Streaming Response

### Endpoint

```http
GET /api/stream
```

### Example

```text
http://localhost:8080/api/stream?message=Explain microservices architecture
```

The application uses:

```java
chatClient.prompt()
        .user(message)
        .stream()
        .content();
```

The response is returned as a reactive `Flux<String>`.

This allows the client to receive the generated response incrementally instead of waiting for the complete response.

---

# Structured Output

Spring AI can convert an LLM response into Java objects and other structured formats.

The project uses the following converters:

* `CountryCities`
* `ListOutputConverter`
* `MapOutputConverter`
* `BeanOutputConverter`
* `ParameterizedTypeReference`

## 5. Entity Output

### Endpoint

```http
GET /api/chat-entity
```

### Example

```text
http://localhost:8080/api/chat-entity?message=Give me India and its major cities
```

The response is converted into:

```java
CountryCities
```

The model is:

```java
public record CountryCities(
        String country,
        List<String> cities
) {
}
```

Example response:

```json
{
  "country": "India",
  "cities": [
    "Mumbai",
    "Delhi",
    "Bengaluru",
    "Hyderabad"
  ]
}
```

The conversion is performed using:

```java
.entity(CountryCities.class)
```

---

## 6. List Output

### Endpoint

```http
GET /api/chat-list
```

### Example

```text
http://localhost:8080/api/chat-list?message=Give me five popular programming languages
```

The response is converted into:

```java
List<String>
```

using:

```java
.entity(new ListOutputConverter())
```

---

## 7. Map Output

### Endpoint

```http
GET /api/chat-map
```

### Example

```text
http://localhost:8080/api/chat-map?message=Give me information about Java including creator and release year
```

The response is converted into:

```java
Map<String, Object>
```

using:

```java
.entity(new MapOutputConverter())
```

---

## 8. Bean Output Converter

### Endpoint

```http
GET /api/chat-bean
```

### Example

```text
http://localhost:8080/api/chat-bean?message=Give me India and five major cities
```

The application uses:

```java
new BeanOutputConverter<>(CountryCities.class)
```

to convert the LLM response into a Java object.

---

## 9. List of Java Beans

### Endpoint

```http
GET /api/chat-bean-list
```

### Example

```text
http://localhost:8080/api/chat-bean-list?message=Give me three countries and their major cities
```

The response is converted into:

```java
List<CountryCities>
```

using:

```java
new ParameterizedTypeReference<List<CountryCities>>() {}
```

This demonstrates handling structured collections of custom Java objects.

---

# ChatClient Configuration

The application defines a reusable `ChatClient` bean in:

```text
ChatClientConfig.java
```

The Ollama options are configured as:

```java
OllamaChatOptions.builder()
        .model("llama3.2:latest")
        .temperature(0.8)
        .maxTokens(500);
```

The configured client also uses advisors:

```java
.defaultAdvisors(
    List.of(
        SimpleLoggerAdvisor.builder().build(),
        new TokenUsageAuditAdvisor()
    )
)
```

A default system prompt is also configured to restrict the assistant to cybersecurity-related support for Digi Bank.

---

# Custom Token Usage Advisor

The project contains a custom advisor:

```text
TokenUsageAuditAdvisor.java
```

It implements:

```java
CallAdvisor
```

The advisor retrieves token usage information from the `ChatResponse`:

```java
Usage usage = chatResponse.getMetadata().getUsage();
```

and logs the usage:

```java
logger.info("Token usage details : {}", usage.toString());
```

This demonstrates how Spring AI advisors can be used to add custom behavior around model calls.

---

# Spring AI Concepts Demonstrated

| Feature                 | Implementation                   |
| ----------------------- | -------------------------------- |
| Basic Chat              | `ChatClient`                     |
| Ollama                  | `spring-ai-starter-model-ollama` |
| System Prompt           | `.system()`                      |
| User Prompt             | `.user()`                        |
| Prompt Templates        | `.text(resource).param()`        |
| Prompt Stuffing         | External `.st` prompt            |
| Streaming               | `.stream().content()`            |
| Entity Conversion       | `.entity(Class)`                 |
| List Conversion         | `ListOutputConverter`            |
| Map Conversion          | `MapOutputConverter`             |
| Bean Conversion         | `BeanOutputConverter`            |
| Generic List Conversion | `ParameterizedTypeReference`     |
| Logging                 | `SimpleLoggerAdvisor`            |
| Custom Advisor          | `TokenUsageAuditAdvisor`         |
| Token Usage             | `Usage` metadata                 |

# Useful Commands

Pull the Ollama model:

```bash
ollama pull llama3.2:latest
```

List installed models:

```bash
ollama list
```

Run the model directly:

```bash
ollama run llama3.2:latest
```

Start the Spring Boot application:

```bash
mvn spring-boot:run
```

Build the project:

```bash
mvn clean package
```

Run the generated JAR:

```bash
java -jar target/SpringAI-0.0.1-SNAPSHOT.jar
```

# Learning Goals

This project is intended as a hands-on example for learning how to build Spring Boot applications using Spring AI and locally hosted LLMs.

The main focus is understanding:

1. How `ChatClient` communicates with an LLM.
2. How Ollama can be used as a local model provider.
3. How system and user prompts are constructed.
4. How prompt templates can be externalized.
5. How streaming responses work.
6. How LLM responses can be converted into Java objects.
7. How Spring AI advisors can intercept and enhance model interactions.
8. How token usage can be inspected from model responses.

# License

This project is intended for learning and experimentation with Spring AI and Ollama.
