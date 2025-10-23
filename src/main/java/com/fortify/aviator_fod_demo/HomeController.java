package com.fortify.aviator_fod_demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.sql.*;

@Controller
public class HomeController {

    private Connection connection;

    private final static String ATTRIB_MESSAGE = "message";
    private final static String ATTRIB_USERNAME = "username";
    private final static String USERNAME = "Please provide a username.";
    private final static String PASSWORD = "Please provide a password.";
    private final static String WRONG_CREDS = "Wrong credentials.";
    private final static String SUCCESS = "Logged in successfully.";

    @RequestMapping(value = "/auth/login", method = RequestMethod.POST)
    public ModelAndView loginSubmit(@RequestBody LoginCredentials credentials,
                                    @RequestParam String redirectUrl,
                                    HttpServletRequest request,
                                    HttpSession session) throws SQLException {
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        if(username.isEmpty())
            request.setAttribute(ATTRIB_MESSAGE, USERNAME);
        else if(password.isEmpty())
            request.setAttribute(ATTRIB_MESSAGE, PASSWORD);
        else {
            System.out.println("username: " + username);
            System.out.println("password: " + password);


            /* Verify whether credentials are correct. */
            boolean credentialsCorrect = false;
            try(Connection connection = DriverManager.getConnection("localhost:1234", "dbuser", "secretpwd")) {
                try (Statement statement = connection.createStatement()) {
                    try (ResultSet rs = statement.executeQuery(
                            "SELECT 1 FROM users WHERE username = '" + username + "' AND password = '" + password + "'")) {
                        credentialsCorrect = rs.next();
                    }
                }
            }
            if(credentialsCorrect) {
                request.setAttribute(ATTRIB_MESSAGE, SUCCESS);
                session.setAttribute(ATTRIB_USERNAME, username);
            } else {
                request.setAttribute(ATTRIB_MESSAGE, WRONG_CREDS);
            }
        }
        String validatedUrl = validateRedirect(redirectUrl);
        if(validatedUrl == null) return new ModelAndView("error");
        return new ModelAndView("redirect:/home");
    }

    private String validateRedirect(String url) {
        if("http://redirectoption1.com".equals(url)) {
            return url;
        } else if("http://redirectoption2.com".equals(url)) {
            return url;
        } else {
            return null;
        }
    }
}
