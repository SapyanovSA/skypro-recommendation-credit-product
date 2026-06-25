package skypro.homework.recommendation.controller;

import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import skypro.homework.recommendation.service.RecommendationService;

import java.util.Map;

@RestController
@RequestMapping("/management")
public class CacheManagementController {

    private final RecommendationService recommendationService;
    private final BuildProperties buildProperties;

    public CacheManagementController(RecommendationService recommendationService,
                                     BuildProperties buildProperties) {
        this.recommendationService = recommendationService;
        this.buildProperties = buildProperties;
    }

    @PostMapping("/clear-caches")
    @ResponseStatus(HttpStatus.OK)
    public void clearCaches() {
        recommendationService.clearCaches();
    }

    @GetMapping("/info")
    public Map<String, String> getInfo() {
        return Map.of(
                "name", buildProperties.getName(),
                "version", buildProperties.getVersion()
        );
    }
}