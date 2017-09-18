package fortegroup.internship.mandrik.exchanger.web.auth.handler;

import org.apache.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthFailureHandler implements AuthenticationFailureHandler {

    private static Logger logger = Logger.getLogger(AuthFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.setContentType("application/json");
        PrintWriter writer = httpServletResponse.getWriter();

        writer.write("{\"result\" : \"failure\", \"error\" : \"" + e.getMessage() + "\"}");
        writer.flush();
    }
}