package skypro.homework.recommendation.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class RecommendationsRepository {
    private final JdbcTemplate jdbcTemplate;

    private final Cache<CountCacheKey, Long> countCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(10000)
            .build();

    private final Cache<SumCacheKey, Long> sumCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(10000)
            .build();

    public RecommendationsRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long countTransactions(UUID userId, String productType) {
        CountCacheKey key = new CountCacheKey(userId, productType);
        return countCache.get(key, k -> {
            String sql = "SELECT COUNT(*) FROM transactions WHERE user_id = ? AND product_type = ?";
            Long count = jdbcTemplate.queryForObject(sql, Long.class, userId, productType);
            return count != null ? count : 0L;
        });
    }

    public long sumTransactions(UUID userId, String productType, String transactionType) {
        SumCacheKey key = new SumCacheKey(userId, productType, transactionType);
        return sumCache.get(key, k -> {
            String sql = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND product_type = ? AND transaction_type = ?";
            Long sum = jdbcTemplate.queryForObject(sql, Long.class, userId, productType, transactionType);
            return sum != null ? sum : 0L;
        });
    }

    public void clearAllCaches() {
        countCache.invalidateAll();
        sumCache.invalidateAll();
    }
}
