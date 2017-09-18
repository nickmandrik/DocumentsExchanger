package fortegroup.internship.mandrik.exchanger.web.controller;

import fortegroup.internship.mandrik.docstorage.service.DocumentDetails;
import fortegroup.internship.mandrik.docstorage.service.DocumentStorageService;
import fortegroup.internship.mandrik.docstorage.service.DocumentStorageServiceImplService;
import fortegroup.internship.mandrik.exchanger.dao.*;
import fortegroup.internship.mandrik.exchanger.dao.exceptions.ExchangerDaoException;
import fortegroup.internship.mandrik.exchanger.dao.exceptions.NotFoundExchangerResourceException;
import fortegroup.internship.mandrik.exchanger.model.*;

import fortegroup.internship.mandrik.exchanger.util.Encoder;
import fortegroup.internship.mandrik.exchanger.util.Status;
import fortegroup.internship.mandrik.exchanger.web.controller.model.StringMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.util.List;


@RestController
public class DocumentRestController {


    private final AssignmentDao assignmentDao;
    private final UserDao userDao;
    private final DocumentDao documentDao;
    private final DocumentStorageService storageService;

    static final Logger logger = Logger.getLogger(UserRestController.class);

    @Autowired
    public DocumentRestController(AssignmentDao assignmentDao, UserDao userDao, DocumentDao documentDao) {
        this.assignmentDao = assignmentDao;
        this.userDao = userDao;
        this.documentDao = documentDao;
        DocumentStorageServiceImplService storageService = new DocumentStorageServiceImplService();
        this.storageService = storageService.getDocumentStorageServiceImplPort();
    }

    /* Get a list of documents in Json form in Spring Rest Services */
    @RequestMapping(value = "/documents/list", method = RequestMethod.GET)
    public List<Document> getDocumentsList() {
        List<Document> documentList = null;
        try {
            documentList = documentDao.getDocumentList();

        } catch (ExchangerDaoException e) {
            e.printStackTrace();
        }
        return documentList;
    }


    /* Getting List of documents in Json format that match request parameter name */
    @RequestMapping(value="/documents/find", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Document> findDocuments(@RequestBody StringMessage request) {
        List<Document> documentList = null;
        try {
            documentList = documentDao.getDocumentListByMathName(request.getMessage());
        } catch (ExchangerDaoException e) {
            e.printStackTrace();
        }

        return documentList;
    }


    /* Upload document in MultipartFile data in Spring Rest Services */
    @RequestMapping(value = "/documents/upload", method = RequestMethod.POST)
    public Status uploadDocument(@RequestParam("file") MultipartFile file,
                                 @RequestParam("path") String path,
                                 @RequestParam("fileName") String fileName) {

        Status outputStatus;

        // get DocumentDetails and DataHandler in bytes
        DocumentDetails documentDetails = new DocumentDetails();
        documentDetails.setName(fileName);
        documentDetails.setPath(path);
        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
            return new Status(1, "Error to get bytes of the file.");
        }

        // use JAX-WS method to upload doc
        fortegroup.internship.mandrik.docstorage.service.Status statusUpload =
                storageService.uploadDocument( fileBytes, documentDetails);

        if(statusUpload.getCode() == 0) {

            // add document in database
            Document document = new Document();
            document.setName(fileName);
            document.setPath(path);
            try {
                documentDao.addDocument(document);
                outputStatus = new Status(0, "Document uploaded successfully.");
            } catch (ExchangerDaoException e) {
                e.printStackTrace();
                deleteDocument(document);
                outputStatus = new Status(1, e.getMessage());
            }
        } else {
            outputStatus = new Status(1, statusUpload.getMessage());
        }

        return outputStatus;
    }




    /* Delete document by it's info */
    @RequestMapping(value = "/documents/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Status deleteDocument(@RequestBody Document document) {
        Status outputStatus;


        DocumentDetails documentDetails = new DocumentDetails();
        documentDetails.setPath(document.getPath());
        documentDetails.setName(document.getName());

        // use JAX-WS method to delete doc
        fortegroup.internship.mandrik.docstorage.service.Status statusUpload =
                storageService.deleteDocument(documentDetails);

        if(statusUpload.getCode() == 0) {
            try {
                documentDao.deleteDocument(document.getId());
                outputStatus = new Status(0, statusUpload.getMessage());
            } catch (ExchangerDaoException | NotFoundExchangerResourceException e) {
                outputStatus = new Status(1, e.getMessage());
            }
        } else {
            outputStatus = new Status(1, statusUpload.getMessage());
        }

        return outputStatus;
    }




    /* Download document */
    @RequestMapping(value = "/documents/download", method = RequestMethod.POST)
    public void downloadDocument(@RequestBody Document document, HttpServletResponse response) {

        DocumentDetails documentDetails = new DocumentDetails();
        documentDetails.setPath(document.getPath());
        documentDetails.setName(document.getName());

        String mimeType= URLConnection.guessContentTypeFromName(document.getName());

        if(mimeType==null) {
            logger.info("Download document '" + document.getName() + "'. " +
                    "Mimetype is not detectable, will take application/octet-stream");
            mimeType = "application/octet-stream";
        } else {
            logger.info("Download document '" + document.getName() + "'. Mimetype : " + mimeType);
        }

        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", String.format("attachment; documentName=\"%s\"", document.getName()));


        byte[] bytesFile = Encoder.encodeBytesToBase64(storageService.downloadDocument(documentDetails));
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesFile);

        response.setContentLength(bytesFile.length);

        InputStream inputStream = new BufferedInputStream(byteArrayInputStream);

        try {
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    /* Delete document by it's info */
    @RequestMapping(value = "/documents/edit", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Status editDocument(@RequestBody Document newDocument) {

        Status outputStatus;

        Document oldDocument = null;
        try {
            oldDocument = documentDao.getDocumentById(newDocument.getId());
        } catch (ExchangerDaoException | NotFoundExchangerResourceException e) {
            e.printStackTrace();
            return new Status(1, "Document with id = " + newDocument.getId() + " doesn't exist");
        }

        DocumentDetails oldDocumentDetails = new DocumentDetails();
        oldDocumentDetails.setPath(oldDocument.getPath());
        oldDocumentDetails.setName(oldDocument.getName());

        DocumentDetails newDocumentDetails = new DocumentDetails();
        if(newDocument.getPath() == null) {
            newDocument.setPath("");
        }
        newDocumentDetails.setPath(newDocument.getPath());
        newDocumentDetails.setName(newDocument.getName());

        // use JAX-WS method to update doc
        fortegroup.internship.mandrik.docstorage.service.Status statusEdit =
                storageService.updateDocument(oldDocumentDetails, newDocumentDetails);

        if(statusEdit.getCode() == 0) {
            try {
                System.out.println(newDocument);
                documentDao.updateDocument(newDocument);
                outputStatus = new Status(0, statusEdit.getMessage());
            } catch (ExchangerDaoException | NotFoundExchangerResourceException e) {
                outputStatus = new Status(1, e.getMessage());
            }
        } else {
            outputStatus = new Status(1, statusEdit.getMessage());
        }

        return outputStatus;
    }



}
