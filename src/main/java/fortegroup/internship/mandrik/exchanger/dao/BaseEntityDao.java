package fortegroup.internship.mandrik.exchanger.dao;

import fortegroup.internship.mandrik.exchanger.dao.exceptions.ExchangerDaoException;
import fortegroup.internship.mandrik.exchanger.dao.exceptions.NotFoundExchangerResourceException;
import javafx.util.Pair;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;


class BaseEntityDao {

     static <T> boolean addEntity(SessionFactory sessionFactory, T entity) throws ExchangerDaoException {
        Transaction transaction = null;
         Session session = null;
         try {
             session = sessionFactory.openSession();
             transaction = session.beginTransaction();
             session.save(entity);
             transaction.commit();
         } catch (Exception e) {
             if (transaction != null) {
                 transaction.rollback();
             }
             throw e;
         } finally {
             if(session != null) {
                 session.close();
             }
         }
        return true;
    }


    static <T, K> T getEntityById(SessionFactory sessionFactory, K id, Class<T> tClass)
            throws ExchangerDaoException, NotFoundExchangerResourceException {
        Transaction tx = null;

        Pair<T, Session> pairEntitySession = openSessionAndFindEntityById(sessionFactory, id, tClass);
        T entity = pairEntitySession.getKey();
        Session session = pairEntitySession.getValue();
        try {
            tx = session.beginTransaction();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new ExchangerDaoException(e);
        } finally {
            session.close();
        }

        return entity;
    }


    static <T> List<T> getEntityList(SessionFactory sessionFactory, Class<T> tClass)
            throws ExchangerDaoException {
        List<T> entities;
        Transaction tx = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> criteria = builder.createQuery(tClass);
            Root<T> userRoot = criteria.from(tClass);
            criteria.select(userRoot);
            entities = session.createQuery(criteria).getResultList();
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
        return entities;
    }


    static <T, K> boolean deleteEntity(SessionFactory sessionFactory, K id, Class<T> tClass)
            throws ExchangerDaoException, NotFoundExchangerResourceException {
        Transaction tx = null;
        Pair<T, Session> pairEntitySession = openSessionAndFindEntityById(sessionFactory, id, tClass);
        T entity = pairEntitySession.getKey();
        Session session = pairEntitySession.getValue();
        try {
            tx = session.beginTransaction();
            session.delete(entity);
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

        return true;
    }

    static <T, K> T updateEntity(SessionFactory sessionFactory, T newEntity, K id, Class<T> tClass)
            throws ExchangerDaoException, NotFoundExchangerResourceException {
        Transaction tx = null;
        Pair<T, Session> pairEntitySession = openSessionAndFindEntityById(sessionFactory, id, tClass);
        Session session = pairEntitySession.getValue();
        try {
            tx = session.beginTransaction();
            session.merge(newEntity);
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
        return newEntity;
    }

    private static <T, K> Pair<T, Session> openSessionAndFindEntityById(SessionFactory sessionFactory, K id, Class<T> tClass)
            throws ExchangerDaoException, NotFoundExchangerResourceException {
        Session session = null;
        T entity;
        try {
            session = sessionFactory.openSession();
            entity = session.find(tClass, id);
        } catch (Exception e) {
            if(session != null) {
                session.close();
            }
            throw new ExchangerDaoException(e);
        }
        if (entity == null) {
            session.close();
            throw new NotFoundExchangerResourceException("Can not find resource " + tClass + " with id=" + id);
        }
        return new Pair<>(entity, session);
    }
}
