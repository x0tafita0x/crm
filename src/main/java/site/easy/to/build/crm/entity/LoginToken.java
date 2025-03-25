package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_token")
@Getter
@Setter
public class LoginToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private int tokenId;

    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expireAt;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public LoginToken() {
    }

    public LoginToken(int tokenId, String token, LocalDateTime createdAt, LocalDateTime expireAt, User user) {
        this.tokenId = tokenId;
        this.token = token;
        this.createdAt = createdAt;
        this.expireAt = expireAt;
        this.user = user;
    }
}
