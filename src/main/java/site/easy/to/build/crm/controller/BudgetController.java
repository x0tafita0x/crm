package site.easy.to.build.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.BudgetType;
import site.easy.to.build.crm.repository.BudgetRepository;
import site.easy.to.build.crm.repository.BudgetTypeRepository;
import site.easy.to.build.crm.repository.CustomerRepository;
import site.easy.to.build.crm.repository.ExpenseRepository;

@Controller
@RequestMapping("/budget")
public class BudgetController {
    private final BudgetRepository budgetRepository;
    private final CustomerRepository customerRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetTypeRepository budgetTypeRepository;

    public BudgetController(BudgetRepository budgetRepository, CustomerRepository customerRepository,
                            ExpenseRepository expenseRepository, BudgetTypeRepository budgetTypeRepository) {
        this.budgetRepository = budgetRepository;
        this.customerRepository = customerRepository;
        this.expenseRepository = expenseRepository;
        this.budgetTypeRepository = budgetTypeRepository;
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
        model.addAttribute("budgetTypes", budgetTypeRepository.findAll());
        return "budget/create-budget";
    }

    @PostMapping("/create-budget")
    public String createBudget(@ModelAttribute("budget") @Validated Budget budget,
                               BindingResult bindingResult, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("customers", customerRepository.findAll());
                model.addAttribute("budgetTypes", budgetTypeRepository.findAll());
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
}
