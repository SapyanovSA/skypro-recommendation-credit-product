package skypro.homework.recommendation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import skypro.homework.recommendation.model.RuleStat;

import java.util.UUID;

@Repository
public interface RuleStatRepository extends JpaRepository<RuleStat, UUID> {
}