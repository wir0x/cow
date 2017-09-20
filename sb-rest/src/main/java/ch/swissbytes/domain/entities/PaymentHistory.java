package ch.swissbytes.domain.entities;

import ch.swissbytes.domain.enumerator.PaymentStatusEnum;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "payment_history")
@Getter
@Setter
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "line")
    private Integer line;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "plan")
    private String plan;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "creation_date")
    private Date creationDate;

    @Column(name = "status_payment")
    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum statusPayment;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "pay_from")
    private String payFrom;

    @ManyToOne
    @JoinColumn(name = "account_id", foreignKey = @ForeignKey(name = "fk_account_id"))
    private Account account;

    public static PaymentHistory createNew() {
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.deviceId = EntityUtil.DEFAULT_LONG;
        paymentHistory.deviceName = EntityUtil.DEFAULT_STRING;
        paymentHistory.userId = EntityUtil.DEFAULT_LONG;
        paymentHistory.userName = EntityUtil.DEFAULT_STRING;
        paymentHistory.line = EntityUtil.DEFAULT_INTEGER;
        paymentHistory.amount = EntityUtil.DEFAULT_DOUBLE;
        paymentHistory.plan = EntityUtil.DEFAULT_STRING;
        paymentHistory.creationDate = EntityUtil.DEFAULT_DATE;
        paymentHistory.status = StatusEnum.ENABLED;
        paymentHistory.account = null;
        paymentHistory.statusPayment = null;
        return paymentHistory;
    }

}
