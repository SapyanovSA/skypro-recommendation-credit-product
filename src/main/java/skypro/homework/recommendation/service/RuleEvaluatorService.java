package skypro.homework.recommendation.service;

import org.springframework.stereotype.Service;
import skypro.homework.recommendation.model.QueryCondition;
import skypro.homework.recommendation.repository.RecommendationsRepository;

import java.util.UUID;

/**
 * Сервисный компонент для динамического вычисления и валидации
 * вложенных условий правил над аналитическим контуром истории транзакций.
 */
@Service
public class RuleEvaluatorService {

    private final RecommendationsRepository repository;

    public RuleEvaluatorService(RecommendationsRepository repository) {
        this.repository = repository;
    }

    /**
     * Вычисляет логическое значение конкретного условия динамического правила для пользователя.
     * Поддерживает инверсию результата на основе флага negate.
     *
     * @param userId    уникальный идентификатор пользователя (UUID)
     * @param condition проверяемое динамическое условие из базы данных
     * @return true, если условие выполнено (с учетом флага инверсии), иначе false
     */
    public boolean evaluateCondition(UUID userId, QueryCondition condition) {
        boolean result = switch (condition.getQuery()) {
            // Временная заглушка-замена для правил кредитного скоринга из Liquibase-миграций
            case "USER_CREDIT_COUNT" -> true;

            case "USER_OF" -> condition.getArguments() != null && !condition.getArguments().isEmpty()
                    && repository.countTransactions(userId, condition.getArguments().get(0)) > 0;

            case "ACTIVE_USER_OF" -> condition.getArguments() != null && !condition.getArguments().isEmpty()
                    && repository.countTransactions(userId, condition.getArguments().get(0)) >= 5;

            case "TRANSACTION_SUM_COMPARE" -> evaluateSumCompare(userId, condition);
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> evaluateDepositWithdrawCompare(userId, condition);
            default -> false;
        };
        return condition.isNegate() ? !result : result;
    }

    /**
     * Вычисляет условия сравнения сумм транзакций по операторам (>, <, =).
     */
    private boolean evaluateSumCompare(UUID userId, QueryCondition condition) {
        String productType = condition.getArguments().get(0);
        String transactionType = condition.getArguments().get(1);
        String operator = condition.getArguments().get(2);
        long valueC = Long.parseLong(condition.getArguments().get(3));

        long sum = repository.sumTransactions(userId, productType, transactionType);
        return compareValues(sum, operator, valueC);
    }

    /**
     * Вычисляет условия сравнения сумм пополнений и списаний для конкретного продукта.
     */
    private boolean evaluateDepositWithdrawCompare(UUID userId, QueryCondition condition) {
        String productType = condition.getArguments().get(0);
        String operator = condition.getArguments().get(1);

        long depositSum = repository.sumTransactions(userId, productType, "DEPOSIT");
        long withdrawSum = repository.sumTransactions(userId, productType, "WITHDRAW");
        return compareValues(depositSum, operator, withdrawSum);
    }

    /**
     * Универсальный утилитарный метод для сопоставления двух чисел по строковому математическому оператору.
     */
    private boolean compareValues(long val1, String operator, long val2) {
        return switch (operator) {
            case ">" -> val1 > val2;
            case "<" -> val1 < val2;
            case "=" -> val1 == val2;
            case ">=" -> val1 >= val2;
            case "<=" -> val1 <= val2;
            default -> false;
        };
    }
}