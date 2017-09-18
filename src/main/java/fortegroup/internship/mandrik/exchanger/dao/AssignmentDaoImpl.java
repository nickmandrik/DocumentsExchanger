package fortegroup.internship.mandrik.exchanger.dao;

import fortegroup.internship.mandrik.exchanger.dao.exceptions.ExchangerDaoException;
import fortegroup.internship.mandrik.exchanger.dao.exceptions.NotFoundExchangerResourceException;
import fortegroup.internship.mandrik.exchanger.model.Assignment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AssignmentDaoImpl implements AssignmentDao{

    private final SessionFactory sessionFactory;

    @Autowired
    public AssignmentDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void addAssignment(Assignment assignment) throws ExchangerDaoException {
        BaseEntityDao.addEntity(sessionFactory, assignment);
    }

    @Override
    public Assignment getAssignmentById(long id) throws ExchangerDaoException, NotFoundExchangerResourceException {
        return BaseEntityDao.getEntityById(sessionFactory, id, Assignment.class);
    }

    @Override
    public List<Assignment> getAssignmentList() throws ExchangerDaoException {
        return BaseEntityDao.getEntityList(sessionFactory, Assignment.class);
    }

    @Override
    public void deleteAssignment(long id) throws ExchangerDaoException, NotFoundExchangerResourceException {
        BaseEntityDao.deleteEntity(sessionFactory, id, Assignment.class);
    }

    @Override
    public long getIdAssignDocumentToUser(long documentId, String userLogin) throws ExchangerDaoException {
        Transaction tx = null;
        Assignment assignment;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            assignment = session.createNamedQuery("Assignment.isExistAssignDocumentToUser", Assignment.class)
                    .setParameter("custDocumentId", documentId)
                    .setParameter("custUserLogin", userLogin)
                    .getSingleResult();
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
        return assignment != null ? assignment.getId() : -1;
    }
}
