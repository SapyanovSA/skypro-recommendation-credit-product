package skypro.homework.recommendation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skypro.homework.recommendation.dto.RecommendationResponse;
import skypro.homework.recommendation.dto.RuleStatDto;
import skypro.homework.recommendation.dto.RuleStatsResponse;
import skypro.homework.recommendation.model.DynamicRule;
import skypro.homework.recommendation.model.RuleStat;
import skypro.homework.recommendation.repository.DynamicRuleRepository;
import skypro.homework.recommendation.repository.RuleStatRepository;
import skypro.homework.recommendation.repository.RecommendationsRepository;
import skypro.homework.recommendation.rule.RecommendationRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final List<RecommendationRule> fixedRules;
    private final DynamicRuleRepository dynamicRuleRepository;
    private final RuleEvaluatorService ruleEvaluatorService;
    private final RuleStatRepository ruleStatRepository;
    private final RecommendationsRepository recommendationsRepository;

    public RecommendationService(List<RecommendationRule> fixedRules,
                                 DynamicRuleRepository dynamicRuleRepository,
                                 RuleEvaluatorService ruleEvaluatorService,
                                 RuleStatRepository ruleStatRepository,
                                 RecommendationsRepository recommendationsRepository) {
        this.fixedRules = fixedRules;
        this.dynamicRuleRepository = dynamicRuleRepository;
        this.ruleEvaluatorService = ruleEvaluatorService;
        this.ruleStatRepository = ruleStatRepository;
        this.recommendationsRepository = recommendationsRepository;
    }

    @Transactional
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
                RuleStat stat = ruleStatRepository.findById(rule.getId())
                        .orElseGet(() -> new RuleStat(rule.getId(), 0L));
                stat.setCount(stat.getCount() + 1);
                ruleStatRepository.save(stat);

                responses.add(new RecommendationResponse(
                        rule.getProductName(),
                        rule.getProductId().toString(),
                        rule.getProductText()
                ));
            }
        }

        return responses;
    }

    @Transactional(readOnly = true)
    public RuleStatsResponse getRuleStats() {
        List<DynamicRule> allRules = dynamicRuleRepository.findAll();

        Map<UUID, Long> statsMap = ruleStatRepository.findAll().stream()
                .collect(Collectors.toMap(RuleStat::getRuleId, RuleStat::getCount));

        List<RuleStatDto> dtos = allRules.stream().map(rule -> {
            long count = statsMap.getOrDefault(rule.getId(), 0L);
            return new RuleStatDto(rule.getId().toString(), count);
        }).collect(Collectors.toList());

        return new RuleStatsResponse(dtos);
    }

    public void clearCaches() {
        recommendationsRepository.clearAllCaches();
    }
}