package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.easy.to.build.crm.entity.BudgetType;

public interface BudgetTypeRepository extends JpaRepository<BudgetType, Integer> {
}
