package skypro.homework.recommendation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import skypro.homework.recommendation.dto.RecommendationResponse;
import skypro.homework.recommendation.dto.RuleListResponse;
import skypro.homework.recommendation.model.DynamicRule;
import skypro.homework.recommendation.repository.DynamicRuleRepository;
import skypro.homework.recommendation.service.RecommendationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final DynamicRuleRepository dynamicRuleRepository;

    public RecommendationController(RecommendationService recommendationService,
                                    DynamicRuleRepository dynamicRuleRepository) {
        this.recommendationService = recommendationService;
        this.dynamicRuleRepository = dynamicRuleRepository;
    }

    @GetMapping("/{userId}")
    public List<RecommendationResponse> getRecommendations(@PathVariable UUID userId) {
        return recommendationService.getRecommendations(userId);
    }

    @PostMapping("/rule")
    public DynamicRule createRule(@RequestBody DynamicRule dynamicRule) {
        return dynamicRuleRepository.save(dynamicRule);
    }

    @GetMapping("/rule")
    public RuleListResponse getAllRules() {
        return new RuleListResponse(dynamicRuleRepository.findAll());
    }

    @DeleteMapping("/rule/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRuleByProductId(@PathVariable UUID productId) {
        dynamicRuleRepository.deleteByProductId(productId);
    }
}
