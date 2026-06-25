package skypro.homework.recommendation.repository;

import java.util.UUID;

public record SumCacheKey(UUID userId, String productType, String transactionType) {
}
