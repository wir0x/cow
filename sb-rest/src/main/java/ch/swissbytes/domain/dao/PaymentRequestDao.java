package ch.swissbytes.domain.dao;


import ch.swissbytes.domain.entities.PaymentRequest;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;
import ch.swissbytes.module.shared.utils.LongUtil;

import javax.ejb.Stateless;

@Stateless
public class PaymentRequestDao extends Repository {

    public Optional<PaymentRequest> getById(Long id) {
        return LongUtil.isEmpty(id) ? Optional.absent() : getById(PaymentRequest.class, id);
    }
}
