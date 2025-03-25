DELIMITER //
CREATE PROCEDURE GenerateBudgets(IN num INT)
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE random_customer_id INT;

    WHILE i < num DO
            SELECT customer_id INTO random_customer_id
            FROM customer
            ORDER BY RAND()
            LIMIT 1;

            INSERT INTO budget (description, amount, customer_id, created_at)
            VALUES (
                       CONCAT('Budget for Customer ', random_customer_id),
                       RAND() * 100000 + 1000000,
                       random_customer_id,
                       DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY)
                   );

            SET i = i + 1;
        END WHILE;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE GenerateTickets(IN num INT)
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE new_expense_id INT;
    DECLARE random_customer_id INT unsigned;
    DECLARE random_budget_id INT;
    DECLARE random_employee_id INT;
    DECLARE random_ticket_id INT;

    WHILE i < num DO
            SELECT customer_id,budget_id INTO random_customer_id,random_budget_id
            FROM budget
            ORDER BY RAND()
            LIMIT 1;

            SELECT id INTO random_employee_id
            FROM v_employe
            ORDER BY RAND()
            LIMIT 1;

            SET random_ticket_id = FLOOR(RAND() * 10000);

            SET new_expense_id = LAST_INSERT_ID();

            INSERT INTO trigger_ticket (subject, status, priority, customer_id, employee_id, created_at, expense_amount)
            VALUES (
                        CONCAT('Ticket ', random_ticket_id),
                       CASE FLOOR(RAND() * 10)
                           WHEN 0 THEN 'open'
                           WHEN 1 THEN 'assigned'
                           WHEN 2 THEN 'on-Hold'
                           WHEN 3 THEN 'in-Progress'
                           WHEN 4 THEN 'resolved'
                           WHEN 5 THEN 'closed'
                           WHEN 6 THEN 'reopened'
                           WHEN 7 THEN 'pending-customer-response'
                           WHEN 8 THEN 'escalated'
                           WHEN 9 THEN 'archived'
                           END,
                       CASE FLOOR(RAND() * 6)
                           WHEN 0 THEN 'low'
                           WHEN 1 THEN 'medium'
                           WHEN 2 THEN 'high'
                           WHEN 3 THEN 'closed'
                           WHEN 4 THEN 'urgent'
                           WHEN 5 THEN 'critical'
                           END,
                       random_customer_id,
                       random_employee_id,
                       TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY)
                           - INTERVAL FLOOR(RAND() * 24) HOUR
                           - INTERVAL FLOOR(RAND() * 60) MINUTE),
                        RAND() * 10000 + 1000000
                   );

            SET i = i + 1;
        END WHILE;
END //
DELIMITER ;


DELIMITER //
CREATE PROCEDURE GenerateLeads(IN num INT)
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE new_expense_id INT;
    DECLARE random_customer_id INT unsigned;
    DECLARE random_budget_id INT;
    DECLARE random_employee_id INT;
    DECLARE random_lead_id INT;

    WHILE i < num DO
            SELECT customer_id,budget_id INTO random_customer_id,random_budget_id
            FROM budget
            ORDER BY RAND()
            LIMIT 1;

            SELECT id INTO random_employee_id
            FROM v_employe
            ORDER BY RAND()
            LIMIT 1;

            SET random_lead_id = FLOOR(RAND() * 10000);

            SET new_expense_id = LAST_INSERT_ID();

            INSERT INTO trigger_lead (name, status, customer_id, employee_id, created_at, expense_amount)
            VALUES (
                        CONCAT('LEAD', random_lead_id),
                       CASE FLOOR(RAND() * 5)
                           WHEN 0 THEN 'meeting-to-schedule'
                           WHEN 1 THEN 'scheduled'
                           WHEN 2 THEN 'archived'
                           WHEN 3 THEN 'success'
                           WHEN 4 THEN 'assign-to-sales'
                           END,
                       random_customer_id,
                       random_employee_id,
                       TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY)
                           - INTERVAL FLOOR(RAND() * 24) HOUR
                           - INTERVAL FLOOR(RAND() * 60) MINUTE),
                        RAND() * 10000 + 1000000
                   );

            SET i = i + 1;
        END WHILE;
END //
DELIMITER ;

-- CALL GenerateBudgets(100);
-- CALL GenerateTickets(50);
-- CALL GenerateLeads(50);