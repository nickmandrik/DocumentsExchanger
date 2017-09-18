package fortegroup.internship.mandrik.exchanger.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class ViewController {

    @RequestMapping(value = "/exchanger")
    public String redirectToPage(Principal principal) {

        String outputRedirect = "";
        if(principal != null && principal.getName() != null &&
                !principal.getName().equals("") && !principal.getName().equals("guest")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String role = auth.getAuthorities().toString();
            if(role.contains("ROLE_ADMIN")) {
                outputRedirect = "redirect:/exchanger/admin";
            } else if(role.contains("ROLE_USER")) {
                outputRedirect = "redirect:/exchanger/client";
            }
        } else {
            outputRedirect = "redirect:/exchanger/login";
        }
        return outputRedirect;
    }

    @RequestMapping(value = "/login")
    public String loginPage(Principal principal) {
        return "login";
    }

    @RequestMapping(value = "/client")
    public String clientPage(Principal principal) {
        return "client";
    }

    @RequestMapping(value = "/admin")
    public String adminPage(Principal principal) {
        return "admin";
    }

}
