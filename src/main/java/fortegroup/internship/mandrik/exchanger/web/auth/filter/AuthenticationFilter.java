package fortegroup.internship.mandrik.exchanger.web.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortegroup.internship.mandrik.exchanger.web.auth.model.LoginRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if ("application/json".equals(request.getHeader("Content-Type"))) {
            try {
                StringBuffer sb = new StringBuffer();
                String line;

                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null){
                    sb.append(line);
                }

                //json transformation
                ObjectMapper mapper = new ObjectMapper();
                LoginRequest loginRequest = mapper.readValue(sb.toString(), LoginRequest.class);


                request.setAttribute("jsonUsername", loginRequest.getUsername());
                request.setAttribute("jsonPassword", loginRequest.getPassword());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return super.attemptAuthentication(request, response);
    }

    protected String obtainPassword(HttpServletRequest request) {
        String password;

        if ("application/json".equals(request.getHeader("Content-Type"))) {
            password = (String) request.getAttribute("jsonPassword");
        }else{
            password = super.obtainPassword(request);
        }

        return password;
    }

    protected String obtainUsername(HttpServletRequest request) {
        String username;

        if ("application/json".equals(request.getHeader("Content-Type"))) {
            username = (String) request.getAttribute("jsonUsername");
        } else {
            username = super.obtainUsername(request);
        }

        return username;
    }
}
