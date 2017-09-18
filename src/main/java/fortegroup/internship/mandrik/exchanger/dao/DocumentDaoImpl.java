package fortegroup.internship.mandrik.exchanger.dao;

import fortegroup.internship.mandrik.exchanger.dao.exceptions.ExchangerDaoException;
import fortegroup.internship.mandrik.exchanger.dao.exceptions.NotFoundExchangerResourceException;
import fortegroup.internship.mandrik.exchanger.model.Document;
import fortegroup.internship.mandrik.exchanger.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DocumentDaoImpl implements DocumentDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public DocumentDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void addDocument(Document document) throws ExchangerDaoException {
        BaseEntityDao.addEntity(sessionFactory, document);
    }

    @Override
    public Document getDocumentById(long id) throws ExchangerDaoException, NotFoundExchangerResourceException {
        return BaseEntityDao.getEntityById(sessionFactory, id, Document.class);
    }

    @Override
    public List<Document> getDocumentList() throws ExchangerDaoException {
        return BaseEntityDao.getEntityList(sessionFactory, Document.class);
    }

    @Override
    public void deleteDocument(long id) throws ExchangerDaoException, NotFoundExchangerResourceException {
        BaseEntityDao.deleteEntity(sessionFactory, id, Document.class);
    }

    @Override
    public Document updateDocument(Document document) throws ExchangerDaoException, NotFoundExchangerResourceException {
        return BaseEntityDao.updateEntity(sessionFactory, document, document.getId(), Document.class);
    }

    @Override
    public Document getDocumentByPathAndName(String path, String name)
            throws ExchangerDaoException {
        Transaction tx = null;
        Document document;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            document = session.createNamedQuery("Document.findByPathAndName", Document.class)
                    .setParameter("custPath", path)
                    .setParameter("custName", name)
                    .getSingleResult();
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new ExchangerDaoException(e);
        }
        return document;
    }

    @Override
    public List<User> getAssignmentUsersToDocument(int documentId)
            throws ExchangerDaoException {
        Transaction tx = null;
        List<User> user;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            user = session.createNamedQuery("Document.findAssignmentUsers", User.class)
                    .setParameter("custDocumentId", documentId)
                    .getResultList();
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new ExchangerDaoException(e);
        }
        return user;
    }

    @Override
    public List<Document> getDocumentListByMathName(String matchUsername) throws ExchangerDaoException {
        List<Document> documentList;
        Transaction tx = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            documentList = session.createNamedQuery("Document.findDocumentsMatchByName", Document.class)
                    .setParameter("nameMatcher", matchUsername)
                    .getResultList();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new ExchangerDaoException(e);
        } finally {
            if(session != null) {
                session.close();
            }
        }
        return documentList;
    }
}
