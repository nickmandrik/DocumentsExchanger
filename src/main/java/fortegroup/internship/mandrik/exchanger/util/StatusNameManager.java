package fortegroup.internship.mandrik.exchanger.util;


import java.util.ResourceBundle;

/**
 * Class MessageManager use ResourceBundle to get properties
 * that saved in messages.properties
 * Created by Nick Mandrik on 24.08.2017.
 * @author Nick Mandrik
 */
public class StatusNameManager {
    private final static ResourceBundle resourceBundle = ResourceBundle.getBundle("status");

    public StatusNameManager() {
    }

    public static String getStatusName(String key) {
        return resourceBundle.getString(key);
    }
}
