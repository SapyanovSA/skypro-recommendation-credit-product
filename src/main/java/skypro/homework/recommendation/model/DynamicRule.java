package skypro.homework.recommendation.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dynamic_rules")
public class DynamicRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "product_text")
    private String productText;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "rule_id")
    private List<QueryCondition> rule;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    public List<QueryCondition> getRule() {
        return rule;
    }

    public void setRule(List<QueryCondition> rule) {
        this.rule = rule;
    }
}
