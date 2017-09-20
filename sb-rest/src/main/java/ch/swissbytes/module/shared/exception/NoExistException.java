package ch.swissbytes.module.shared.exception;

import javax.ejb.EJBException;

/**
 * Created with IntelliJ IDEA.
 * UserEntity: timoteo
 * Date: 3/19/14
 * Time: 10:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class NoExistException extends EJBException {
    public NoExistException(String msg) {
        super(msg);
    }
}

