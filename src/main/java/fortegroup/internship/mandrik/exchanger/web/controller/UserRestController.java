package fortegroup.internship.mandrik.exchanger.web.controller;

import fortegroup.internship.mandrik.exchanger.dao.*;
import fortegroup.internship.mandrik.exchanger.dao.exceptions.ExchangerDaoException;
import fortegroup.internship.mandrik.exchanger.dao.exceptions.NotFoundExchangerResourceException;
import fortegroup.internship.mandrik.exchanger.model.*;
import fortegroup.internship.mandrik.exchanger.util.*;
import fortegroup.internship.mandrik.exchanger.web.controller.model.Assign;
import fortegroup.internship.mandrik.exchanger.web.controller.model.StringMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;

@RestController
public class UserRestController {

    private final AssignmentDao assignmentDao;
    private final UserDao userDao;
    private final DocumentDao documentDao;

    static final Logger logger = Logger.getLogger(UserRestController.class);

    @Autowired
    public UserRestController(AssignmentDao assignmentDao, UserDao userDao, DocumentDao documentDao) {
        this.assignmentDao = assignmentDao;
        this.userDao = userDao;
        this.documentDao = documentDao;
    }


    /* Submit form in Spring Restful Services */
    @RequestMapping(value = "/user/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Status addUser(@RequestBody User user) {
        try {
            userDao.getUserByLogin(user.getLogin());
            return new Status(0, "User with this username already exists.");
        } catch (ExchangerDaoException e) {
            return new Status(1, e.toString());
        } catch (NotFoundExchangerResourceException e) {
            try {
                userDao.addUser(user);
                return new Status(0, "User added Successfully.");
            } catch (ExchangerDaoException ex) {
                ex.printStackTrace();
                return new Status(1, ex.toString());
            }
        }

    }

    /* Get a single user in Json form in Spring Rest Services */
    @RequestMapping(value = "/user/get", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUserByLogin(@RequestBody String login) {
        User user = null;
        try {
            user = userDao.getUserByLogin(login);

        } catch (ExchangerDaoException | NotFoundExchangerResourceException e) {
            e.printStackTrace();
        }
        return user;
    }

    /* Getting List of users in Json format in Spring Restful Services */
    @RequestMapping(value="/user/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getUserList() {
        List<User> userList = null;
        try {
            userList = userDao.getUserList();
        } catch (ExchangerDaoException e) {
            e.printStackTrace();
        }

        return userList;
    }

    /* Delete an user from DB in Spring Restful Services */
    @RequestMapping(value = "/user/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Status deleteUser(@RequestBody StringMessage login) {
        try {
            userDao.deleteUser(login.getMessage());
            return new Status(0, "User deleted Successfully.");

        } catch (ExchangerDaoException | NotFoundExchangerResourceException e) {
            return new Status(1, e.toString());
        }

    }

    /* Update an user from DB in Spring Restful Services */
    @RequestMapping(value = "/user/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Status updateUser(@RequestBody User user) {
        try {
            userDao.updateUser(user);
            return new Status(0, "User updated Successfully.");
        } catch (ExchangerDaoException | NotFoundExchangerResourceException e) {
            e.printStackTrace();
            return new Status(1, e.toString());
        }

    }

    /* Getting List of users in Json format that match request parameter username */
    @RequestMapping(value="/user/find", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> findUsers(@RequestBody StringMessage request) {
        List<User> userList = null;
        try {
            userList = userDao.getUserListByMathName(request.getMessage());
        } catch (ExchangerDaoException e) {
            e.printStackTrace();
        }

        return userList;
    }


    /* Getting List of documents in Json format that match request parameter name */
    @RequestMapping(value="/user/docs", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Document> findAssignedDocumentsToUser(@RequestBody StringMessage loginUser) {
        List<Document> documentList = null;
        try {
            documentList = userDao.getAssignmentDocumentsToUser(loginUser.getMessage());
        } catch (ExchangerDaoException e) {
            e.printStackTrace();
        } catch (NotFoundExchangerResourceException ignored) {}

        return documentList;
    }


    /* Getting List of documents in Json format that match request parameter name */
    @RequestMapping(value="/user/assign", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public StringMessage changeAssignDocumentToUser(@RequestBody Assign assign) {
        StringMessage message = new StringMessage();
        try {
            long idAssign = assignmentDao.getIdAssignDocumentToUser(
                    assign.getDocument().getId(), assign.getUser().getLogin());
            if(idAssign != -1) {
                assignmentDao.deleteAssignment(idAssign);
                message.setMessage("0 0");
            } else {
                Assignment assignment = new Assignment();
                assignment.setDocument(assign.getDocument());
                assignment.setUser(assign.getUser());
                assignmentDao.addAssignment(assignment);
                message.setMessage("0 1");
            }
        } catch (ExchangerDaoException | NotFoundExchangerResourceException e) {
            e.printStackTrace();
            message.setMessage("1");
        }

        return message;
    }


    @RequestMapping(value="/assign/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Assign> changeAssignDocumentToUser() {
        List<Assign> assignList = null;
        try {
            List<Assignment> assignmentList = assignmentDao.getAssignmentList();
            assignList = new ArrayList<>();
            for(Assignment assignment: assignmentList) {
                Assign assign = new Assign();
                assign.setDocument(assignment.getDocument());
                assign.setUser(assignment.getUser());
                assignList.add(assign);
            }
        } catch (ExchangerDaoException e) {
            e.printStackTrace();
        }

        return assignList;
    }

}