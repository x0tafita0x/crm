package site.easy.to.build.crm.service.budget;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.settings.AlertSettings;
import site.easy.to.build.crm.repository.LeadRepository;
import site.easy.to.build.crm.repository.TicketRepository;
import site.easy.to.build.crm.repository.settings.AlertSettingsRepository;
import site.easy.to.build.crm.repository.BudgetRepository;
import site.easy.to.build.crm.repository.ExpenseRepository;

import java.math.BigDecimal;

@Service
public class BudgetServiceImpl implements BudgetService {
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final AlertSettingsRepository alertSettingsRepository;
    private final LeadRepository leadRepository;
    private final TicketRepository ticketRepository;

    public BudgetServiceImpl(BudgetRepository budgetRepository,
                             ExpenseRepository expenseRepository, AlertSettingsRepository alertSettingsRepository,
                             LeadRepository leadRepository, TicketRepository ticketRepository) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.alertSettingsRepository = alertSettingsRepository;
        this.leadRepository = leadRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public void saveTicketExpense(Expense expense, Ticket ticket) {
        expense = expenseRepository.save(expense);
        ticket.setExpense(expense);
        if (ticketRepository.findById(ticket.getTicketId()).isPresent()) {
            ticketRepository.save(ticket);
        }
    }

    @Override
    public void saveLeadExpense(Expense expense, Lead lead) {
        expense = expenseRepository.save(expense);
        lead.setExpense(expense);
        if (leadRepository.findById(lead.getLeadId()).isPresent()) {
            leadRepository.save(lead);
        }
    }

    @Override
    public boolean alertRateExceeded(Expense expense) {
        BigDecimal previousExpense = expenseRepository.previousExpenses(expense.getCustomer().getCustomerId(),
                expense.getCreatedAt(), expense.getBudget().getBudgetId());
        BigDecimal previousBudget = budgetRepository.previousBudget(expense.getCustomer().getCustomerId(),
                expense.getCreatedAt());

        AlertSettings alertSettings = alertSettingsRepository.getSettings();
        double limit = previousBudget.doubleValue() * (alertSettings.getRate() / 100);

        return previousExpense.doubleValue() >= limit;
    }

    @Override
    public boolean budgetExceeded(Expense expense) {
        BigDecimal previousExpense = expenseRepository.previousExpenses(expense.getCustomer().getCustomerId(),
                expense.getCreatedAt(), expense.getBudget().getBudgetId());
        BigDecimal previousBudget = budgetRepository.previousBudget(expense.getCustomer().getCustomerId(),
                expense.getCreatedAt());

        return previousExpense.doubleValue() + expense.getAmount().doubleValue() >= previousBudget.doubleValue();
    }
}
