package skypro.homework.recommendation.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import skypro.homework.recommendation.model.DynamicRule;

import java.util.UUID;

@Repository
public interface DynamicRuleRepository extends JpaRepository<DynamicRule, UUID> {
    @Transactional
    void deleteByProductId(UUID productId);
}
