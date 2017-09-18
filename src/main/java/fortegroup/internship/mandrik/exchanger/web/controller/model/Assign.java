package fortegroup.internship.mandrik.exchanger.web.controller.model;

import fortegroup.internship.mandrik.exchanger.model.Document;
import fortegroup.internship.mandrik.exchanger.model.User;

import java.io.Serializable;

public class Assign implements Serializable {

    private User user;
    private Document document;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Assign that = (Assign) o;

        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        return document != null ? document.equals(that.document) : that.document == null;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (document != null ? document.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Assign{" +
                "user=" + user +
                ", document=" + document +
                '}';
    }
}
