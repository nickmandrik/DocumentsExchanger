package fortegroup.internship.mandrik.exchanger.model;



import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

/**
 * Represent the Document entity with technology JPA.
 * Represent table named 'document'
 * Created by Nick Mandrik on 22.08.2017.
 * @author Nick Mandrik
 */
@Entity
@Table(name = "document")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
        @NamedQuery(name="Document.findByPathAndName",
                query="SELECT d FROM Document d WHERE d.path LIKE :custPath AND d.name LIKE :custName"),
        @NamedQuery(name="Document.findDocumentsMatchByName",
                query="SELECT d FROM Document d WHERE d.name LIKE CONCAT('%', :nameMatcher, '%')"),
        @NamedQuery(name="Document.findAssignmentUsers",
                query="SELECT u FROM User u WHERE u.login IN " +
                        "(SELECT a.user.login FROM Assignment a WHERE a.document.id = :custDocumentId)")
})
public class Document implements Serializable {

    public Document() {}

    public Document(long id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }
    /**
     * fields in table.
     */
    @XmlAttribute
    private long id;
    @XmlAttribute
    private String path;
    @XmlAttribute
    private String name;

    /**
     * represent connect one to many with Assignment entity to the documents.
     */
    @XmlElement(name="assignment")
    @XmlIDREF
    private Collection<Assignment> assignments;

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
     * Basic column named 'path'.
     */
    @Basic
    @Column(name = "path", nullable = false)
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Basic column named 'model'.
     */
    @Basic
    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Connection with Assignment entity.
     * Mapped by 'document'.
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "document")
    @JsonManagedReference(value="doc-assignment")
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

        Document document = (Document) o;

        if (!Objects.equals(id, document.id)) return false;
        if (path != null ? !path.equals(document.path) : document.path != null) return false;
        if (name != null ? !name.equals(document.name) : document.name != null) return false;
        return assignments != null ? assignments.equals(document.assignments) : document.assignments == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (assignments != null ? assignments.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder();
        stb.append("Document{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
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
