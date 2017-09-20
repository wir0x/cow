package ch.swissbytes.domain.entities;

import ch.swissbytes.domain.enumerator.PaymentStatusEnum;
import ch.swissbytes.module.shared.utils.LongUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "payment_request")
@Getter
@Setter
public class PaymentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "creation_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum status;

    @Column(name = "status_detail", length = 255)
    private String statusDetail;

    @Column(name = "document_number", length = 10)
    private String documentNumber;

    @Column(name = "message", length = 100)
    private String message;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "line", nullable = false, length = 8)
    private Integer line;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "correct_url", nullable = false, length = 100)
    private String correctUrl;

    @Column(name = "error_url", nullable = false, length = 200)
    private String errorUrl;

    @Column(name = "confirmation", length = 20)
    private String confirmation;

    @Column(name = "notification", length = 20)
    private String notification;

    @Column(name = "business_name", length = 40)
    private String businessName;

    @Column(name = "nit", length = 20)
    private String nit;

    public boolean isNew() {
        return LongUtil.isEmpty(this.id);
    }
}
