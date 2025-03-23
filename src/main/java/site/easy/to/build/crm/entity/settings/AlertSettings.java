package site.easy.to.build.crm.entity.settings;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "alert_settings")
@Getter
@Setter
public class AlertSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private int alertSettingsId;

    @Column(name = "rate", precision = 5, scale = 2)
    private double rate;

    public AlertSettings() {
    }

    public AlertSettings(int alertSettingsId, double rate) {
        this.alertSettingsId = alertSettingsId;
        this.rate = rate;
    }
}
