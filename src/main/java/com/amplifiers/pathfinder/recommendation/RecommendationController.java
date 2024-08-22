package com.amplifiers.pathfinder.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/recommendation")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/add-purchase-view")
    @ResponseStatus(HttpStatus.OK)
    public void addPurchaseView(
            @PathVariable Integer GigId,
            @PathVariable Integer UserId,
            @RequestParam String recommId
    ) {
        recommendationService.addPurchaseView(GigId, UserId, recommId);
    }

}
