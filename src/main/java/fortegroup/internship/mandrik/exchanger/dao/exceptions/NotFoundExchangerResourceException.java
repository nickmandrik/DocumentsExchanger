package fortegroup.internship.mandrik.exchanger.dao.exceptions;

import java.io.Serializable;

/**
 * {@link NotFoundExchangerResourceException} be thrown only by one reason: info about some entity doesn't exist.
 * Created by Nick Mandrik on 22.08.2017.
 * @author Nick Mandrik
 */
public class NotFoundExchangerResourceException extends Exception implements Serializable {

    public NotFoundExchangerResourceException(String message) {
        super(message);
    }

}