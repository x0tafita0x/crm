package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.easy.to.build.crm.entity.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
}
