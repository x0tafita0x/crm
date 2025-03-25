package site.easy.to.build.crm.entity;

import java.util.List;

public class DashboardData {
    List<Lead> leads;
    List<Budget> budgets;
    List<Ticket> tickets;
    public DashboardData(List<Lead> leads, List<Budget> budgets, List<Ticket> tickets) {
        this.leads = leads;
        this.budgets = budgets;
        this.tickets = tickets;
    }
    public DashboardData() {
    }
    public List<Lead> getLeads() {
        return leads;
    }
    public void setLeads(List<Lead> leads) {
        this.leads = leads;
    }
    public List<Budget> getBudgets() {
        return budgets;
    }
    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
    }
    public List<Ticket> getTickets() {
        return tickets;
    }
    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}
