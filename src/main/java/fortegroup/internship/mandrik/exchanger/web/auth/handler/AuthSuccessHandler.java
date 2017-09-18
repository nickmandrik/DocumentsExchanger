package fortegroup.internship.mandrik.exchanger.web.auth.handler;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    private static Logger logger = Logger.getLogger(AuthSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);

        httpServletResponse.setContentType("application/json");
        PrintWriter writer = httpServletResponse.getWriter();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().toString();
        String outputRole = "anonymous";
        if(role.contains("ROLE_ADMIN")) {
            outputRole = "admin";
        } else if(role.contains("ROLE_USER")) {
            outputRole = "client";
        }
        writer.write("{\"result\" : \"success\", \"role\" : \"" + outputRole + "\"}");
        writer.flush();
    }
}