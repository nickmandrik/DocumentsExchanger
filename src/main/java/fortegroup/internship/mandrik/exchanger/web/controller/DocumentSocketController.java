package fortegroup.internship.mandrik.exchanger.web.controller;

import fortegroup.internship.mandrik.exchanger.dao.*;
import fortegroup.internship.mandrik.exchanger.dao.exceptions.ExchangerDaoException;
import fortegroup.internship.mandrik.exchanger.dao.exceptions.NotFoundExchangerResourceException;
import fortegroup.internship.mandrik.exchanger.model.*;
import fortegroup.internship.mandrik.exchanger.util.Encoder;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.BinaryMessage;

import java.io.*;
import java.net.URLConnection;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Controller
public class DocumentSocketController {

    private final UserDao userDao;
    private final DocumentDao documentDao;

    private static final Logger logger = Logger.getLogger(DocumentSocketController.class);

    @Autowired
    public DocumentSocketController(UserDao userDao, DocumentDao documentDao) {
        this.userDao = userDao;
        this.documentDao = documentDao;
    }

    @SubscribeMapping(value = "/doclist")
    public Document[] getDocuments(Principal principal) {
        Document[] documents = null;
        try {
            List<Document> documentList = userDao.getAssignmentDocumentsToUser(principal.getName());
            documents = documentList.toArray(new Document[documentList.size()]);
        } catch (ExchangerDaoException | NotFoundExchangerResourceException e) {
            e.printStackTrace();
        }

        return documents;

    }


    @SubscribeMapping(value = "/download/{docId}")
    public BinaryMessage downloadDocument(@DestinationVariable String docId, Principal principal) {


        Document document;
        try {
            document = documentDao.getDocumentById(Long.parseLong(docId));
        } catch (ExchangerDaoException | NotFoundExchangerResourceException e) {
            e.printStackTrace();
            return null;
        }

        String pathToStorage = getPathToStorage();

        String mimeType= URLConnection.guessContentTypeFromName(document.getName());

        if(mimeType==null) {
            logger.info("Download document '" + document.getName() + "'. " +
                    "Mimetype is not detectable, will take application/octet-stream");
        } else {
            logger.info("Download document '" + document.getName() + "'. Mimetype : " + mimeType);
        }

        /*BinaryMessage binaryMessage = null;
        try {
            binaryMessage = new BinaryMessage(Encoder.encodeFileToBase64(
                    new File(pathToStorage + getPathWithSeparatorsAsNeeded(document.getPath())
                            + document.getName())));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        BinaryMessage binaryMessage = null;
        try {
            binaryMessage = new BinaryMessage(FileUtils.readFileToByteArray(new File(pathToStorage +
                    getPathWithSeparatorsAsNeeded(document.getPath()) + document.getName())));
        } catch (IOException e) {
            e.printStackTrace();
        }


        return binaryMessage;

    }


    private String getPathWithSeparatorsAsNeeded(String path) {
        if(path.length() != 0) {
            String pathFirstNonSep = path;
            if(path.charAt(0) == File.separatorChar) {
                pathFirstNonSep = path.substring(1, path.length());
            }
            return pathFirstNonSep.charAt(path.length() - 1) != File.separatorChar ?
                    pathFirstNonSep.concat(File.separator) : pathFirstNonSep;
        } else {
            return "";
        }
    }


    private String getPathToStorage() {
        Properties prop = new Properties();
        InputStream input = null;
        String directorySource = "";
        try {

            input = DocumentSocketController.class.getResourceAsStream("/docstorage.properties");

            prop.load(input);

            directorySource = prop.getProperty("documents.dir");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return directorySource;
    }

}