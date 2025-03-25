package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "budget")
@Getter
@Setter
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private int budgetId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Amount is required")
    @Digits(integer = 10, fraction = 2, message = "Amount must be a valid number with up to 2 decimal places")
    @DecimalMin(value = "0.00", inclusive = true, message = "Amount must be greater than or equal to 0.00")
    @DecimalMax(value = "9999999.99", inclusive = true, message = "Amount must be less than or equal to 9999999.99")
    private BigDecimal amount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Budget() {
    }

    public Budget(int budgetId, Customer customer, String description, BigDecimal amount) {
        this.budgetId = budgetId;
        this.customer = customer;
        this.amount = amount;
    }
}
