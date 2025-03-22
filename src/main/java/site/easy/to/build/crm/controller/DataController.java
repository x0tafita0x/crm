package site.easy.to.build.crm.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.easy.to.build.crm.service.data.DataService;

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
}
