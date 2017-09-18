package fortegroup.internship.mandrik.exchanger.dao;

import fortegroup.internship.mandrik.exchanger.dao.exceptions.ExchangerDaoException;
import fortegroup.internship.mandrik.exchanger.dao.exceptions.NotFoundExchangerResourceException;
import fortegroup.internship.mandrik.exchanger.model.Document;
import fortegroup.internship.mandrik.exchanger.model.User;
import org.hibernate.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

public class UserDaoImpl implements UserDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void addUser(User user) throws ExchangerDaoException {
        BaseEntityDao.addEntity(sessionFactory, user);
    }

    @Override
    public User getUserByLogin(String login) throws ExchangerDaoException, NotFoundExchangerResourceException {
        return BaseEntityDao.getEntityById(sessionFactory, login, User.class);
    }

    @Override
    public List<User> getUserList() throws ExchangerDaoException {
        return BaseEntityDao.getEntityList(sessionFactory, User.class);
    }

    @Override
    public void deleteUser(String login) throws ExchangerDaoException, NotFoundExchangerResourceException {
        BaseEntityDao.deleteEntity(sessionFactory, login, User.class);
    }

    @Override
    public User updateUser(User user) throws ExchangerDaoException, NotFoundExchangerResourceException {
        return BaseEntityDao.updateEntity(sessionFactory, user, user.getLogin(), User.class);
    }


    @Override
    public List<Document> getAssignmentDocumentsToUser(String login)
            throws ExchangerDaoException, NotFoundExchangerResourceException {
        Transaction tx = null;
        List<Document> documents = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            User user = session.load(User.class, login);
            if (user == null) {
                throw new NotFoundExchangerResourceException("das");
            }
            tx = session.beginTransaction();
            if(Objects.equals(user.getRole(), "USER")) {
                documents = session.createNamedQuery("User.findAssignDocuments", Document.class)
                        .setParameter("custUserLogin", user.getLogin())
                        .getResultList();
            } else if(Objects.equals(user.getRole(), "ADMIN")) {
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Document> criteria = builder.createQuery(Document.class);
                Root<Document> userRoot = criteria.from(Document.class);
                criteria.select(userRoot);
                documents = session.createQuery(criteria).getResultList();
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new ExchangerDaoException(e);
        } finally {
            if(session != null) {
                session.close();
            }
        }
        return documents;
    }

    @Override
    public List<User> getUserListByMathName(String matchUsername) throws ExchangerDaoException {
        List<User> userList = null;
        Transaction tx = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            userList = session.createNamedQuery("User.findUsersMatchByLogin", User.class)
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
        return userList;
    }
}
