package fortegroup.internship.mandrik.exchanger.web.auth;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import fortegroup.internship.mandrik.exchanger.dao.UserDao;
import fortegroup.internship.mandrik.exchanger.model.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import sun.security.provider.MD5;

/**
 * A custom authentication manager that allows access if the user details
 * exist in the database.
 * Otherwise, throw a {@link BadCredentialsException}
 */
public class AuthManager implements AuthenticationManager {

    private static Logger logger = Logger.getLogger(AuthManager.class);

    // Our custom DAO layer
    private final UserDao userDAO;


    @Autowired
    public AuthManager(UserDao userDAO) {
        this.userDAO = userDAO;
    }

    public Authentication authenticate(Authentication auth)
            throws AuthenticationException {

        logger.debug("Performing exchanger documents authentication...");

        // Init a database user object
        User user;

        try {
            // Retrieve user details from database
            user = userDAO.getUserByLogin(auth.getName());
        } catch (Exception e) {
            logger.error("User does not exists!");
            throw new BadCredentialsException("Invalid username or password.");
        }

       /* MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        assert m != null;
        m.reset();
        m.update(user.getPassword().getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        StringBuilder hashText = new StringBuilder(bigInt.toString(16));
        // Now we need to zero pad it if you actually want the full 32 chars.
        while(hashText.length() < 32 ){
            hashText.insert(0, "0");
        }*/


        if (!user.getPassword().equals(auth.getCredentials())) {
            logger.error("Wrong password!");

            throw new BadCredentialsException("Invalid username or password.");
        }


        logger.info("User details are good and ready to work.");
        return new UsernamePasswordAuthenticationToken(
                    auth.getName(),
                    auth.getCredentials(),
                    getGrantedAuthorities(user));
    }

    private List<GrantedAuthority> getGrantedAuthorities(User user){
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        if(Objects.equals(user.getRole(), "ADMIN")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER" ));
        }

        return authorities;
    }

}