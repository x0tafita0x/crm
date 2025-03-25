package site.easy.to.build.crm.controller;

import com.opencsv.exceptions.CsvValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import site.easy.to.build.crm.exception.DataException;
import site.easy.to.build.crm.service.data.DataService;

import java.io.IOException;

@Controller
@RequestMapping("/data")
public class DataController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DataService dataService;

    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/reset")
    public String reset(HttpServletRequest request) {
        try {
            dataService.reset();

            String referer = request.getHeader("Referer");
            return "redirect:" + referer;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "error/500";
        }
    }

    @GetMapping("/import")
    public String importData(Model model) {
        return "data/import";
    }

    @PostMapping("/import")
    public String importData(@RequestParam("customerFile") MultipartFile customerFile,
                             @RequestParam("ticketFile") MultipartFile ticketFile,
                             @RequestParam("budgetFile") MultipartFile budgetFile,
                             Model model,
                             Authentication authentication) {
        try {
            dataService.multipleImport(customerFile, ticketFile, budgetFile, authentication);
            return "redirect:/data/import";
        } catch (DataException e) {
            model.addAttribute("error_datas", e.getErrorMessages());

            return "data/error-import";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "data/error-import";
        }
    }
}
