package fortegroup.internship.mandrik.exchanger.web.controller;

import fortegroup.internship.mandrik.exchanger.web.controller.model.StringMessage;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserInfoController {


    /* Get username of sign in user */
    @RequestMapping(value = "/info/username", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public StringMessage getUsername(Principal principal) {
        StringMessage stringRequest = new StringMessage();
        stringRequest.setMessage(principal.getName());
        return stringRequest;
    }
}
