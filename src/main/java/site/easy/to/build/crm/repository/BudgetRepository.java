package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    @Query("select coalesce(sum(b.amount),0) from Budget b " +
            "where b.customer = :customer " +
            "and b.createdAt <= :date")
    double previousBudget(Customer customer, LocalDateTime date);

    List<Budget> findBudgetsByCustomer(Customer customer);

    List<Budget> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Budget b WHERE b.customer = :customer")
    double findTotalAmountByCustomer(Customer customer);

    List<Budget> findByCreatedAtBeforeOrderByCreatedAtDesc(LocalDateTime endDate);

    List<Budget> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime startDate);
}
