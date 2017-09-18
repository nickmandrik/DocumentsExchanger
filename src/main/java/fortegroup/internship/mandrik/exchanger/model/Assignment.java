package fortegroup.internship.mandrik.exchanger.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represent the Assignment as entity with technology JPA.
 * Represent table named 'assignment'.
 * Created by Nick Mandrik on 22.08.2017.
 * @author Nick Mandrik
 */
@Entity
@Table(name="assignment")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
        @NamedQuery(name="Assignment.isExistAssignDocumentToUser",
                query="SELECT a FROM Assignment a " +
                        "WHERE a.document.id = :custDocumentId AND a.user.login = :custUserLogin")
})
public class Assignment implements Serializable {

    /**
     * fields in table.
     */
    private long id;

    /**
     * represent connect many to one with User entity.
     */
    @XmlElement(name="user")
    private User user;

    /**
     * represent connect many to one with Document entity.
     */
    @XmlElement(name="document")
    private Document document;

    /**
     * Id column named 'id'.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Many to one connection with User entity.
     * By join column named 'user_id', referenced column named 'id'.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_name", referencedColumnName = "login")
    @JsonBackReference(value="user-assignment")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Many to one connection with User entity.
     * By join column named 'user_id', referenced column named 'id'.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_id", referencedColumnName = "id")
    @JsonBackReference(value="doc-assignment")
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

        Assignment that = (Assignment) o;

        if (!Objects.equals(id, that.id)) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        return document != null ? document.equals(that.document) : that.document == null;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + id +
                ", username=" + user.getLogin() +
                ", document_id=" + document.getId() +
                '}';
    }
}
