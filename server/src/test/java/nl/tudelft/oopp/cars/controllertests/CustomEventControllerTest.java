package nl.tudelft.oopp.cars.controllertests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.http.Cookie;

import nl.tudelft.oopp.cars.controllers.CustomEventController;
import nl.tudelft.oopp.shared.requests.create.CreateCustomEventRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveEventByIdRequest;
import nl.tudelft.oopp.shared.requests.read.GetEventsByUserRequest;

import nl.tudelft.oopp.shared.requests.read.GetEventsInSpanRequest;
import nl.tudelft.oopp.shared.requests.update.UpdateCustomEventRequest;
import nl.tudelft.oopp.shared.responses.content.CustomEventResponse;
import nl.tudelft.oopp.shared.responses.content.CustomEventsResponse;
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
public class CustomEventControllerTest {

    Gson gson = new GsonBuilder().create();

    @Autowired
    private MockMvc mvc;

    @Autowired
    CustomEventController customEventController;

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
    public void addNewCustomEventTest() throws Exception {
        CreateCustomEventRequest request = createBasicEventForUser("user1");

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/addCustomEvent")
                .cookie(ses)
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String confirmation = result.getResponse().getContentAsString();
        assertEquals("Custom event saved.", confirmation);

        CustomEventsResponse response = gson.fromJson(
                customEventController.getAllCustomEvents(req, res),
                CustomEventsResponse.class);

        assertTrue(1 <= response.getEvents().size());
    }

    @Test
    @Transactional
    public void getAllCustomEventsTest() throws Exception {
        // MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/getAllCustomEvents")
        //         .cookie(ses)
        //         .accept(MediaType.APPLICATION_JSON))
        //         .andExpect(status().isOk())
        //         .andReturn();
        //
        // CustomEventsResponse response = gson.fromJson(
        //         result.getResponse().getContentAsString(), CustomEventsResponse.class);
        //
        // assertEquals(0, response.getEvents().size());

        CreateCustomEventRequest request1 = createBasicEventForUser("user1");

        customEventController.addNewCustomEvent(gson.toJson(request1), req, res);

        MvcResult result1 = mvc.perform(MockMvcRequestBuilders.get("/getAllCustomEvents")
                .cookie(ses)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CustomEventsResponse response1 = gson.fromJson(
                result1.getResponse().getContentAsString(), CustomEventsResponse.class);

        assertTrue(1 <= response1.getEvents().size());

        CreateCustomEventRequest request2 = createBasicEventForUser("user2");

        customEventController.addNewCustomEvent(gson.toJson(request2), req, res);

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders.get("/getAllCustomEvents")
                .cookie(ses)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CustomEventsResponse response2 = gson.fromJson(
                result2.getResponse().getContentAsString(), CustomEventsResponse.class);

        assertTrue(2 <= response2.getEvents().size());

        CustomEventResponse responseRequest1 = null;
        CustomEventResponse responseRequest2 = null;
        for (CustomEventResponse resp :  response2.getEvents()) {
            if (resp.getUser().equals("user1")) {
                responseRequest1 = resp;
            } else if (resp.getUser().equals("user2")) {
                responseRequest2 = resp;
            }
        }

        //ID does not need to be checked, as it does not exist in the request
        //assertEquals(request1.getUser(), responseRequest1.getUser());
        //assertEquals(request1.getTitle(), responseRequest1.getTitle());
        //assertEquals(request1.getStart(), responseRequest1.getStart());
        //assertEquals(request1.getEnd(), responseRequest1.getEnd());
        //assertEquals(request1.getAddress(), responseRequest1.getAddress());
        //assertEquals(request1.getDescription(), responseRequest1.getDescription());

        //ID does not need to be checked, as it does not exist in the request
        //assertEquals(request2.getUser(), responseRequest2.getUser());
        //assertEquals(request2.getTitle(), responseRequest2.getTitle());
        //assertEquals(request2.getStart(), responseRequest2.getStart());
        //assertEquals(request2.getEnd(), responseRequest2.getEnd());
        //assertEquals(request2.getAddress(), responseRequest2.getAddress());
        //assertEquals(request2.getDescription(), responseRequest2.getDescription());
    }

    @Test
    @Transactional
    public void getCustomEventsByUserTest() throws Exception {
        CreateCustomEventRequest addRequest = createBasicEventForUser("user1");
        customEventController.addNewCustomEvent(gson.toJson(addRequest), req, res);

        GetEventsByUserRequest getUser1Request = new GetEventsByUserRequest("user1");

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/getCustomEventsByUser")
                .cookie(ses)
                .content(gson.toJson(getUser1Request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CustomEventsResponse response = gson.fromJson(
                result.getResponse().getContentAsString(), CustomEventsResponse.class);

        assertEquals(1, response.getEvents().size());

        addRequest.setUser("user2");
        customEventController.addNewCustomEvent(gson.toJson(addRequest), req, res);

        result = mvc.perform(MockMvcRequestBuilders.post("/getCustomEventsByUser")
                .cookie(ses)
                .content(gson.toJson(getUser1Request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        response = gson.fromJson(
                result.getResponse().getContentAsString(), CustomEventsResponse.class);

        assertEquals(1, response.getEvents().size());

        addRequest.setUser("user1");
        customEventController.addNewCustomEvent(gson.toJson(addRequest), req, res);

        result = mvc.perform(MockMvcRequestBuilders.post("/getCustomEventsByUser")
                .cookie(ses)
                .content(gson.toJson(getUser1Request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        response = gson.fromJson(
                result.getResponse().getContentAsString(), CustomEventsResponse.class);

        assertEquals(2, response.getEvents().size());
    }

    @Test
    @Transactional
    public void removeCustomEventTest() throws Exception {
        CreateCustomEventRequest addRequest = createBasicEventForUser("user1");
        customEventController.addNewCustomEvent(gson.toJson(addRequest), req, res);

        GetEventsByUserRequest getUser1Request = new GetEventsByUserRequest("user1");
        CustomEventsResponse response =
                gson.fromJson(customEventController.getCustomEventsByUser(
                        gson.toJson(getUser1Request), req, res), CustomEventsResponse.class);

        assertEquals(1, response.getEvents().size());

        CustomEventResponse event = response.getEvents().get(0);

        RemoveEventByIdRequest removeEventRequest = new RemoveEventByIdRequest(event.getId());

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/removeCustomEventsById")
                .cookie(ses)
                .content(gson.toJson(removeEventRequest))
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("Deleted event, if present in the DB.",
                result.getResponse().getContentAsString());

        response = gson.fromJson(customEventController.getCustomEventsByUser(
                        gson.toJson(getUser1Request), req, res), CustomEventsResponse.class);

        assertEquals(0, response.getEvents().size());
    }

    @Test
    @Transactional
    public void updateCustomEventTest() throws Exception {
        //Adding an initial event to the controller
        CreateCustomEventRequest addRequest = createBasicEventForUser("user1");
        customEventController.addNewCustomEvent(gson.toJson(addRequest), req, res);

        GetEventsByUserRequest getUser1Request = new GetEventsByUserRequest("user1");

        //Getting the actual event back
        CustomEventsResponse response =
                gson.fromJson(customEventController.getCustomEventsByUser(
                        gson.toJson(getUser1Request), req, res), CustomEventsResponse.class);

        //Making sure the setup was correct and that the rest of the test can function correctly
        assertEquals(1, response.getEvents().size());
        CustomEventResponse event = response.getEvents().get(0);

        //Setting up the update info
        UpdateCustomEventRequest updateRequest = new UpdateCustomEventRequest();

        updateRequest.setTitle("Party Time");
        updateRequest.setAddress("/pub");
        updateRequest.setDescription("Pijpjes draaikolken als dorstige koningen!");
        updateRequest.setStart(new GregorianCalendar(2020, Calendar.APRIL, 20, 20, 30,0));
        updateRequest.setEnd(new GregorianCalendar(2020, Calendar.APRIL, 21, 3, 0,0));

        //These 2 values need to match the original, otherwise no match will be found
        updateRequest.setId(event.getId());
        updateRequest.setUser(event.getUser());

        //Doing the actual updating
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/updateCustomEvent")
                .cookie(ses)
                .content(gson.toJson(updateRequest))
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andReturn();

        //It should have worked
        assertEquals("Updated value", result.getResponse().getContentAsString());

        response = gson.fromJson(customEventController.getCustomEventsByUser(
                        gson.toJson(getUser1Request), req, res), CustomEventsResponse.class);

        //The response should be the same size still
        assertEquals(1, response.getEvents().size());

        event = response.getEvents().get(0);

        //The event should have all the new info from the update request
        assertEquals(updateRequest.getTitle(), event.getTitle());
        assertEquals(updateRequest.getAddress(), event.getAddress());
        assertEquals(updateRequest.getDescription(), event.getDescription());
        assertEquals(updateRequest.getStart(), event.getStart());
        assertEquals(updateRequest.getEnd(), event.getEnd());

        //Mis-setting the user now
        updateRequest.setUser("user2");
        //Changing the description
        updateRequest.setDescription("Knuppels drukken als blauwgebloede zuidasridders!");

        //Doing the wrong update
        result = mvc.perform(MockMvcRequestBuilders.post("/updateCustomEvent")
                .cookie(ses)
                .content(gson.toJson(getUser1Request))
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andReturn();

        assertNotEquals("Updated value", result.getResponse().getContentAsString());

        //Getting the actual event back
        GetEventsByUserRequest user1Request2 = new GetEventsByUserRequest("user1");
        CustomEventsResponse response2 =
                gson.fromJson(customEventController.getCustomEventsByUser(
                        gson.toJson(user1Request2), req, res), CustomEventsResponse.class);

        //Making sure no extra thing was inserted and that the original remained unchanged
        assertEquals(1, response2.getEvents().size());
        assertNotEquals(response2.getEvents().get(0), updateRequest.getDescription());
    }

    @Test
    @Transactional
    public void getEventsInSpanTest() throws Exception {
        //Adding an initial event to the controller
        CreateCustomEventRequest addRequest = createBasicEventForUser("user1");

        //Early event
        addRequest.setStart(new GregorianCalendar(2020, Calendar.APRIL, 10, 10, 0,0));
        addRequest.setEnd(new GregorianCalendar(2020, Calendar.APRIL, 10, 13, 0,0));
        addRequest.setTitle("Early");
        customEventController.addNewCustomEvent(gson.toJson(addRequest), req, res);

        //Middle event
        addRequest.setStart(new GregorianCalendar(2020, Calendar.APRIL, 10, 12, 0,0));
        addRequest.setEnd(new GregorianCalendar(2020, Calendar.APRIL, 10, 14, 30,0));
        addRequest.setTitle("Mid");
        customEventController.addNewCustomEvent(gson.toJson(addRequest), req, res);

        //Late event
        addRequest.setStart(new GregorianCalendar(2020, Calendar.APRIL, 10, 13, 30,0));
        addRequest.setEnd(new GregorianCalendar(2020, Calendar.APRIL, 10, 15, 30,0));
        addRequest.setTitle("Late");
        customEventController.addNewCustomEvent(gson.toJson(addRequest), req, res);

        //Getting the actual events back
        GetEventsByUserRequest getUser1Request = new GetEventsByUserRequest("user1");
        CustomEventsResponse allResponse =
                gson.fromJson(customEventController.getCustomEventsByUser(
                        gson.toJson(getUser1Request), req, res), CustomEventsResponse.class);

        //Making sure that all events are there
        assertEquals(3, allResponse.getEvents().size());

        //Test 1

        //Getting a timespan that only captures the first 2
        GetEventsInSpanRequest earlyMidRequest = new GetEventsInSpanRequest(
                "user1",
                new GregorianCalendar(2020, Calendar.APRIL, 10, 10, 0,0),
                new GregorianCalendar(2020, Calendar.APRIL, 10, 15, 0,0)
        );

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .post("/getCustomEventsInTimespanByUser")
                .cookie(ses)
                .content(gson.toJson(earlyMidRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CustomEventsResponse earlyMidResponse =
                gson.fromJson(result.getResponse().getContentAsString(),
                        CustomEventsResponse.class);

        //Making sure that only the first 2 events are there
        assertEquals(2, earlyMidResponse.getEvents().size());

        //And checking if it was right
        assertEquals("Early", earlyMidResponse.getEvents().get(0).getTitle());
        assertEquals("Mid", earlyMidResponse.getEvents().get(1).getTitle());

        //Test 2

        //Getting a timespan that only captures the last 2
        GetEventsInSpanRequest midLateRequest = new GetEventsInSpanRequest(
                "user1",
                new GregorianCalendar(2020, Calendar.APRIL, 10, 12, 0,0),
                new GregorianCalendar(2020, Calendar.APRIL, 10, 16, 0,0)
        );

        result = mvc.perform(MockMvcRequestBuilders.post("/getCustomEventsInTimespanByUser")
                .cookie(ses)
                .content(gson.toJson(midLateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CustomEventsResponse midLateResponse =
                gson.fromJson(result.getResponse().getContentAsString(),
                    CustomEventsResponse.class);

        //Making sure that only the last 2 events are there
        assertEquals(2, midLateResponse.getEvents().size());

        //And checking if it was right
        assertEquals("Mid", midLateResponse.getEvents().get(0).getTitle());
        assertEquals("Late", midLateResponse.getEvents().get(1).getTitle());
    }

    /**
     * Helper function to easily create some events for a given user.
     * @param user the user for which the event is
     * @return the event request to make the event
     */
    private CreateCustomEventRequest createBasicEventForUser(String user) {
        Calendar start = new GregorianCalendar(2020, Calendar.APRIL, 20, 10, 30,0);
        Calendar end = new GregorianCalendar(2020, Calendar.APRIL, 20, 12, 30,0);

        return new CreateCustomEventRequest(
                user,
                "Doing Dishes For House",
                start,
                end,
                "Mekelweg",
                "Cleaning dishes for the entire student house");
    }
}