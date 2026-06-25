package skypro.homework.recommendation.dto;

import skypro.homework.recommendation.model.DynamicRule;

import java.util.List;

public record RuleListResponse(List<DynamicRule> data) {
}
