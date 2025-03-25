package site.easy.to.build.crm.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class ApiException extends Exception {
    HashMap<String, Object> errors;

    public ApiException(String message, HashMap<String, Object> errors) {
        super(message);
        this.errors = errors;
    }
}
