package skypro.homework.recommendation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import skypro.homework.recommendation.dto.RuleStatsResponse;
import skypro.homework.recommendation.service.RecommendationService;

@RestController
public class RuleStatsController {

    private final RecommendationService recommendationService;

    public RuleStatsController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/rule/stats")
    public RuleStatsResponse getRuleStats() {
        return recommendationService.getRuleStats();
    }
}