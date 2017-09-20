package ch.swissbytes.domain.dto;

import ch.swissbytes.domain.entities.PaymentHistory;
import ch.swissbytes.module.shared.utils.DateUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class HistoryPaymentDto implements Serializable {

    private Long id;
    private String deviceName;
    private Integer line;
    private Double amount;
    private String status;
    private String creationDate;

    public static HistoryPaymentDto fromPaymentHistory(PaymentHistory paymentHistory) {
        HistoryPaymentDto historyPaymentDto = new HistoryPaymentDto();
        historyPaymentDto.id = paymentHistory.getId();
        historyPaymentDto.deviceName = paymentHistory.getDeviceName();
        historyPaymentDto.line = paymentHistory.getLine();
        historyPaymentDto.amount = paymentHistory.getAmount();
        historyPaymentDto.status = paymentHistory.getStatusPayment().toString();
        historyPaymentDto.creationDate = DateUtil.getSimpleDateTime(paymentHistory.getCreationDate());
        return historyPaymentDto;
    }
}
