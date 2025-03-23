package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.BudgetType;
import site.easy.to.build.crm.entity.Customer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    @Query("select coalesce(sum(b.amount),0) from Budget b " +
            "where b.customer.customerId = :idCustomer " +
            "and b.createdAt <= :date")
    BigDecimal previousBudget(@Param("idCustomer") int idCustomer, @Param("date") LocalDate date);

    List<Budget> findBudgetsByCustomer(Customer customer);

}
