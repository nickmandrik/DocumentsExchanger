package fortegroup.internship.mandrik.exchanger.dao;

import fortegroup.internship.mandrik.exchanger.dao.exceptions.ExchangerDaoException;
import fortegroup.internship.mandrik.exchanger.dao.exceptions.NotFoundExchangerResourceException;
import fortegroup.internship.mandrik.exchanger.model.Assignment;
import fortegroup.internship.mandrik.exchanger.model.Document;
import fortegroup.internship.mandrik.exchanger.model.User;

import java.util.List;

/**
 * Represent methods to access operations with {@link Assignment} entity
 * @author Nick Mandrik
 */
public interface AssignmentDao {

    /**
     * Add new assignment of the {@link Document} document to {@link User} user.
     * @param assignment added to the database.
     * @throws ExchangerDaoException caused by Hibernate error while running this operation.
     */
    void addAssignment(Assignment assignment) throws ExchangerDaoException;

    /**
     * Find {@link Assignment} by it's id.
     * @param id of the wanted {@link Assignment} entity.
     * @throws ExchangerDaoException caused by Hibernate error while running this operation.
     * @throws NotFoundExchangerResourceException if record doesn't exist in the database
     * @return {@link Assignment} that matches param id.
     */
    Assignment getAssignmentById(long id) throws ExchangerDaoException, NotFoundExchangerResourceException;

    /**
     * Use to find all assignments documents to users.
     * @throws ExchangerDaoException caused by Hibernate error while running this operation.
     * @return All {@link List<Assignment>} that represent in database.
     */
    List<Assignment> getAssignmentList() throws ExchangerDaoException;

    /**
     * Delete {@link Assignment} of the {@link Document} document to {@link User} user.
     * @param id of the wanted to delete {@link Assignment} entity.
     * @throws ExchangerDaoException caused by Hibernate error while running this operation.
     * @throws NotFoundExchangerResourceException if record that match id param doesn't exist in the database
     */
    void deleteAssignment(long id) throws ExchangerDaoException, NotFoundExchangerResourceException;

    /**
     * Get id of the {@link Assignment} if the {@link Assignment} with documentId and userLogin exists in database
     * @param documentId id of the {@link Document}
     * @param userLogin login of the {@link User}
     * @throws ExchangerDaoException caused by Hibernate error while running this operation.
     * @return id of the {@link Assignment} if user have access to the document. Else return -1.
     */
    long getIdAssignDocumentToUser(long documentId, String userLogin) throws ExchangerDaoException;
}
