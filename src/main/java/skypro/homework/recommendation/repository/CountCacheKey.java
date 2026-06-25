package skypro.homework.recommendation.repository;

import java.util.UUID;

public record CountCacheKey(UUID userId, String productType) {
}
