
package fortegroup.internship.mandrik.docstorage.service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the fortegroup.internship.mandrik.docstorage.service package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Status_QNAME = new QName("http://service.docstorage.mandrik.internship.fortegroup/", "status");
    private final static QName _Document_QNAME = new QName("http://service.docstorage.mandrik.internship.fortegroup/", "document");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: fortegroup.internship.mandrik.docstorage.service
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DocumentDetails }
     * 
     */
    public DocumentDetails createDocumentDetails() {
        return new DocumentDetails();
    }

    /**
     * Create an instance of {@link Status }
     * 
     */
    public Status createStatus() {
        return new Status();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Status }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.docstorage.mandrik.internship.fortegroup/", name = "status")
    public JAXBElement<Status> createStatus(Status value) {
        return new JAXBElement<Status>(_Status_QNAME, Status.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocumentDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.docstorage.mandrik.internship.fortegroup/", name = "document")
    public JAXBElement<DocumentDetails> createDocument(DocumentDetails value) {
        return new JAXBElement<DocumentDetails>(_Document_QNAME, DocumentDetails.class, null, value);
    }

}
