package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.easy.to.build.crm.entity.LoginToken;

public interface LoginTokenRepository extends JpaRepository<LoginToken, Integer> {
    @Query("select l from LoginToken l where l.token = :token order by l.tokenId limit 1")
    LoginToken getUserToken(@Param("token") String token);
}
