package fortegroup.internship.mandrik.exchanger.dao.exceptions;

import java.io.Serializable;

/**
 * Wrapper of error in model.
 * Created by Nick Mandrik on 22.08.2017.
 * @author Nick Mandrik
 */
public class ExchangerDaoException extends Exception implements Serializable {

    public ExchangerDaoException(String message) {
        super(message);
    }

    public ExchangerDaoException( Exception e) {
        super(e);
    }

    public ExchangerDaoException(String message, Exception e) {
        super(message, e);
    }
}