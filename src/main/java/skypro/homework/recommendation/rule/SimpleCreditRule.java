package skypro.homework.recommendation.rule;

import org.springframework.stereotype.Component;
import skypro.homework.recommendation.dto.RecommendationResponse;

import java.util.Optional;
import java.util.UUID;

@Component
public class SimpleCreditRule implements RecommendationRule {

    @Override
    public Optional<RecommendationResponse> evaluate(UUID userId) {
        // Check if the first character of the UUID is a digit, recommended product.
        char firstChar = userId.toString().charAt(0);
        if (Character.isDigit(firstChar)) {
            return Optional.of(new RecommendationResponse(
                    "Простой кредит",
                    "simple_credit_id",
                    "Попробуйте наш новый выгодный кредит с базовой ставкой!"
            ));
        }
        return Optional.empty();
    }
}
