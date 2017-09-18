package fortegroup.internship.mandrik.exchanger.dao;

import fortegroup.internship.mandrik.exchanger.dao.exceptions.ExchangerDaoException;
import fortegroup.internship.mandrik.exchanger.dao.exceptions.NotFoundExchangerResourceException;
import fortegroup.internship.mandrik.exchanger.model.Document;
import fortegroup.internship.mandrik.exchanger.model.User;

import java.util.List;

public interface  UserDao {

    public void addUser(User user)
            throws ExchangerDaoException;

    public List<User> getUserList()
            throws ExchangerDaoException;

    public void deleteUser(String login)
            throws ExchangerDaoException, NotFoundExchangerResourceException;

    public User updateUser(User user)
            throws ExchangerDaoException, NotFoundExchangerResourceException;

    public User getUserByLogin(String login)
            throws ExchangerDaoException, NotFoundExchangerResourceException;

    public List<Document> getAssignmentDocumentsToUser(String login)
            throws ExchangerDaoException, NotFoundExchangerResourceException;

    public List<User> getUserListByMathName(String mathUsername)
            throws ExchangerDaoException;
}