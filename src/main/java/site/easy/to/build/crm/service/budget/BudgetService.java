package site.easy.to.build.crm.service.budget;

import site.easy.to.build.crm.entity.*;

import java.time.LocalDate;
import java.util.List;

public interface BudgetService {
    public void saveTicketExpense(Expense expense, Ticket ticket);
    public void saveLeadExpense(Expense expense, Lead lead);
    public boolean alertRateExceeded(Expense expense);
    public boolean budgetExceeded(Expense expense);
}
