package site.easy.to.build.crm.service.data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DataServiceImpl implements DataService {
    private final JdbcTemplate jdbcTemplate;
    private final String resetScript;

    public DataServiceImpl(JdbcTemplate jdbcTemplate,
                           @Value("${database.reset.script}") String resetScript) {
        this.jdbcTemplate = jdbcTemplate;
        this.resetScript = resetScript;
    }

    @Transactional
    public void reset() throws Exception {
        try {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

            Resource resource = new ClassPathResource(resetScript);
            String sql = new String(Files.readAllBytes(Paths.get(resource.getURI())));

            jdbcTemplate.execute(sql);
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
