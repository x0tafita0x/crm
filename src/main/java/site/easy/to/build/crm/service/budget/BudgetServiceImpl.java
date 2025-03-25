package site.easy.to.build.crm.service.budget;

import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.settings.AlertSettings;
import site.easy.to.build.crm.repository.LeadRepository;
import site.easy.to.build.crm.repository.TicketRepository;
import site.easy.to.build.crm.repository.settings.AlertSettingsRepository;
import site.easy.to.build.crm.repository.BudgetRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {
    private final BudgetRepository budgetRepository;
    private final AlertSettingsRepository alertSettingsRepository;
    private final LeadRepository leadRepository;
    private final TicketRepository ticketRepository;

    public BudgetServiceImpl(BudgetRepository budgetRepository, AlertSettingsRepository alertSettingsRepository,
                             LeadRepository leadRepository, TicketRepository ticketRepository) {
        this.budgetRepository = budgetRepository;
        this.alertSettingsRepository = alertSettingsRepository;
        this.leadRepository = leadRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public List<Budget> getBudgetBetween(LocalDateTime startDate, LocalDateTime endDate){
        if (startDate == null && endDate == null) {
            return budgetRepository.findAll();
        } else if (startDate == null) {
            return budgetRepository.findByCreatedAtBeforeOrderByCreatedAtDesc(endDate);
        } else if (endDate == null) {
            return budgetRepository.findByCreatedAtAfterOrderByCreatedAtDesc(startDate);
        } else {
            return budgetRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
        }
    }

    @Override
    public boolean alertRateExceeded(Object action) {
        double limit, previousExpense, previousBudget;
        AlertSettings alertSettings = alertSettingsRepository.getSettings();

        if (action instanceof Ticket ticket) {
            previousExpense = ticketRepository.findTotalDepenseBeforeCreatedAt(ticket.getCreatedAt(), ticket.getCustomer());
            previousBudget = budgetRepository.previousBudget(ticket.getCustomer(), ticket.getCreatedAt());

            limit = previousBudget * (alertSettings.getRate() / 100);
        }
        else if (action instanceof Lead lead) {
            previousExpense = leadRepository.findTotalDepenseBeforeCreatedAt(lead.getCreatedAt(), lead.getCustomer());
            previousBudget = budgetRepository.previousBudget(lead.getCustomer(), lead.getCreatedAt());

            limit = previousBudget * (alertSettings.getRate() / 100);
        }
        else {
            throw new IllegalArgumentException("action should be Ticket or Lead");
        }

        return previousExpense >= limit;
    }

    @Override
    public boolean budgetExceeded(Object action) {
        double newExpense, previousExpense, previousBudget;

        if (action instanceof Ticket ticket) {
            previousExpense = ticketRepository.findTotalDepenseBeforeCreatedAt(ticket.getCreatedAt(), ticket.getCustomer());
            previousBudget = budgetRepository.previousBudget(ticket.getCustomer(), ticket.getCreatedAt());

            newExpense = previousExpense + ticket.getDepense().doubleValue();
        }
        else if (action instanceof Lead lead) {
            previousExpense = leadRepository.findTotalDepenseBeforeCreatedAt(lead.getCreatedAt(), lead.getCustomer());
            previousBudget = budgetRepository.previousBudget(lead.getCustomer(), lead.getCreatedAt());

            newExpense = previousExpense + lead.getDepense().doubleValue();
        }
        else {
            throw new IllegalArgumentException("action should be Ticket or Lead");
        }

        return newExpense >= previousBudget;
    }
}
