package nl.tudelft.oopp.cars.controllertests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.transaction.Transactional;

import nl.tudelft.oopp.cars.controllers.AnnouncementController;

import nl.tudelft.oopp.shared.requests.create.CreateAnnouncementRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveAnnouncementRequest;
import nl.tudelft.oopp.shared.requests.read.GetByDateRequest;
import nl.tudelft.oopp.shared.requests.read.GetByIdRequest;
import nl.tudelft.oopp.shared.responses.content.AnnouncementResponse;
import nl.tudelft.oopp.shared.responses.content.AnnouncementsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.Modifying;
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
public class AnnouncementControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AnnouncementController controller;

    @Autowired
    private WebApplicationContext webApplicationContext;

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
    public void addAnnouncementTest() throws Exception {

        Gson gson = new GsonBuilder().create();

        Calendar posted = Calendar.getInstance();
        Calendar rel = Calendar.getInstance();

        CreateAnnouncementRequest request = new CreateAnnouncementRequest(posted, rel, "TESTtitle",
                "content", "user");

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/addAnnouncement")
                .cookie(ses)
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        assertEquals("Announcement saved.", str);

        String str2 = controller.getAllAnnouncements(req, res);
        AnnouncementsResponse response = gson.fromJson(str2, AnnouncementsResponse.class);
        List<AnnouncementResponse> resList = response.getAnnouncements();

        for (AnnouncementResponse a : resList) {
            if (a.getTitle().startsWith("TEST")) {
                RemoveAnnouncementRequest b = new RemoveAnnouncementRequest(a.getId());
                controller.removeAnnouncement(gson.toJson(b), req, res);
                break;
            }
        }

    }

    @Test
    public void getAllAnnouncementsTest() throws Exception {

        Gson gson = new GsonBuilder().create();

        Calendar posted = Calendar.getInstance();
        Calendar rel = Calendar.getInstance();

        CreateAnnouncementRequest request = new CreateAnnouncementRequest(posted, rel, "TESTtitle",
                "content", "user");
        controller.addAnnouncement(gson.toJson(request), req, res);
        request = new CreateAnnouncementRequest(posted, rel, "TESTtitle2", "content", "user");
        controller.addAnnouncement(gson.toJson(request), req, res);
        request = new CreateAnnouncementRequest(posted, rel, "TESTtitle3", "content", "user");
        controller.addAnnouncement(gson.toJson(request), req, res);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/getAllAnnouncements")
                .cookie(ses)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        AnnouncementsResponse b =
                gson.fromJson(str, AnnouncementsResponse.class);
        List<AnnouncementResponse> announcements = b.getAnnouncements();
        assertTrue(announcements.size() >= 3);

        // deletes the three test records
        for (AnnouncementResponse a : announcements) {
            if (a.getTitle().startsWith("TEST")) {
                RemoveAnnouncementRequest removeReq = new RemoveAnnouncementRequest(a.getId());
                controller.removeAnnouncement(gson.toJson(removeReq), req, res);
            }
        }

    }

    @Modifying
    @Test
    public void getByIdTest() {

        Gson gson = new GsonBuilder().create();

        Calendar posted = Calendar.getInstance();
        Calendar rel = Calendar.getInstance();

        CreateAnnouncementRequest request = new CreateAnnouncementRequest(posted, rel, "TESTtitle",
                "content", "user");
        controller.addAnnouncement(gson.toJson(request), req, res);
        AnnouncementsResponse response = gson.fromJson(controller.getAllAnnouncements(req, res),
                AnnouncementsResponse.class);
        List<AnnouncementResponse> responseList = response.getAnnouncements();

        long id;
        for (AnnouncementResponse a : responseList) {
            id = a.getId();
            GetByIdRequest temp = new GetByIdRequest(id);
            String anRes = controller.getAnnouncementById(gson.toJson(temp), req, res);
            AnnouncementResponse b = gson.fromJson(anRes, AnnouncementResponse.class);
            assertTrue(a.getTitle().equals(b.getTitle())
                    && a.getPosted().equals(b.getPosted()));
            if (a.getTitle().startsWith("TEST")) {
                controller.removeAnnouncement(gson.toJson(new
                        RemoveAnnouncementRequest(a.getId())), req, res);
            }
        }
    }

    @Modifying
    @Test
    public void removeAnnouncementTest() throws Exception {
        Gson gson = new GsonBuilder().create();

        Calendar posted = Calendar.getInstance();
        Calendar rel = Calendar.getInstance();

        CreateAnnouncementRequest request = new CreateAnnouncementRequest(posted, rel, "TESTtitle",
                "content", "user");
        controller.addAnnouncement(gson.toJson(request), req, res);

        AnnouncementsResponse a = gson.fromJson(controller.getAllAnnouncements(req, res),
                AnnouncementsResponse.class);
        List<AnnouncementResponse> b = a.getAnnouncements();

        long id = 0;
        for (AnnouncementResponse c : b) {
            if (c.getTitle().equals("TESTtitle")) {
                id = c.getId();
            }
        }
        if (id != 0) {
            RemoveAnnouncementRequest request1 = new RemoveAnnouncementRequest(id);
            MvcResult result2 = mvc.perform(MockMvcRequestBuilders.delete("/deleteAnnouncement")
                    .cookie(ses)
                    .content(gson.toJson(request1))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
            String str = result2.getResponse().getContentAsString();
            assertEquals("Announcement deleted.", str);
        } else {
            fail("cannot find object with right title.");
        }
    }

    @Modifying
    @Test
    public void getByDateTest() throws Exception {

        Gson gson = new GsonBuilder().create();

        Calendar posted = Calendar.getInstance();
        Calendar rel = Calendar.getInstance();
        rel.set(2020, 04, 31, 23, 59, 00);

        CreateAnnouncementRequest request = new CreateAnnouncementRequest(posted, rel, "TESTtitle",
                "content", "user");
        controller.addAnnouncement(gson.toJson(request), req, res);
        request = new CreateAnnouncementRequest(posted, rel, "TESTtitle2", "content", "user");
        controller.addAnnouncement(gson.toJson(request), req, res);
        request = new CreateAnnouncementRequest(posted, rel, "TESTtitle3", "content", "user");
        controller.addAnnouncement(gson.toJson(request), req, res);

        GetByDateRequest request2 = new GetByDateRequest(posted);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/getAnnouncementsByDate")
                .cookie(ses)
                .content(gson.toJson(request2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String str = result.getResponse().getContentAsString();
        AnnouncementsResponse response = gson.fromJson(str, AnnouncementsResponse.class);
        List<AnnouncementResponse> announcements = response.getAnnouncements();
        assertTrue(announcements.size() >= 3);

        // deletes the three test records
        for (AnnouncementResponse a : announcements) {
            if (a.getTitle().startsWith("TEST")) {
                RemoveAnnouncementRequest removeReq = new RemoveAnnouncementRequest(a.getId());
                controller.removeAnnouncement(gson.toJson(removeReq), req, res);
            }
        }
    }
}
