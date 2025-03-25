package site.easy.to.build.crm.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ImportUtil {
    public static void createTemporaryTableScript(String tableName, JdbcTemplate jdbcTemplate) {
        String getTableDefinitionSql = "DESCRIBE " + tableName;

        List<TableColumn> columns = jdbcTemplate.query(getTableDefinitionSql,
                (rs, rowNum) -> new TableColumn(rs.getString("Field"), rs.getString("Type")));

        StringBuilder createTableSql = new StringBuilder("CREATE TEMPORARY TABLE " + tableName + "_temp (");

        for (TableColumn column : columns) {
            createTableSql.append(column.getName()).append(" ").append(column.getType()).append(", ");
        }

        createTableSql.setLength(createTableSql.length() - 2);
        createTableSql.append(")");

        jdbcTemplate.execute(createTableSql.toString());
    }

    public static void importCsv(MultipartFile file, String tableName, List<String> errorMessages,
                                                  JdbcTemplate jdbcTemplate) throws IOException {
        StringBuilder insertSql = new StringBuilder("INSERT INTO " + tableName + "_temp (");

        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

            List<String> headers = new ArrayList<>(csvParser.getHeaderMap().keySet());

            for (String header : headers) {
                insertSql.append(header).append(", ");
            }

            insertSql = new StringBuilder(insertSql.substring(0, insertSql.length() - 2));
            insertSql.append(") VALUES (");

            insertSql.append("?, ".repeat(headers.size()));

            insertSql = new StringBuilder(insertSql.substring(0, insertSql.length() - 2));
            insertSql.append(")");

            int lineNumber = 1;
            for (CSVRecord record : csvParser) {
                Object[] values = new Object[headers.size()];
                for (int i = 0; i < headers.size(); i++) {
                    values[i] = record.get(headers.get(i));
                }

                try {
                    jdbcTemplate.update(insertSql.toString(), values);
                } catch (DataAccessException ex) {
                    String errorMessage = "Error inserting line " + lineNumber + ": " + ex.getMessage();
                    errorMessages.add(errorMessage);
                }

                lineNumber++;
            }
        }
    }

    public static void moveData(String tableName, JdbcTemplate jdbcTemplate) {
        String moveDataSql = "INSERT INTO " + tableName + " SELECT * FROM " + tableName + "_temp";
        jdbcTemplate.update(moveDataSql);
    }

    public static void dropTemporaryTable(String tableName, JdbcTemplate jdbcTemplate) {
        String dropTableSql = "DROP TEMPORARY TABLE IF EXISTS " + tableName + "_temp";
        jdbcTemplate.execute(dropTableSql);
    }

    static class TableColumn {
        private final String name;
        private final String type;

        public TableColumn(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }
}
