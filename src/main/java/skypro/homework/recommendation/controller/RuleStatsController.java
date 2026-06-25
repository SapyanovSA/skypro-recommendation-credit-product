package skypro.homework.recommendation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import skypro.homework.recommendation.dto.RuleStatsResponse;
import skypro.homework.recommendation.service.RecommendationService;

/**
 * REST-контроллер для управления аналитикой и просмотра сквозной статистики
 * работы динамических правил рекомендаций.
 */
@RestController
@RequestMapping("/rule")
public class RuleStatsController {

    private final RecommendationService recommendationService;

    public RuleStatsController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Возвращает агрегированную статистику срабатываний по всем динамическим правилам.
     * Эндпоинт гарантированно выводит все правила, включая неактивные со значением 0.
     *
     * @return DTO-объект со списком идентификаторов правил и счетчиками их вызовов
     */
    @GetMapping("/stats")
    public RuleStatsResponse getStats() {
        return recommendationService.getRuleStats();
    }
}