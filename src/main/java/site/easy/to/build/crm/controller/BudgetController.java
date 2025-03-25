package site.easy.to.build.crm.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.google.model.gmail.Attachment;
import site.easy.to.build.crm.repository.*;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.ticket.TicketService;
import site.easy.to.build.crm.util.AuthorizationUtil;
import site.easy.to.build.crm.util.FileUtil;

import java.util.List;

@Controller
@RequestMapping("/budget")
public class BudgetController {
    private final BudgetRepository budgetRepository;
    private final CustomerRepository customerRepository;
    private final LeadService leadService;
    private final TicketRepository ticketRepository;
    private final FileUtil fileUtil;

    public BudgetController(BudgetRepository budgetRepository, CustomerRepository customerRepository,
                            LeadService leadService, TicketRepository ticketRepository,
                            FileUtil fileUtil) {
        this.budgetRepository = budgetRepository;
        this.customerRepository = customerRepository;
        this.leadService = leadService;
        this.ticketRepository = ticketRepository;
        this.fileUtil = fileUtil;
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
    public String validateConfirm(HttpSession session, Authentication authentication,
                                  HttpServletRequest request) {
        if (session.getAttribute("ticket") != null) {
            ticketRepository.save((Ticket) session.getAttribute("ticket"));
            return "redirect:/employee/ticket/assigned-tickets";
        }
        else if (session.getAttribute("lead") != null) {
            Lead lead = (Lead) session.getAttribute("lead");
            Lead createdLead = leadService.save(lead);
            List<Attachment> allFiles = (List<Attachment>) session.getAttribute("all_files");
            fileUtil.saveFiles(allFiles, createdLead);

            if (lead.getGoogleDrive() != null) {
                String folderId = (String) session.getAttribute("folder_id");
                fileUtil.saveGoogleDriveFiles(authentication, allFiles, folderId, createdLead);
            }
            if (lead.getStatus().equals("meeting-to-schedule")) {
                return "redirect:/employee/calendar/create-event?leadId=" + lead.getLeadId();
            }
            if(AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
                return "redirect:/employee/lead/created-leads";
            }
            return "redirect:/employee/lead/assigned-leads";
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }
}
