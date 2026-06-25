package skypro.homework.recommendation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import skypro.homework.recommendation.dto.RecommendationResponse;
import skypro.homework.recommendation.service.RecommendationService;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер для взаимодействия с клиентским контуром банка.
 * Обеспечивает предоставление персонального скоринга финансовых предложений.
 */
@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Возвращает сформированный список персональных рекомендаций для конкретного клиента банка.
     *
     * @param userId уникальный строковый идентификатор пользователя (UUID)
     * @return список доступных клиенту банковских продуктов с рекламными текстами
     */
    @GetMapping("/{userId}")
    public List<RecommendationResponse> getRecommendations(@PathVariable UUID userId) {
        return recommendationService.getRecommendations(userId);
    }
}