package skypro.homework.recommendation.service;

import org.springframework.stereotype.Service;
import skypro.homework.recommendation.dto.RecommendationResponse;
import skypro.homework.recommendation.model.DynamicRule;
import skypro.homework.recommendation.repository.DynamicRuleRepository;
import skypro.homework.recommendation.rule.RecommendationRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecommendationService {

    private final List<RecommendationRule> fixedRules;
    private final DynamicRuleRepository dynamicRuleRepository;
    private final RuleEvaluatorService ruleEvaluatorService;

    public RecommendationService(List<RecommendationRule> fixedRules,
                                 DynamicRuleRepository dynamicRuleRepository,
                                 RuleEvaluatorService ruleEvaluatorService) {
        this.fixedRules = fixedRules;
        this.dynamicRuleRepository = dynamicRuleRepository;
        this.ruleEvaluatorService = ruleEvaluatorService;
    }

    public List<RecommendationResponse> getRecommendations(UUID userId) {
        List<RecommendationResponse> responses = new ArrayList<>();

        fixedRules.stream()
                .map(rule -> rule.evaluate(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(responses::add);

        List<DynamicRule> allDynamicRules = dynamicRuleRepository.findAll();
        for (DynamicRule rule : allDynamicRules) {
            boolean rulePassed = rule.getRule().stream()
                    .allMatch(condition -> ruleEvaluatorService.evaluateCondition(userId, condition));

            if (rulePassed) {
                responses.add(new RecommendationResponse(
                        rule.getProductName(),
                        rule.getProductId().toString(),
                        rule.getProductText()
                ));
            }
        }

        return responses;
    }
}
