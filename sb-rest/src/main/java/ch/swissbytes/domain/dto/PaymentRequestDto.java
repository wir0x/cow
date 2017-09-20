package ch.swissbytes.domain.dto;

import ch.swissbytes.domain.entities.PaymentRequest;
import ch.swissbytes.domain.entities.PaymentRequestItem;
import ch.swissbytes.domain.enumerator.PaymentStatusEnum;
import ch.swissbytes.ewallet.tigomoney.dto.TMPaymentItemDto;
import ch.swissbytes.ewallet.tigomoney.dto.TMPaymentRequestDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentRequestDto extends TMPaymentRequestDto {
    private Long userId;
    private Long deviceId;
    private Long servicePlanId;
    private Date creationDate;
    private PaymentStatusEnum status;
    private String statusDetail;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getServicePlanId() {
        return servicePlanId;
    }

    public void setServicePlanId(Long servicePlanId) {
        this.servicePlanId = servicePlanId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public PaymentStatusEnum getStatus() {
        return status;
    }

    public void setStatus(PaymentStatusEnum status) {
        this.status = status;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public PaymentRequest toPaymentRequestEntity() {
        PaymentRequest request = new PaymentRequest();
        request.setId(Long.getLong(this.getOrderId()));
        request.setStatus(this.getStatus());
        request.setAmount(this.getAmount());
        request.setBusinessName(this.getBusinessName());
        request.setConfirmation(this.getConfirmation());
        request.setCorrectUrl(this.getCorrectUrl());
        request.setCreationDate(this.getCreationDate());
        request.setDocumentNumber(this.getDocumentNumber());
        request.setErrorUrl(this.getErrorUrl());
        request.setLine(this.getLine());
        request.setMessage(this.getMessage());
        request.setName(this.getName());
        request.setNit(this.getNit());
        request.setNotification(this.getNotification());

        return request;
    }

    public static PaymentRequestDto newFromEntity(PaymentRequest request, List<PaymentRequestItem> items, String orderId) {
        PaymentRequestDto requestDto = new PaymentRequestDto();
        requestDto.setStatus(request.getStatus());
        requestDto.setAmount(request.getAmount());
        requestDto.setBusinessName(request.getBusinessName());
        requestDto.setConfirmation(request.getConfirmation());
        requestDto.setCorrectUrl(request.getCorrectUrl());
        requestDto.setCreationDate(request.getCreationDate());
        requestDto.setDocumentNumber(request.getDocumentNumber());
        requestDto.setErrorUrl(request.getErrorUrl());
        requestDto.setLine(request.getLine());
        requestDto.setMessage(request.getMessage());
        requestDto.setName(request.getName());
        requestDto.setNit(request.getNit());
        requestDto.setNotification(request.getNotification());

        requestDto.setOrderId(orderId);
        requestDto.setStatusDetail(request.getStatusDetail());

        List<TMPaymentItemDto> itemDtos = new ArrayList<>();
        for (PaymentRequestItem item : items) {
            TMPaymentItemDto itemDto = TMPaymentItemDto.createNew(item.getSerial()
                    , item.getQuantity()
                    , item.getConcept()
                    , item.getUnitPrice()
                    , item.getTotalPrice());
            itemDtos.add(itemDto);
        }
        requestDto.setItems(itemDtos);
        return requestDto;
    }
}
