package site.easy.to.build.crm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Ticket;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    public Ticket findByTicketId(int ticketId);

    public List<Ticket> findByManagerId(int id);

    public List<Ticket> findByEmployeeId(int id);

    List<Ticket> findByCustomerCustomerId(Integer customerId);

    List<Ticket> findByManagerIdOrderByCreatedAtDesc(int managerId, Pageable pageable);

    List<Ticket> findByEmployeeIdOrderByCreatedAtDesc(int managerId, Pageable pageable);

    List<Ticket> findByCustomerCustomerIdOrderByCreatedAtDesc(int customerId, Pageable pageable);

    long countByEmployeeId(int employeeId);

    long countByManagerId(int managerId);

    long countByCustomerCustomerId(int customerId);

    List<Ticket> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    void deleteAllByCustomer(Customer customer);

    @Query("SELECT COALESCE(SUM(l.depense), 0) FROM Ticket l WHERE l.customer = :customer")
    double findTotalAmountByCustomer(Customer customer);

    @Query("SELECT COALESCE(SUM(l.depense), 0) FROM Ticket l WHERE l.createdAt <= :createdAt and l.customer = :customer")
    public double findTotalDepenseBeforeCreatedAt(@Param("createdAt") LocalDateTime createdAt, @Param("customer")  Customer customer);

    public List<Ticket> findByCreatedAtBeforeOrderByCreatedAtDesc(LocalDateTime date);

    public List<Ticket> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime date);
}
