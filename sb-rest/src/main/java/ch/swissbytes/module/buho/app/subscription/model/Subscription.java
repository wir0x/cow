package ch.swissbytes.module.buho.app.subscription.model;

import ch.swissbytes.domain.entities.PaymentRequest;
import ch.swissbytes.domain.entities.ServicePlan;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.LongUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "subscription")
@Getter
@Setter
@ToString
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "max_sms")
    private Integer maxSms;

    @Column(name = "user_pay")
    private Boolean userPay;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "shopping_cart")
    private Boolean shoppingCart;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "device_id", foreignKey = @ForeignKey(name = "fk_subscription_device_id"))
    private Device device;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "service_plan_id", foreignKey = @ForeignKey(name = "fk_subscription_service_plan_id"))
    private ServicePlan servicePlan;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "payment_request_id", foreignKey = @ForeignKey(name = "fk_subscription_payment_request_id"))
    private PaymentRequest paymentRequest;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_subscription_user_id"))
    private User user;

    public Subscription() {
    }

    public Subscription(Long id) {
        this.id = id;
    }


    public void setUser(User user) {
        this.user = user;
    }

    @Transient
    public static Subscription createNew() {
        Subscription subscription = new Subscription();
        subscription.startDate = EntityUtil.DEFAULT_DATE;
        subscription.endDate = EntityUtil.DEFAULT_DATE;
        subscription.maxSms = KeyAppConfiguration.getInt(ConfigurationKey.SMS_MAX_LIMIT);
        subscription.userPay = EntityUtil.DEFAULT_BOOLEAN_TRUE;
        subscription.shoppingCart = EntityUtil.DEFAULT_BOOLEAN;
        subscription.status = StatusEnum.DISABLED;
        subscription.device = null;
        subscription.servicePlan = null;
        subscription.paymentRequest = null;
        return subscription;
    }

    @Transient
    public boolean isNew() {
        return LongUtil.isEmpty(id);
    }
}
