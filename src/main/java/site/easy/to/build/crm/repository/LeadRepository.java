package site.easy.to.build.crm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Lead;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Integer> {
    public Lead findByLeadId(int id);

    public List<Lead> findByCustomerCustomerId(int customerId);
    public List<Lead> findByManagerId(int userId);

    public List<Lead> findByEmployeeId(int userId);

    Lead findByMeetingId(String meetingId);

    List<Lead> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    public List<Lead> findByEmployeeIdOrderByCreatedAtDesc(int employeeId, Pageable pageable);

    public List<Lead> findByManagerIdOrderByCreatedAtDesc(int managerId, Pageable pageable);

    public List<Lead> findByCustomerCustomerIdOrderByCreatedAtDesc(int customerId, Pageable pageable);

    long countByEmployeeId(int employeeId);

    long countByManagerId(int managerId);
    long countByCustomerCustomerId(int customerId);

    void deleteAllByCustomer(Customer customer);

    @Query("SELECT COALESCE(SUM(l.depense), 0) FROM Lead l WHERE l.customer = :customer")
    double findTotalAmountByCustomer(Customer customer);

    @Query("SELECT COALESCE(SUM(l.depense), 0) FROM Lead l WHERE l.createdAt <= :createdAt and l.customer = :customer")
    public double findTotalDepenseBeforeCreatedAt(@Param("createdAt") LocalDateTime createdAt, @Param("customer") Customer customer);

    public List<Lead> findByCreatedAtBeforeOrderByCreatedAtDesc(LocalDateTime endDate);

    public List<Lead> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime startDate);
}
