package ch.swissbytes.module.buho.service;

import ch.swissbytes.domain.dao.PositionPointerDao;
import ch.swissbytes.domain.entities.PositionPointer;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class PositionPointerService {

    @Inject
    private PositionPointerDao positionPointerDao;


    public void update(PositionPointer positionPointer) {
        positionPointerDao.update(positionPointer);
    }
}
