package site.easy.to.build.crm.service.budget;

import site.easy.to.build.crm.entity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BudgetService {
    public boolean alertRateExceeded(Object action);
    public boolean budgetExceeded(Object action);
    public List<Budget> getBudgetBetween(LocalDateTime startDate, LocalDateTime endDate);
}
