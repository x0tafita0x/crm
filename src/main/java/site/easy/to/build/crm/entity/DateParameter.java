package site.easy.to.build.crm.entity;

import java.time.LocalDateTime;

public class DateParameter {
    private LocalDateTime min;
    
    private LocalDateTime max;

    public DateParameter() {
    }

    public DateParameter(LocalDateTime min, LocalDateTime max) {
        this.min = min;
        this.max = max;
    }
    
    public LocalDateTime getMin() {
        return min;
    }
    public void setMin(LocalDateTime min) {
        this.min = min;
    }
    public LocalDateTime getMax() {
        return max;
    }
    public void setMax(LocalDateTime max) {
        this.max = max;
    }

}
