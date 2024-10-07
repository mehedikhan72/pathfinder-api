package com.amplifiers.pathfinder.openAI;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final Dotenv dotenv = Dotenv.configure().load();
    private final String apiKey = dotenv.get("GROQ_API_KEY");

    private final SimpleOpenAI openAI = SimpleOpenAI.builder()
            .apiKey(apiKey)
            .baseUrl("https://api.groq.com/openai")
            .build();

    private final Integer suggestionMaxTokens = 500;
    private final Integer chatMaxTokens = 300;

    public String suggestion(String message) {
        var chatRequest = ChatRequest.builder()
            .model("llama3-8b-8192")
            .message(
                ChatMessage.SystemMessage.of(
                    "You will give a very short description, roadmap and a few resources on how to learn the given topic." +
                    "Generate a response within a maximum 500 tokens."
                )
            )
            .message(ChatMessage.UserMessage.of(message))
            .temperature(0.0)
            .maxTokens(suggestionMaxTokens)
            .build();

        var futureChat = openAI.chatCompletions().create(chatRequest);
        var chatResponse = futureChat.join();

        System.out.println(chatResponse.firstContent());
        return chatResponse.firstContent();
    }

    public String chat(String message) {
        var chatRequest = ChatRequest.builder()
                .model("llama3-8b-8192")
                .message(ChatMessage.SystemMessage.of("You are a simple chat assistant for a website. You will give brief concise replies."))
                .message(ChatMessage.UserMessage.of(message))
                .temperature(0.0)
                .maxTokens(chatMaxTokens)
                .build();

        var futureChat = openAI.chatCompletions().create(chatRequest);
        var chatResponse = futureChat.join();

//        System.out.println(chatResponse.firstContent());

        return chatResponse.firstContent();
    }
}
