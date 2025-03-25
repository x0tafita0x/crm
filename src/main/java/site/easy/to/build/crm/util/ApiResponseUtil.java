package site.easy.to.build.crm.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

public class ApiResponseUtil {
    public static ResponseEntity<?> error(int status, String message) {
        return new ResponseEntity<>(message, HttpStatus.valueOf(status));
    }

    public static ResponseEntity<?> success() {
        HashMap<String,Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", new HashMap<>());
        result.put("error", null);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public static ResponseEntity<?> success(HashMap<String, Object> data) {
        HashMap<String,Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", data);
        result.put("error", null);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public static ResponseEntity<?> error(HashMap<String, Object> errors) {
        HashMap<String,Object> result = new HashMap<>();
        result.put("status", "error");
        result.put("data", null);
        result.put("errors", errors);
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<?> error(Exception e) {
        HashMap<String,Object> result = new HashMap<>();
        result.put("status", "error");
        result.put("data", null);
        HashMap<String, Object> errors = new HashMap<>();
        errors.put("message", e.getMessage());
        result.put("errors", errors);
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

