package fortegroup.internship.mandrik.exchanger.dao;

import fortegroup.internship.mandrik.exchanger.dao.exceptions.ExchangerDaoException;
import fortegroup.internship.mandrik.exchanger.dao.exceptions.NotFoundExchangerResourceException;
import fortegroup.internship.mandrik.exchanger.model.Document;
import fortegroup.internship.mandrik.exchanger.model.User;

import java.util.List;

public interface  DocumentDao {

    void addDocument(Document employee)
            throws ExchangerDaoException;

    Document getDocumentById(long id)
            throws ExchangerDaoException, NotFoundExchangerResourceException;

    List<Document> getDocumentList()
            throws ExchangerDaoException;

    void deleteDocument(long id)
            throws ExchangerDaoException, NotFoundExchangerResourceException;

    Document updateDocument(Document document)
            throws ExchangerDaoException, NotFoundExchangerResourceException;

    Document getDocumentByPathAndName(String path, String name)
            throws ExchangerDaoException;

    List<User> getAssignmentUsersToDocument(int documentId)
            throws ExchangerDaoException;

    List<Document> getDocumentListByMathName(String mathUsername)
            throws ExchangerDaoException;
}
