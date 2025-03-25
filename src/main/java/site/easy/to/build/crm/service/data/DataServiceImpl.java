package site.easy.to.build.crm.service.data;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;
import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.exception.DataException;
import site.easy.to.build.crm.repository.*;
import site.easy.to.build.crm.util.AuthenticationUtils;
import site.easy.to.build.crm.util.AuthorizationUtil;
import site.easy.to.build.crm.util.ImportUtil;
import site.easy.to.build.crm.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Service
public class DataServiceImpl implements DataService {
    private final JdbcTemplate jdbcTemplate;
    private final String resetScript;
    private final Validator validator;
    private final CustomerRepository customerRepository;
    private final BudgetRepository budgetRepository;
    private final AuthenticationUtils authenticationUtils;
    private final UserRepository userRepository;
    private final LeadRepository leadRepository;
    private final TicketRepository ticketRepository;

    public DataServiceImpl(JdbcTemplate jdbcTemplate,
                           @Value("${database.reset.script}") String resetScript,
                           Validator validator,
                           CustomerRepository customerRepository,
                           BudgetRepository budgetRepository,
                           AuthenticationUtils authenticationUtils,
                           UserRepository userRepository,
                           LeadRepository leadRepository,
                           TicketRepository ticketRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.resetScript = resetScript;
        this.validator = validator;
        this.customerRepository = customerRepository;
        this.budgetRepository = budgetRepository;
        this.authenticationUtils = authenticationUtils;
        this.userRepository = userRepository;
        this.leadRepository = leadRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Transactional
    public void multipleImport(MultipartFile customerFile, MultipartFile ticketFile, MultipartFile budgetFile,
                               Authentication authentication) throws IOException, CsvValidationException, DataException {
        HashMap<String, Customer> customerMap = importCustomer(customerFile, authentication);
        importBudget(budgetFile, customerMap);
        importTicketLead(ticketFile, customerMap, authentication);
    }

    @Override
    public void importTicketLead(MultipartFile file, HashMap<String, Customer> customerMap,
                                 Authentication authentication) throws IOException, DataException, CsvValidationException {
        List<String> errors = new ArrayList<>();
        List<Ticket> tickets = new ArrayList<>();
        List<Lead> leads = new ArrayList<>();

        CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()));
        String[] nextLine;
        reader.readNext();
        int i = 1;
        while ((nextLine = reader.readNext()) != null) {
            switch (nextLine[2]) {
                case "lead":
                    leads.add(parseLead(nextLine, customerMap, authentication, errors, i, file.getOriginalFilename()));
                    break;
                case "ticket":
                    tickets.add(parseTicket(nextLine, customerMap, authentication, errors, i, file.getOriginalFilename()));
                    break;
                default:
                    errors.add(file.getOriginalFilename() + " : Error in line : " + i + " : " + nextLine[2] + " is not a valid type");
                    break;
            }
            i++;
        }

        if (!errors.isEmpty()) {
            throw new DataException(errors);
        }

        leadRepository.saveAll(leads);
        ticketRepository.saveAll(tickets);
    }

    private Ticket parseTicket(String[] line, HashMap<String, Customer> customerMap,
                               Authentication authentication, List<String> errors, int i, String fileName) {
        Ticket ticket = new Ticket();
        try {
            ticket.setDepense(StringUtils.parseDouble(line[4]));
            ticket.setCustomer(findCustomer(line[0], customerMap));
            ticket.setCreatedAt(LocalDateTime.now());

            if (AuthorizationUtil.hasRole(authentication, "ROLE_EMPLOYEE")) {
                int userId = authenticationUtils.getLoggedInUserId(authentication);
                User user = userRepository.findById(userId);
                ticket.setEmployee(user);
            }
            else {
                Random random = new Random();
                List<User> employees = userRepository.findAll();
                ticket.setEmployee(employees.get(random.nextInt(employees.size())));
            }

            List<String> priorities = List.of("low", "medium", "high", "closed", "urgent", "critical");

            Random random = new Random();
            String randomPriority = priorities.get(random.nextInt(priorities.size()));
            ticket.setPriority(randomPriority);
            ticket.setSubject(line[1]);
            ticket.setStatus(line[3]);

            BindingResult bindingResult = new BeanPropertyBindingResult(ticket, "ticket");
            validator.validate(ticket, bindingResult);
            if (bindingResult.hasErrors()) {
                List<String> messages = bindingResult.getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList();
                errors.add(fileName + " : Error in line " + i + " : " + String.join(",", messages));
            }
        } catch (Exception e) {
            errors.add(fileName + " : Error in line : " + i + " : " + e.getMessage());
        }
        return ticket;
    }

    private Lead parseLead(String[] line, HashMap<String, Customer> customerMap,
                               Authentication authentication, List<String> errors, int i, String fileName) {
        Lead lead = new Lead();
        try {
            lead.setDepense(StringUtils.parseDouble(line[4]));
            lead.setCustomer(findCustomer(line[0], customerMap));
            lead.setCreatedAt(LocalDateTime.now());
            lead.setStatus(line[3]);
            lead.setName(line[1]);

            if (AuthorizationUtil.hasRole(authentication, "ROLE_EMPLOYEE")) {
                int userId = authenticationUtils.getLoggedInUserId(authentication);
                User user = userRepository.findById(userId);
                lead.setEmployee(user);
            }
            else {
                Random random = new Random();
                List<User> employees = userRepository.findAll();
                lead.setEmployee(employees.get(random.nextInt(employees.size())));
            }

            BindingResult bindingResult = new BeanPropertyBindingResult(lead, "lead");
            validator.validate(lead, bindingResult);
            if (bindingResult.hasErrors()) {
                List<String> messages = bindingResult.getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList();
                errors.add(fileName + " : Error in line " + i + " : " + String.join(",", messages));
            }
        } catch (Exception e) {
            errors.add(fileName + " : Error in line : " + i + " : " + e.getMessage());
        }
        return lead;
    }

    @Override
    public void importBudget(MultipartFile budgetFile, HashMap<String, Customer> customerMap) throws IOException, DataException, CsvValidationException {
        List<String> errors = new ArrayList<>();
        List<Budget> budgets = new ArrayList<>();

        CSVReader reader = new CSVReader(new InputStreamReader(budgetFile.getInputStream()));
        String[] nextLine;
        reader.readNext();
        int i = 1;
        while ((nextLine = reader.readNext()) != null) {
            Budget budget = new Budget();
            try {
                budget.setAmount(StringUtils.parseDouble(nextLine[1]));
                budget.setCustomer(findCustomer(nextLine[0], customerMap));
                budget.setCreatedAt(LocalDateTime.now());
                BindingResult bindingResult = new BeanPropertyBindingResult(budget, "budget");
                validator.validate(budget, bindingResult);
                if (bindingResult.hasErrors()) {
                    List<String> messages = bindingResult.getAllErrors().stream()
                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .toList();
                    errors.add(budgetFile.getOriginalFilename() + " : Error in line " + i + " : " + String.join(",", messages));
                }
                budgets.add(budget);
            } catch (Exception e) {
                errors.add("Error in line : " + i + " : " + e.getMessage());
            }
            i++;
        }

        if (!errors.isEmpty()) {
            throw new DataException(errors);
        }

        budgetRepository.saveAll(budgets);
    }

    private Customer findCustomer(String email, HashMap<String, Customer> customerMap) throws Exception {
        if (customerMap.containsKey(email)) {
            return customerMap.get(email);
        }
        throw new Exception("no customer found with email : " + email);
    }

    @Override
    public HashMap<String, Customer> importCustomer(MultipartFile customerFile, Authentication authentication) throws IOException, CsvValidationException, DataException {
        List<String> errors = new ArrayList<>();
        List<Customer> customers = new ArrayList<>();
        HashMap<String, Customer> customerMap = new HashMap<>();

        CSVReader reader = new CSVReader(new InputStreamReader(customerFile.getInputStream()));
        String[] nextLine;
        reader.readNext();
        int i = 1;
        while ((nextLine = reader.readNext()) != null) {
            Customer customer = new Customer();
            customer.setEmail(nextLine[0]);
            customer.setName(nextLine[1]);
            customer.setCountry("country");

            int userId = authenticationUtils.getLoggedInUserId(authentication);
            User user = userRepository.findById(userId);

            customer.setUser(user);
            BindingResult bindingResult = new BeanPropertyBindingResult(customer, "customer");
            validator.validate(customer, bindingResult);
            if (bindingResult.hasErrors()) {
                List<String> messages = bindingResult.getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList();
                errors.add(customerFile.getOriginalFilename() + " : Error in line " + i + " : " + String.join(",", messages));
            }
            customers.add(customer);
            i++;
        }

        if (!errors.isEmpty()) {
            throw new DataException(errors);
        }

        customers = customerRepository.saveAll(customers);
        for (Customer customer : customers) {
            customerMap.put(customer.getEmail(), customer);
        }
        return customerMap;
    }

    @Override
    @Transactional
    public void importCsv(MultipartFile file) throws IOException, DataException {
        String fileName = file.getOriginalFilename();
        String tableName = fileName != null ? fileName.substring(0, fileName.lastIndexOf('.')) : "temp_table";

        List<String> errorMessages = new ArrayList<>();

        ImportUtil.createTemporaryTableScript(tableName, jdbcTemplate);
        ImportUtil.importCsv(file, tableName, errorMessages, jdbcTemplate);
        ImportUtil.moveData(tableName, jdbcTemplate);
        ImportUtil.dropTemporaryTable(tableName, jdbcTemplate);

        if (!errorMessages.isEmpty()) {
            throw new DataException(errorMessages);
        }
    }

    @Transactional
    public void reset() throws Exception {
        try {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
            Resource resource = new ClassPathResource(resetScript);

            BufferedReader reader = new BufferedReader(new FileReader(Paths.get(resource.getURI()).toFile().getAbsolutePath()));
            String line;
            while ((line = reader.readLine()) != null) {
                jdbcTemplate.execute(line);
            }

            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
        } catch (Exception e) {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            throw e;
        }
    }

    @Transactional
    public void restore(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        try {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("--") || line.startsWith("#")) {
                        continue;
                    }
                    sb.append(line);

                    if (line.endsWith(";")) {
                        jdbcTemplate.execute(sb.toString());
                        sb.setLength(0);
                    }
                }
            }

            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
        } catch (Exception e) {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            throw e;
        }
    }
}
