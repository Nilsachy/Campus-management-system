package nl.tudelft.oopp.cars.controllertests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.Cookie;

import nl.tudelft.oopp.cars.controllers.UserController;
import nl.tudelft.oopp.cars.entities.User;
import nl.tudelft.oopp.shared.requests.create.CreateUserRequest;

import nl.tudelft.oopp.shared.requests.read.GetByEmailRequest;
import nl.tudelft.oopp.shared.requests.read.GetByUserRoleRequest;
import nl.tudelft.oopp.shared.requests.read.GetUserValidationRequest;

import nl.tudelft.oopp.shared.requests.update.UpdateUserAdminStatusRequest;
import nl.tudelft.oopp.shared.responses.content.UsersResponse;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    UserController controller;

    @Autowired
    private WebApplicationContext context;

    private MockHttpServletRequest req;

    private MockHttpServletResponse res;

    private Cookie ses;

    /**
     * Makes a new HttpServletRequest and HttpServletResponse for each test.
     */
    @BeforeEach
    public void setup() throws Exception {
        this.req = new MockHttpServletRequest();
        this.res = new MockHttpServletResponse();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("role", "admin");
        req.setSession(session);

        ses = TestSession.getInstance(mvc).getSessionCookie();
    }

    @Test
    public void validateUserTest() throws Exception {

        Gson gson = new GsonBuilder().create();

        CreateUserRequest request = new
                CreateUserRequest("name.surname@student.tudelft.nl", 1480518);
        controller.addNewUser(gson.toJson(request), req, res);

        GetUserValidationRequest request2 = new
                GetUserValidationRequest("name.surname@student.tudelft.nl", 1480518);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/validateLogin")
                .content(gson.toJson(request2))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        assertEquals("SUCCESS correct user and password", str);

        GetUserValidationRequest request3 = new
                GetUserValidationRequest("name.surname@student.tudelft.nl", 000000);

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders.post("/validateLogin")
                .content(gson.toJson(request3))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result2.getResponse().getContentAsString();
        assertEquals("ERROR wrong username or password", str2);

        GetUserValidationRequest request4 = new
                GetUserValidationRequest("name1.surname@student.tudelft.nl", 000000);

        MvcResult result3 = mvc.perform(MockMvcRequestBuilders.post("/validateLogin")
                .content(gson.toJson(request4))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str3 = result3.getResponse().getContentAsString();
        assertEquals("ERROR user not in DB.", str3);


        controller.removeUserByEmail(
                gson.toJson(new GetByEmailRequest("name.surname@student.tudelft.nl")), req, res);
    }

    @Test
    public void addNewUserTest() throws Exception {

        Gson gson = new GsonBuilder().create();

        CreateUserRequest request = new
                CreateUserRequest("name.surname@student.tudelft.nl", 1480518);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/addUser")
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        assertEquals("SUCCESS User added", str);

        CreateUserRequest request2 = new
                CreateUserRequest("name.surname@student.tudelft.nl", 1480518);

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders.post("/addUser")
                .content(gson.toJson(request2))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result2.getResponse().getContentAsString();
        assertEquals("ERROR User already exists", str2);

        controller.removeUserByEmail(
                gson.toJson(new GetByEmailRequest("name.surname@student.tudelft.nl")), req, res);
    }

    @Test
    public void getUserTest() throws Exception {

        Gson gson = new GsonBuilder().create();

        CreateUserRequest request = new
                CreateUserRequest("name.surname@student.tudelft.nl", 1480518);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/addUser")
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        assertEquals("SUCCESS User added", str);

        GetByEmailRequest request2 = new GetByEmailRequest("name.surname@student.tudelft.nl");

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders.post("/getUser")
                .cookie(ses)
                .content(gson.toJson(request2))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result2.getResponse().getContentAsString();
        User user = gson.fromJson(str2, User.class);

        assertEquals(1480518, user.getPasswordHash());
        controller.removeUserByEmail(
                gson.toJson(new GetByEmailRequest("name.surname@student.tudelft.nl")), req, res);

        MvcResult result3 = mvc.perform(MockMvcRequestBuilders.post("/getUser")
                .cookie(ses)
                .content(gson.toJson(request2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str3 = result3.getResponse().getContentAsString();
        User user2 = gson.fromJson(str3, User.class);
        assertNull(user2);
    }

    @Test
    public void removeUserTest() throws Exception {

        Gson gson = new GsonBuilder().create();

        CreateUserRequest request = new
                CreateUserRequest("name.surname@student.tudelft.nl", 1480518);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/addUser")
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        assertEquals("SUCCESS User added", str);

        GetByEmailRequest request2 = new GetByEmailRequest("name.surname@student.tudelft.nl");

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders.post("/getUser")
                .cookie(ses)
                .content(gson.toJson(request2))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result2.getResponse().getContentAsString();
        User user = gson.fromJson(str2, User.class);

        assertEquals(1480518, user.getPasswordHash());

        MvcResult result3 = mvc.perform(MockMvcRequestBuilders.delete("/removeUserByEmail")
                .cookie(ses)
                .content(gson.toJson(request2))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str3 = result3.getResponse().getContentAsString();
        assertEquals("User deleted.", str3);

        MvcResult result4 = mvc.perform(MockMvcRequestBuilders.delete("/removeUserByEmail")
                .cookie(ses)
                .content(gson.toJson(request2))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str4 = result4.getResponse().getContentAsString();
        assertEquals("User not deleted.", str4);
    }

    @Test
    public void getAllUserTest() throws Exception {

        Gson gson = new GsonBuilder().create();

        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/getAllUsers")
                .cookie(ses)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        UsersResponse users = gson.fromJson(str, UsersResponse.class);

        assertTrue(users.getUsers().size() > 0);
    }

    @Test
    public void getByRoleTest() throws Exception {

        Gson gson = new GsonBuilder().create();

        GetByUserRoleRequest request = new GetByUserRoleRequest("student");

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/getUsersByRoles")
                .cookie(ses)
                .content(gson.toJson(request))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        UsersResponse users = gson.fromJson(str, UsersResponse.class);
        assertTrue(users.getUsers().size() > 0);
    }

    @Test
    public void makeAdminTest() throws Exception {
        Gson gson = new GsonBuilder().create();

        UpdateUserAdminStatusRequest request =
                new UpdateUserAdminStatusRequest("makeadmin.test@tudelft.nl");

        CreateUserRequest newUser1 = new
                CreateUserRequest("makeadmin.test@tudelft.nl", 1480542);
        controller.addNewUser(gson.toJson(newUser1), req, res);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/makeAdmin")
                .cookie(ses)
                .content(gson.toJson(request))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        assertEquals("Made Admin", str);

        request =
                new UpdateUserAdminStatusRequest("makenulladmin.test@tudelft.nl");

        result = mvc.perform(MockMvcRequestBuilders.post("/makeAdmin")
                .cookie(ses)
                .content(gson.toJson(request))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        str = result.getResponse().getContentAsString();
        assertEquals("Could not find a match for Email", str);

        CreateUserRequest newUser2 = new
                CreateUserRequest("makeadmin.test@student.tudelft.nl", 1480542);

        mvc.perform(MockMvcRequestBuilders.post("/addUser")
                .content(gson.toJson(newUser2))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        request =
                new UpdateUserAdminStatusRequest("makeadmin.test@student.tudelft.nl");

        result = mvc.perform(MockMvcRequestBuilders.post("/makeAdmin")
                .cookie(ses)
                .content(gson.toJson(request))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        str = result.getResponse().getContentAsString();
        assertEquals("Student can't be admin", str);

        controller.removeUserByEmail(
                gson.toJson(new GetByEmailRequest("makeadmin.test@student.tudelft.nl")), req, res);
        controller.removeUserByEmail(
                gson.toJson(new GetByEmailRequest("makeadmin.test@tudelft.nl")), req, res);
    }

    @Test
    public void removeAdminTest() throws Exception {
        Gson gson = new GsonBuilder().create();

        UpdateUserAdminStatusRequest updateAdmin =
                new UpdateUserAdminStatusRequest("admin.test@tudelft.nl");

        CreateUserRequest newUser1 = new
                CreateUserRequest("admin.test@tudelft.nl", 1480542);
        controller.addNewUser(gson.toJson(newUser1), req, res);
        controller.makeAdmin(gson.toJson(updateAdmin), req, res);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/makeStaff")
                .cookie(ses)
                .content(gson.toJson(updateAdmin))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        assertEquals("Made staff", str);

        UpdateUserAdminStatusRequest removeSelfAdmin =
                new UpdateUserAdminStatusRequest("admin@tudelft.nl");

        result = mvc.perform(MockMvcRequestBuilders.post("/makeStaff")
                .cookie(ses)
                .content(gson.toJson(removeSelfAdmin))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        str = result.getResponse().getContentAsString();
        assertEquals("Can't de-admin yourself", str);

        result = mvc.perform(MockMvcRequestBuilders.post("/makeStaff")
                .cookie(ses)
                .content(gson.toJson(
                        new UpdateUserAdminStatusRequest("admin.test@student.tudelft")))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        str = result.getResponse().getContentAsString();
        assertEquals("Could not find a match for Email", str);

        CreateUserRequest newUser2 = new
                CreateUserRequest("admin.test@student.tudelft.nl", 1480542);

        controller.addNewUser(gson.toJson(newUser2), req, res);

        result = mvc.perform(MockMvcRequestBuilders.post("/makeStaff")
                .cookie(ses)
                .content(gson.toJson(
                        new UpdateUserAdminStatusRequest("admin.test@student.tudelft.nl")))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        str = result.getResponse().getContentAsString();
        assertEquals("No need to make staff", str);

        controller.removeUserByEmail(
                gson.toJson(new GetByEmailRequest("admin.test@student.tudelft.nl")), req, res);
        controller.removeUserByEmail(
                gson.toJson(new GetByEmailRequest("admin.test@tudelft.nl")), req, res);
    }
}
