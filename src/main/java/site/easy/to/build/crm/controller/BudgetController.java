package site.easy.to.build.crm.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.repository.*;
import site.easy.to.build.crm.service.budget.BudgetService;

@Controller
@RequestMapping("/budget")
public class BudgetController {
    private final BudgetRepository budgetRepository;
    private final CustomerRepository customerRepository;
    private final ExpenseRepository expenseRepository;
    private final LeadRepository leadRepository;
    private final TicketRepository ticketRepository;
    private final BudgetService budgetService;

    public BudgetController(BudgetRepository budgetRepository, CustomerRepository customerRepository,
                            ExpenseRepository expenseRepository, LeadRepository leadRepository, TicketRepository ticketRepository, BudgetService budgetService) {
        this.budgetRepository = budgetRepository;
        this.customerRepository = customerRepository;
        this.expenseRepository = expenseRepository;
        this.leadRepository = leadRepository;
        this.ticketRepository = ticketRepository;
        this.budgetService = budgetService;
    }

    @GetMapping("/all-budget")
    public String allBudget(Model model) {
        model.addAttribute("budgets", budgetRepository.findAll());
        return "budget/all-budgets";
    }

    @GetMapping("/create-budget")
    public String createBudget(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("budget", new Budget());
        return "budget/create-budget";
    }

    @PostMapping("/create-budget")
    public String createBudget(@ModelAttribute("budget") @Validated Budget budget,
                               BindingResult bindingResult, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("customers", customerRepository.findAll());
                return "budget/create-budget";
            }
            budgetRepository.save(budget);

            return "redirect:/budget/all-budget";
        } catch (Exception e) {
            return "error/500";
        }
    }

    @GetMapping("/all-expense")
    public String allExpense(Model model) {
        model.addAttribute("expenses", expenseRepository.findAll());
        return "expense/all-expenses";
    }

    @GetMapping("/lead/{idLead}/create-expense")
    public String createLeadExpense(Model model, @PathVariable int idLead) {
        if (leadRepository.findById(idLead).isEmpty()) {
            return "error/not-found";
        }
        Lead lead = leadRepository.findById(idLead).get();
        model.addAttribute("customer", lead.getCustomer());
        model.addAttribute("expense", new Expense());
        model.addAttribute("budgets", budgetRepository.findBudgetsByCustomer(lead.getCustomer()));
        model.addAttribute("form_url", "/budget/lead/" + idLead + "/create-expense");
        return "expense/create-expense";
    }

    @PostMapping("/lead/{idLead}/create-expense")
    public String createLeadExpense(@ModelAttribute("expense") Expense expense, BindingResult bindingResult,
                                    Model model, @PathVariable int idLead, RedirectAttributes redirectAttributes,
                                    HttpSession session) {
        if (leadRepository.findById(idLead).isEmpty()) {
            return "error/not-found";
        }
        Lead lead = leadRepository.findById(idLead).get();

        if (bindingResult.hasErrors()) {
            model.addAttribute("customer", lead.getCustomer());
            model.addAttribute("budgets", budgetRepository.findBudgetsByCustomer(lead.getCustomer()));
            return "expense/create-expense";
        }

        try {
            expense.setCustomer(lead.getCustomer());
            if (budgetService.budgetExceeded(expense)) {
                session.setAttribute("expense_insert", expense);
                session.setAttribute("lead", lead);
                return "redirect:/budget/confirm-expense";
            }

            budgetService.saveLeadExpense(expense, lead);

            if (budgetService.alertRateExceeded(expense)) {
                redirectAttributes.addFlashAttribute("alert", "taux d'alerte atteint");
            }

            return "redirect:/budget/all-expense";
        } catch (Exception e) {
            return "error/500";
        }
    }

    @GetMapping("/ticket/{idTicket}/create-expense")
    public String createTicketExpense(Model model, @PathVariable int idTicket) {
        if (ticketRepository.findById(idTicket).isEmpty()) {
            return "error/not-found";
        }
        Ticket ticket = ticketRepository.findById(idTicket).get();
        model.addAttribute("customer", ticket.getCustomer());
        model.addAttribute("expense", new Expense());
        model.addAttribute("budgets", budgetRepository.findBudgetsByCustomer(ticket.getCustomer()));
        model.addAttribute("form_url", "/budget/ticket/" + idTicket + "/create-expense");
        return "expense/create-expense";
    }

    @PostMapping("/ticket/{idTicket}/create-expense")
    public String createTicketExpense(@ModelAttribute("expense") @Validated Expense expense, BindingResult bindingResult,
                                      Model model, @PathVariable int idTicket, RedirectAttributes redirectAttributes,
                                      HttpSession session) {
        if (ticketRepository.findById(idTicket).isEmpty()) {
            return "error/not-found";
        }
        Ticket ticket = ticketRepository.findById(idTicket).get();

        if (bindingResult.hasErrors()) {
            model.addAttribute("customer", ticket.getCustomer());
            model.addAttribute("budgets", budgetRepository.findBudgetsByCustomer(ticket.getCustomer()));
        }

        try {
            expense.setCustomer(ticket.getCustomer());
            if (budgetService.budgetExceeded(expense)) {
                session.setAttribute("expense_insert", expense);
                session.setAttribute("ticket", ticket);
                return "redirect:/budget/confirm-expense";
            }

            budgetService.saveTicketExpense(expense, ticket);

            if (budgetService.alertRateExceeded(expense)) {
                redirectAttributes.addFlashAttribute("alert", "taux d'alerte atteint");
            }

            return "redirect:/budget/all-expense";
        } catch (Exception e) {
            e.printStackTrace();
            return "error/500";
        }
    }

    @GetMapping("/confirm-expense")
    public String confirmExpense(Model model) {
        return "expense/confirm";
    }

    @GetMapping("/cancel-expense")
    public String cancelExpense(Model model, HttpSession session) {
        session.removeAttribute("expense_insert");
        session.removeAttribute("ticket");
        session.removeAttribute("lead");
        return "redirect:/budget/all-expense";
    }

    @GetMapping("/validate-confirm")
    public String validateConfirm(Model model, HttpSession session) {
        Expense expense = (Expense) session.getAttribute("expense_insert");
        if (session.getAttribute("ticket") != null) {
            budgetService.saveTicketExpense(expense, (Ticket) session.getAttribute("ticket"));
        }
        if (session.getAttribute("lead") != null) {
            budgetService.saveLeadExpense(expense, (Lead) session.getAttribute("lead"));
        }
        return "redirect:/budget/all-expense";
    }
}
