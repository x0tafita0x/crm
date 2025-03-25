package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "budget_type")
@Getter
@Setter
public class BudgetType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_type_id")
    private int budgetTypeId;

    @NotNull(message = "budget type required")
    @NotBlank(message = "budget type name cannot be empty")
    @Column(name = "name")
    private String budgetTypeName;

    public BudgetType() {
    }

    public BudgetType(int budgetTypeId, String budgetTypeName) {
        this.budgetTypeId = budgetTypeId;
        this.budgetTypeName = budgetTypeName;
    }
}
