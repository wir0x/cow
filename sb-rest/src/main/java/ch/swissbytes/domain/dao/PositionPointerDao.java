package ch.swissbytes.domain.dao;

import ch.swissbytes.domain.entities.PositionPointer;
import ch.swissbytes.domain.enumerator.AlertTypeEnum;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;
import ch.swissbytes.module.shared.utils.LongUtil;
import javafx.scene.control.Alert;

import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class PositionPointerDao extends Repository {

    public Optional<PositionPointer> getById(Integer id) {
        Long idLong = Long.valueOf(id);
        return LongUtil.isEmpty(idLong) ? Optional.absent() : getById(PositionPointer.class, idLong);
    }

    public Optional<PositionPointer> getLastFencePointerPosition() {

        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT p FROM ");
        jpql.append("PositionPointer p ");
        jpql.append("WHERE p.alertType = :alertType ");
        Map<String, Object> filters = new HashMap<>();
        filters.put("alertType", AlertTypeEnum.FENCE);
        return getBy(jpql.toString(), filters);
    }
}
