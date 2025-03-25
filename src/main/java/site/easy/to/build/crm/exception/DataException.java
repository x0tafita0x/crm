package site.easy.to.build.crm.exception;

import java.util.List;

public class DataException extends RuntimeException {
    List<String> errorMessages;

    public DataException(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
