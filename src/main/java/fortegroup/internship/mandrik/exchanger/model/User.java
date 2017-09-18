package fortegroup.internship.mandrik.exchanger.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.internal.NotNull;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.*;

/**
 * Represent the User as entity with technology JPA.
 * Represent table named 'user'
 * Created by Nick Mandrik on 22.08.2017.
 * @author Nick Mandrik
 */
@Entity
@Table(name = "user")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
        @NamedQuery(name="User.findByLogin",
                query="SELECT u FROM User u WHERE u.login LIKE :custLogin"),
        @NamedQuery(name="User.findUsersMatchByLogin",
                query="SELECT u FROM User u WHERE u.login LIKE CONCAT('%',:nameMatcher,'%')"),
        @NamedQuery(name="User.findAssignDocuments",
                query="SELECT d FROM Document d WHERE d.id IN " +
                        "(SELECT a.document.id FROM Assignment a WHERE a.user.login = :custUserLogin)")
})
public class User implements Serializable {

    /**
     * fields in table.
     */

    @XmlAttribute
    @NotNull
    private String login;
    @XmlAttribute
    @NotNull
    private String role;
    @XmlAttribute
    @NotNull
    private String password;

    /**
     * represent connect one to many with Assignment entity to the documents.
     */
    @XmlElement(name="assignment")
    @XmlIDREF
    private Collection<Assignment> assignments;


    /**
     * Id column named 'login'.
     */
    @Id
    @Column(name = "login")
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    /**
     * Basic column named 'role'.
     */
    @Basic
    @Column(name = "role", nullable = false)
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    /**
     * Basic column named 'password'.
     */
    @Basic
    @Column(name = "password", nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Connection with Assignment entity.
     * Mapped by 'user'.
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    @JsonManagedReference(value="user-assignment")
    public Collection<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(Collection<Assignment> assignments) {
        this.assignments = assignments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!Objects.equals(login, user.login)) return false;
        if (role != null ? !role.equals(user.role) : user.role != null) return false;
        if (login != null ? !login.equals(user.login) : user.login != null) return false;
        return assignments != null ? assignments.equals(user.assignments) : user.assignments == null;
    }

    @Override
    public int hashCode() {
        int result = (role != null ? role.hashCode() : 0);
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (assignments != null ? assignments.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder();
        stb.append("User{" +
                "login='" + login + '\'' +
                ", role='" + role + '\'' +
                ", password='" + password + '\'' +
                ", assignment_ids=[");

        StringBuilder stbAssignments = new StringBuilder();
        if(assignments != null) {
            boolean isFirstAssignment = true;
            for (Assignment assignment : assignments) {
                stbAssignments.append(" " + assignment.getId() + ",");
                if(isFirstAssignment) {
                    isFirstAssignment = false;
                    stbAssignments.deleteCharAt(0);
                }
            }
            if(stbAssignments.length() != 0) {
                stbAssignments.deleteCharAt(stbAssignments.length() - 1);
            }
        }
        stb.append(stbAssignments);

        stb.append("]}");
        return  stb.toString();
    }
}
