package site.easy.to.build.crm.controller.api;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import site.easy.to.build.crm.entity.DashboardData;
import site.easy.to.build.crm.entity.DateParameter;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.service.api.ApiLoginService;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.ticket.TicketService;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final BudgetService budgetService;
    private final LeadService leadService;
    private final TicketService ticketService;
    private final ApiLoginService loginService;
    private final ApiLoginService apiLoginService;

    public ApiController(BudgetService budgetService, LeadService leadService, TicketService ticketService,
                         ApiLoginService loginService, ApiLoginService apiLoginService) {
        this.budgetService = budgetService;
        this.leadService = leadService;
        this.ticketService = ticketService;
        this.loginService = loginService;
        this.apiLoginService = apiLoginService;
    }

    @PostMapping("/dashboard")
    public ResponseEntity<?> dashboard(@RequestBody DateParameter request,
                                       @RequestHeader("Authorization") String token) {
        try {
            apiLoginService.verifyToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }

        DashboardData datas=new DashboardData();
        datas.setLeads(leadService.getLeadBetween(request.getMin(), request.getMax()));
        datas.setBudgets(budgetService.getBudgetBetween(request.getMin(), request.getMax()));
        datas.setTickets(ticketService.getTicketBetween(request.getMin(), request.getMax()));
        return ResponseEntity.ok(datas);
    }

    @PostMapping("/updateLead")
    public ResponseEntity<?> updateLead(@RequestBody Lead request,
                                        @RequestHeader("Authorization") String token) {
        try {
            apiLoginService.verifyToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }

        try {
            // Find the lead by ID
            Lead lead = leadService.findByLeadId(request.getLeadId());
    
            if (lead == null) {
                // If the lead is not found, return a 404 error
                return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("Lead not found with ID: " + request.getLeadId());
            }
    
            // Update the lead's amount with the value from the request
            lead.setDepense(request.getDepense());
    
            // Save the updated lead
            leadService.save(lead);
    
            // Return the updated lead as a response
            return ResponseEntity.ok(lead);
    
        } catch (Exception e) {
            // Handle unexpected exceptions and return a 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the lead: " + e.getMessage());
        }
    }
    @PostMapping("/updateTicket")
    public ResponseEntity<?> updateTicket(@RequestBody Ticket request,
                                          @RequestHeader("Authorization") String token) {
        try {
            apiLoginService.verifyToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }

        try {
            // Find the ticket by ID
            Ticket ticket = ticketService.findByTicketId(request.getTicketId());

            if (ticket == null) {
                // If the ticket is not found, return 404 Not Found
                return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("Ticket not found with ID: " + request.getTicketId());
            }

            ticket.setDepense(request.getDepense());  // Assuming 'amount' is a field to update

            // Save the updated ticket
            ticketService.save(ticket);

            // Return the updated ticket as a response
            return ResponseEntity.ok(ticket);

        } catch (Exception e) {
            // Handle unexpected exceptions and return a 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the ticket: " + e.getMessage());
        }
    }
    @GetMapping("/deleteLead/{leadId}")
    public ResponseEntity<?> deleteLead(@PathVariable int leadId,
                                        @RequestHeader("Authorization") String token) {
        try {
            apiLoginService.verifyToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }

        try {
            Lead lead = leadService.findByLeadId(leadId);
    
            if (lead == null) {
                // If the lead is not found, return a 404 error
                return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("Lead not found with ID: " + leadId);
            }
            // Call service to delete the lead by ID
            leadService.delete(lead);
            return ResponseEntity.ok("Lead deleted successfully");
        } catch (Exception e) {
            // Handle unexpected exceptions and return 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the lead: " + e.getMessage());
        }
    }
    @GetMapping("/deleteTicket/{ticketId}")
    public ResponseEntity<?> deleteTicket(@PathVariable int ticketId,
                                          @RequestHeader("Authorization") String token) {
        try {
            apiLoginService.verifyToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }

        try {
            // Find the ticket by its ID
            Ticket ticket = ticketService.findByTicketId(ticketId);

            if (ticket == null) {
                // If the ticket is not found, return a 404 error
                return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("Ticket not found with ID: " + ticketId);
            }

            // Call service to delete the ticket by ID
            ticketService.delete(ticket);
            return ResponseEntity.ok("Ticket deleted successfully");
        } catch (Exception e) {
            // Handle unexpected exceptions and return 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the ticket: " + e.getMessage());
        }
    }

}
