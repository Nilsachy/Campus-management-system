package nl.tudelft.oopp.cars.controllertests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Calendar;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.transaction.Transactional;
import nl.tudelft.oopp.cars.controllers.RoomReservationController;
import nl.tudelft.oopp.shared.requests.create.CreateRoomReservationRequest;
import nl.tudelft.oopp.shared.requests.delete.DeleteRoomReservationRequest;
import nl.tudelft.oopp.shared.requests.read.GetByEmailRequest;
import nl.tudelft.oopp.shared.responses.content.ReservationsResponse;
import nl.tudelft.oopp.shared.responses.content.RoomReservationResponse;
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
public class RoomReservationControllerTest {

    @Autowired
    private MockMvc mvc;

    //@Autowired
    //ReservationsController reservationsController;

    @Autowired
    RoomReservationController roomReservationController;

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
    public void getAllRoomReservationsTest() throws Exception {
        Calendar d2 = Calendar.getInstance();
        d2.set(2000, 3, 28);

        CreateRoomReservationRequest reservationRequest1 = new CreateRoomReservationRequest(d2,
                    "15:00-17:00",
                    "testforReserveRoomTest@student.tudelft.nl",
                    "remove",
                    "this room",
                    "please");

        Gson gson = new GsonBuilder().create();

        roomReservationController.reserveRoom(gson.toJson(reservationRequest1), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest1), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest1), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest1), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest1), req, res);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/getAllRoomReservations")
                .cookie(ses)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String str = result.getResponse().getContentAsString();

        ReservationsResponse  reservationsResponse = gson.fromJson(str, ReservationsResponse.class);
        List<RoomReservationResponse> reservations =
                reservationsResponse.getRoomReservationResponses();
        assertTrue(reservations.size() >= 5);

    }

    @Test
    @Transactional
    public void reserveRoomTest() throws Exception {
        Calendar d2 = Calendar.getInstance();
        d2.set(2000, 3, 28);

        CreateRoomReservationRequest reservationRequest1 = new CreateRoomReservationRequest(d2,
                    "15:00-17:00",
                    "testforReserveRoomTest1777@student.tudelft.nl",
                    "remove",
                    "this room",
                    "please");
        Gson gson = new GsonBuilder().create();

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/reserveRoom")
                .cookie(ses)
                .content(gson.toJson(reservationRequest1))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        assertEquals("Room Reservation Saved.", str);
        GetByEmailRequest emailRequest = new GetByEmailRequest(
                "testforReserveRoomTest1777@student.tudelft.nl");

        ReservationsResponse reservationsResponse = gson.fromJson(
                roomReservationController.findRoomReservationByUser(
                        gson.toJson(emailRequest), req, res), ReservationsResponse.class);

        assertEquals(reservationsResponse.getRoomReservationResponses().size(), 1);
    }

    @Test
    @Transactional
    public void cancelRoomTest() throws Exception {
        Calendar d2 = Calendar.getInstance();
        d2.set(2000, 3, 28);

        CreateRoomReservationRequest reservationRequest1 = new CreateRoomReservationRequest(d2,
                "15:00-17:00",
                "testforReserveRoomTest1777@student.tudelft.nl",
                "remove",
                "this room",
                "please");
        Gson gson = new GsonBuilder().create();
        String str = roomReservationController.reserveRoom(gson.toJson(reservationRequest1),
                req, res);

        assertEquals("Room Reservation Saved.", str);
        GetByEmailRequest emailRequest =
                new GetByEmailRequest("testforReserveRoomTest1777@student.tudelft.nl");
        ReservationsResponse reservationsResponse = gson.fromJson(
                roomReservationController.findRoomReservationByUser(
                        gson.toJson(emailRequest), req, res), ReservationsResponse.class);
        assertEquals(reservationsResponse.getRoomReservationResponses().size(), 1);
        int id = reservationsResponse.getRoomReservationResponses().get(0).getId();
        DeleteRoomReservationRequest request = new DeleteRoomReservationRequest(id);

        MvcResult result1 = mvc.perform(MockMvcRequestBuilders.post("/cancelRoom")
                .cookie(ses)
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str1 = result1.getResponse().getContentAsString();
        assertEquals(str1, "Room Reservation Deleted.");

        ReservationsResponse reservationsResponse1 = gson.fromJson(
                roomReservationController.findRoomReservationByUser(
                        gson.toJson(emailRequest), req, res), ReservationsResponse.class);
        assertEquals(reservationsResponse1.getRoomReservationResponses().size(), 0);
    }

    @Test
    @Transactional
    public void findRoomReservationByUserTest() throws Exception {
        Calendar d2 = Calendar.getInstance();
        d2.set(2000, 3, 28);

        CreateRoomReservationRequest reservationRequest1 = new CreateRoomReservationRequest(d2,
                "15:00-17:00",
                "testforReserveRoomTest177735@student.tudelft.nl",
                "remove",
                "this room",
                "please");

        Gson gson = new GsonBuilder().create();

        roomReservationController.reserveRoom(gson.toJson(reservationRequest1), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest1), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest1), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest1), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest1), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest1), req, res);

        GetByEmailRequest emailRequest = new
                GetByEmailRequest("testforReserveRoomTest177735@student.tudelft.nl");
        MvcResult result1 = mvc.perform(MockMvcRequestBuilders.post("/findRoomReservationByUser")
                .cookie(ses)
                .content(gson.toJson(emailRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String str1 = result1.getResponse().getContentAsString();

        ReservationsResponse reservationsResponse1 =
                gson.fromJson(str1, ReservationsResponse.class);

        assertEquals(reservationsResponse1.getRoomReservationResponses().size(), 6);
    }


}
