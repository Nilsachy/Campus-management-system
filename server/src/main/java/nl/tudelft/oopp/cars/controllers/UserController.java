package nl.tudelft.oopp.cars.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.tudelft.oopp.cars.constants.StringResponses;
import nl.tudelft.oopp.cars.entities.User;
import nl.tudelft.oopp.cars.repositories.UserRepository;
import nl.tudelft.oopp.shared.requests.create.ChangePasswordRequest;
import nl.tudelft.oopp.shared.requests.create.CreateUserRequest;
import nl.tudelft.oopp.shared.requests.read.GetByEmailRequest;
import nl.tudelft.oopp.shared.requests.read.GetByUserRoleRequest;
import nl.tudelft.oopp.shared.requests.read.GetUserValidationRequest;
import nl.tudelft.oopp.shared.requests.update.UpdateUserAdminStatusRequest;
import nl.tudelft.oopp.shared.responses.content.UserResponse;
import nl.tudelft.oopp.shared.responses.content.UsersResponse;
import nl.tudelft.oopp.shared.util.StringUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository repository;

    /**
     * Validates the user.
     * Everyone has access.
     *
     * @param json JSON Formatted file containing user info
     * @return Returns if the user is validated
     */
    @PostMapping(path = "validateLogin")
    public @ResponseBody
    String validateUser(@RequestBody String json, HttpServletRequest req, HttpServletResponse res) {
        Gson gson = new GsonBuilder().create();

        GetUserValidationRequest request = gson.fromJson(json, GetUserValidationRequest.class);

        logger.info("Validating user: {}", request.getEmail());

        //TODO: Implement user validation
        Optional<User> user = repository.findById(request.getEmail());
        if (user.isPresent()) {
            User opUser = user.get();
            if (opUser.getPasswordHash() == request.getPasswordHash()) {
                HttpSession session = req.getSession();
                session.setAttribute("user", opUser.getEmail());
                session.setAttribute("role", opUser.getRole());
                logger.info("Gave user {} session id: {}", opUser.getEmail(), session.getId());
                return "SUCCESS correct user and password";
            } else {
                return "ERROR wrong username or password";
            }
        }

        return "ERROR user not in DB.";
    }

    /**
     * Adds a new user to the database.
     * Everyone has access.
     *
     * @param json JSON that contains the user creation info (Email, Password Hash and Role)
     * @return String containing Error/Success
     */
    @PostMapping("addUser")
    public @ResponseBody
    String addNewUser(@RequestBody String json,
                      HttpServletRequest req, HttpServletResponse res) {
        Gson gson = new GsonBuilder().create();

        CreateUserRequest request = gson.fromJson(json, CreateUserRequest.class);

        logger.info("User tried to add new user with params: {{}, {}}",
                request.getEmail(), request.getPasswordHash());
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestBody takes the parameters from the post body

        // check for email format
        if (!StringUtilities.isValidEmail(request.getEmail())) {
            logger.error("The new user is not saved, the provided email {} address is malformed",
                    request.getEmail());
            return "ERROR Incorrect email format";
        }

        // check for a TU mail
        if (!request.getEmail().contains("tudelft.nl")) {
            logger.error("The new user is not saved,"
                            + " the provided address {} is not from the TU Delft domain",
                    request.getEmail());
            return "ERROR Wrong email domain. Please use a TU Delft supplied email.";
        }
        // a check for whether the email matches the role
        String domain = request.getEmail().split("@")[1];

        String role = "staff";

        if (domain.startsWith("student")) {
            role = "student";
        }

        Optional<User> oldUser = repository.findById(request.getEmail());
        if (!oldUser.isEmpty()) {
            logger.error("The new user is not saved, the user already exists");
            return "ERROR User already exists";
        }

        // adds new user to repository
        User n = new User(request.getEmail(), role, request.getPasswordHash());
        logger.info("The new user '{}' is being saved", n.toString());
        repository.save(n);
        return "SUCCESS User added";
    }

    /**
     * Method to get a user.
     *
     * @param json JSON string that contains the email
     * @return user with the email entered as a parameter. If not found, the function returns null.
     */
    @PostMapping(path = "getUser")
    public @ResponseBody
    String getUserByEmail(@RequestBody String json,
                          HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetByEmailRequest request = gson.fromJson(json, GetByEmailRequest.class);

        Optional<User> u = repository.findById(request.getEmail());
        if (u.isEmpty()) {
            // error > send Feedback instance
            logger.debug("Could not find a match for {}", request.getEmail());
            return "";
        } else {
            User user = u.get();
            return gson.toJson(new UserResponse(
                    user.getEmail(),
                    user.getRole(),
                    user.getPasswordHash()
            ));
        }
    }

    /**
     * Deletes specified user - if there - from the database.
     *
     * @param json JSON request with email of the student to be deleted
     * @return success/failure message
     */
    @DeleteMapping(path = "removeUserByEmail")
    public @ResponseBody
    String removeUserByEmail(@RequestBody String json,
                             HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetByEmailRequest request = gson.fromJson(json, GetByEmailRequest.class);

        try {
            repository.deleteById(request.getEmail());
            return "User deleted.";
        } catch (Exception e) {
            return "User not deleted.";
        }
    }

    /**
     * Method which returns a Iterator of all users in the database.
     *
     * @return List of all users in database
     */
    @GetMapping(path = "getAllUsers")
    public @ResponseBody
    String getAllUsers(HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        logger.debug("Getting all users");

        List<User> allUsers = repository.findAll();

        return gson.toJson(userListToResponse(allUsers));
    }

    /**
     * Searches for the users that have a specific role.
     *
     * @param json JSON containing the role that is searched for
     * @return List of all users with the specified role
     */
    @PostMapping(path = "getUsersByRoles")
    public @ResponseBody
    String getByRole(@RequestBody String json,
                     HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetByUserRoleRequest request = gson.fromJson(json, GetByUserRoleRequest.class);

        logger.debug("Getting all users with role {}", request.getRole());

        List<User> users = repository.getByRole(request.getRole());

        return gson.toJson(userListToResponse(users));
    }

    /**
     * Test method to check connection.
     *
     * @return Confirmation string
     */
    @GetMapping(path = "test")
    public @ResponseBody
    String tester(HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        logger.debug("Tester function was called");

        logger.info("Session id: {}", req.getSession(false).getId());

        return "It works";
    }

    /**
     * Changes a user's password.
     
     * @param json JSON containing the user and the new password.
     * @return Confirmation string
     */
    @PostMapping(path = "changePassword")
    public @ResponseBody
    String changePassword(@RequestBody String json,
                          HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        logger.debug("Session: {} wants to change the password.", req.getSession(false).getId());

        ChangePasswordRequest request = gson.fromJson(json, ChangePasswordRequest.class);

        Optional<User> u = repository.findById(request.getEmail());
        if (u.isEmpty()) {
            logger.debug("Could not find a match for {}", request.getEmail());

            return "Could not find a match for Email";
        } else {
            logger.debug("change password. email{}", request.getEmail());

            User opUser = u.get();
            opUser.setPasswordHash(request.getPassword());

            repository.save(opUser);

            return "Password Updated";
        }
    }

    /**
     * An admin gives another user admin privileges.
     * @param json JSON string containing the user that is to be changed.
     * @return Confirmation string
     */
    @PostMapping(path = "makeAdmin")
    public @ResponseBody String makeAdmin(@RequestBody String json,
                                          HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        if (!req.getSession(false).getAttribute("role").equals("admin")) {
            return StringResponses.ONLY_ADMINS;
        }

        Gson gson = new GsonBuilder().create();

        UpdateUserAdminStatusRequest request =
                gson.fromJson(json, UpdateUserAdminStatusRequest.class);

        Optional<User> u = repository.findById(request.getEmail());
        if (u.isEmpty()) {
            logger.debug("Tried to make a non-existing user {} admin", request.getEmail());

            return "Could not find a match for Email";
        } else if (u.get().getRole().equals("student")) {
            logger.debug("Tried to make a student {} an admin", request.getEmail());

            return "Student can't be admin";
        } else {
            logger.debug("Making user {} admin", request.getEmail());

            User opUser = u.get();
            opUser.setRole("admin");

            repository.save(opUser);

            return "Made Admin";
        }
    }

    /**
     * An admin removes another user's admin privileges.
     * @param json JSON string containing the user that is to be changed.
     * @return Confirmation string
     */
    @PostMapping(path = "makeStaff")
    public @ResponseBody String makeStaff(@RequestBody String json, HttpServletRequest req,
                                          HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        if (!req.getSession(false).getAttribute("role").equals("admin")) {
            return StringResponses.ONLY_ADMINS;
        }

        Gson gson = new GsonBuilder().create();

        UpdateUserAdminStatusRequest request =
                gson.fromJson(json, UpdateUserAdminStatusRequest.class);

        if (request.getEmail().equals(req.getSession(false).getAttribute("user"))) {
            logger.debug("Admin {} tried to de-admin him/herself", request.getEmail());
            return "Can't de-admin yourself";
        }

        Optional<User> u = repository.findById(request.getEmail());
        if (u.isEmpty()) {
            logger.debug("Tried to make a non-existing user {} not admin", request.getEmail());

            return "Could not find a match for Email";
        } else if (u.get().getRole().equals("admin")) {
            logger.debug("Removing user {}'s admin privileges", request.getEmail());

            User opUser = u.get();
            opUser.setRole("staff");

            repository.save(opUser);

            return "Made staff";
        } else {
            logger.debug("Tried to make non-admin into staff");

            return "No need to make staff";
        }
    }

    /**
     * Gets the currently logged in user.
     *
     * @return username of the logged in user
     */
    @GetMapping(path = "getThisUser")
    public @ResponseBody
    String getThisUser(HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        return (String) req.getSession(false).getAttribute("user");
    }

    /**
     * Gets the currently logged in user's role.
     *
     * @return role of the logged in user
     */
    @GetMapping(path = "getThisRole")
    public @ResponseBody
    String getThisRole(HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        return (String) req.getSession(false).getAttribute("role");
    }

    /**
     * Turns a list of Users into a response object.
     *
     * @param users List of users
     * @return A response object
     */
    private UsersResponse userListToResponse(List<User> users) {
        List<UserResponse> userResponses = new ArrayList<>();

        for (User user : users) {
            userResponses.add(userToResponse(user));
        }

        return new UsersResponse(userResponses);
    }

    /**
     * Turns a User into a user response object.
     *
     * @param user The user entity
     * @return A response object
     */
    private UserResponse userToResponse(User user) {
        return new UserResponse(
                user.getEmail(),
                user.getRole(),
                user.getPasswordHash()
        );
    }
}
