package ch.swissbytes.domain.entities;


import ch.swissbytes.module.buho.app.user.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.inject.Named;
import javax.persistence.*;
import java.util.Date;

@Named
@Entity
@Table(name = "verification_token", uniqueConstraints = {@UniqueConstraint(name = "idx_verification_token", columnNames = {"token"})})
@Getter
@Setter
public class VerificationTokenEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false)
    private String token;

    @Basic
    @Column(name = "verified", nullable = false)
    private boolean verified;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_verification_token_user_id"))
    private User user;

    @Basic
    @Column(name = "expiration_date", nullable = false)
    private Date expirationDate;

    public VerificationTokenEntity() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}