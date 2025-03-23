package site.easy.to.build.crm.repository.settings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.easy.to.build.crm.entity.settings.AlertSettings;

import java.util.Optional;

public interface AlertSettingsRepository extends JpaRepository<AlertSettings, Integer> {
    @Query("select a from AlertSettings a order by a.alertSettingsId limit 1")
    AlertSettings getSettings();
}
