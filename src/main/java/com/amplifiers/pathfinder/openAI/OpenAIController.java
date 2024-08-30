package com.amplifiers.pathfinder.openAI;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/ai")
@RequiredArgsConstructor
public class OpenAIController {

    private final OpenAIService openAIService;

    @GetMapping("/chat")
    public ResponseEntity<?> getChatResponse(@RequestParam(name = "query") String query) {
        return ResponseEntity.ok(openAIService.chat(query));
    }

    @GetMapping("/guideline")
    public ResponseEntity<?> getSearchSuggestionResponse(@RequestParam(name = "query") String query) {
        return ResponseEntity.ok(openAIService.suggestion(query));
    }
}
