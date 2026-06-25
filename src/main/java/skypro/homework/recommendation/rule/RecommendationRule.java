package skypro.homework.recommendation.rule;

import skypro.homework.recommendation.dto.RecommendationResponse;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRule {
    Optional<RecommendationResponse> evaluate(UUID userId);
}
