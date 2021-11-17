package nl.tudelft.oopp.cars.controllertests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.Cookie;

import nl.tudelft.oopp.cars.controllers.FacultyController;
import nl.tudelft.oopp.shared.requests.create.CreateFacultyRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveFacultyRequest;
import nl.tudelft.oopp.shared.requests.read.GetByNameRequest;

import nl.tudelft.oopp.shared.responses.content.FacultiesResponse;
import nl.tudelft.oopp.shared.responses.content.FacultyResponse;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;


@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTest {

    @Autowired
    FacultyController controller;

    @Autowired
    private MockMvc mvc;

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
    @Transactional
    public void addFacultyTest() throws Exception {

        Gson gson = new GsonBuilder().create();

        CreateFacultyRequest request = new
                CreateFacultyRequest("id33", "name1", "01512345678");

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/createFaculty")
                .cookie(ses)
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        assertEquals("Faculty added or update saved.", str);

        CreateFacultyRequest request2 = new
                CreateFacultyRequest("id22", "name2", "01512345678");

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders.post("/createFaculty")
                .cookie(ses)
                .content(gson.toJson(request2))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result2.getResponse().getContentAsString();
        assertEquals("Faculty added or update saved.", str2);

        //controller.removeFaculty(gson.toJson(new RemoveFacultyRequest("id1")), req, res);
    }

    @Test
    @Transactional
    public void removeFacultyTest() throws Exception {

        Gson gson = new GsonBuilder().create();

        CreateFacultyRequest request = new
                CreateFacultyRequest("id1234", "name3231", "01512345678");
        controller.createFaculty(gson.toJson(request), req, res);

        GetByNameRequest request2 = new GetByNameRequest("name3231");
        String json = controller.getFacultyByName(gson.toJson(request2), req, res);
        FacultyResponse faculty = gson.fromJson(json, FacultyResponse.class);
        assertEquals("01512345678", faculty.getPhone());

        RemoveFacultyRequest request3 = new RemoveFacultyRequest("id1234");

        MvcResult result = mvc.perform(MockMvcRequestBuilders.delete("/removeFaculty")
                .cookie(ses)
                .content(gson.toJson(request3))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        assertEquals("Faculty deleted.", str);

        String json2 = controller.getFacultyByName(gson.toJson(request2), req, res);
        FacultyResponse faculty2 = gson.fromJson(json2, FacultyResponse.class);

        assertNull(faculty2);

    }

    @Test
    @Transactional
    public void getAllFacultiesTest() throws Exception {

        Gson gson = new GsonBuilder().create();

        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/getAllFaculties")
                .cookie(ses)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result.getResponse().getContentAsString();
        FacultiesResponse faculties = gson.fromJson(str2, FacultiesResponse.class);
        assertTrue(faculties.getFaculties().size() > 0);

    }

    @Test
    @Transactional
    public void getFacultyByName() throws Exception {

        Gson gson = new GsonBuilder().create();

        CreateFacultyRequest request = new
                CreateFacultyRequest("id123", "name1", "01512345678");

        controller.createFaculty(gson.toJson(request), req, res);

        GetByNameRequest request2 = new GetByNameRequest("name1");
        String json = controller.getFacultyByName(gson.toJson(request2), req, res);
        FacultyResponse faculty = gson.fromJson(json, FacultyResponse.class);
        assertEquals("01512345678", faculty.getPhone());

        //controller.removeFaculty(gson.toJson(new RemoveFacultyRequest("id1")), req, res);
    }

}
