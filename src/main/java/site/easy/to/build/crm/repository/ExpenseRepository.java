package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.BudgetType;
import site.easy.to.build.crm.entity.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    @Query("select coalesce(sum(e.amount),0) from Expense e " +
            "where e.customer.customerId=:idCustomer " +
            "and e.createdAt <= :date " +
            "and e.budget.budgetId = :idBudget")
    BigDecimal previousExpenses(@Param("idCustomer") int idCustomer, @Param("date") LocalDate date,
                                @Param("idBudget") int idBudget);
}
