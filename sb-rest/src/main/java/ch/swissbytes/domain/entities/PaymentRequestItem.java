package ch.swissbytes.domain.entities;

import ch.swissbytes.module.shared.utils.LongUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "payment_request_item")
@Getter
@Setter
public class PaymentRequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "serial", nullable = false, length = 3)
    private String serial;

    @Column(name = "quantity", nullable = false, length = 3)
    private Integer quantity;

    @Column(name = "concept", nullable = false, length = 255)
    private String concept;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @ManyToOne
    @JoinColumn(name = "payment_request_id", foreignKey = @ForeignKey(name = "fk_payment_request_item_payment_request_id"))
    private PaymentRequest paymentRequest;

    public boolean isNew() {
        return LongUtil.isEmpty(this.id);
    }
}
