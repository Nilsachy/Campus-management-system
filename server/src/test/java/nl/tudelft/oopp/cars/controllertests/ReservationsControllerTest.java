package nl.tudelft.oopp.cars.controllertests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.Cookie;

import nl.tudelft.oopp.cars.controllers.BikeReservationController;
import nl.tudelft.oopp.cars.controllers.ReservationsController;
import nl.tudelft.oopp.cars.controllers.RoomReservationController;
import nl.tudelft.oopp.cars.controllers.UserController;
import nl.tudelft.oopp.cars.entities.BikeReservation;
import nl.tudelft.oopp.cars.entities.RoomReservation;
import nl.tudelft.oopp.shared.requests.create.CreateBikeRentalRequest;
import nl.tudelft.oopp.shared.requests.create.CreateRoomReservationRequest;
import nl.tudelft.oopp.shared.requests.create.CreateUserRequest;
import nl.tudelft.oopp.shared.requests.read.GetReservationsRequest;
import nl.tudelft.oopp.shared.responses.content.BikeReservationResponse;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReservationsControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    ReservationsController reservationsController;

    @Autowired
    RoomReservationController roomReservationController;

    @Autowired
    BikeReservationController bikeReservationController;

    @Autowired
    UserController userController;

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
    public void getCurrentRoomReservationsForUserTest() throws Exception {
        CreateUserRequest createUserRequest =
                new CreateUserRequest("test974812@student.tudelft.nl", 12312321);
        Gson gson = new GsonBuilder().create();
        userController.addNewUser(gson.toJson(createUserRequest), req, res);

        CreateUserRequest createUserRequest1 =
                new CreateUserRequest("test97481322@student.tudelft.nl", 12312321);
        userController.addNewUser(gson.toJson(createUserRequest1), req, res);
        Calendar d1 = Calendar.getInstance();
        d1.set(1988, 3, 28);
        CreateRoomReservationRequest reservationRequest1 = new CreateRoomReservationRequest(d1,
                "15:00-17:00",
                "test974812@student.tudelft.nl",
                "remove",
                "123",
                "please");
        Calendar d2 = Calendar.getInstance();
        d2.set(2030, 3, 28);
        CreateRoomReservationRequest reservationRequest2 = new CreateRoomReservationRequest(d2,
                "15:00-17:00",
                "test974812@student.tudelft.nl",
                "remove",
                "123",
                "please");

        CreateRoomReservationRequest reservationRequest3 = new CreateRoomReservationRequest(d2,
                "15:00-17:00",
                "test9ss74812@student.tudelft.nl",
                "remove",
                "123",
                "please");

        roomReservationController.reserveRoom(gson.toJson(reservationRequest1), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest1), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest2), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest2), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest2), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest2), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest3), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest3), req, res);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .post("/getReservations")
                .cookie(ses)
                .content(gson.toJson(new GetReservationsRequest("admin@tudelft.nl", "active")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        ReservationsResponse response = gson.fromJson(str, ReservationsResponse.class);
        List<RoomReservationResponse> reservations = response.getRoomReservationResponses();
        assertTrue(reservations.size() >= 6);

        MvcResult result1 = mvc.perform(MockMvcRequestBuilders
                .post("/getReservations")
                .cookie(ses)
                .content(gson.toJson(new GetReservationsRequest("test974812@student.tudelft.nl",
                                            "active")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str1 = result1.getResponse().getContentAsString();
        ReservationsResponse response1 = gson.fromJson(str1, ReservationsResponse.class);
        List<RoomReservationResponse> reservations1 = response1.getRoomReservationResponses();
        assertTrue(reservations1.size() == 4);
        assertEquals(reservations1.get(0).getUser(), "test974812@student.tudelft.nl");
        assertEquals(reservations1.get(1).getUser(), "test974812@student.tudelft.nl");
        assertEquals(reservations1.get(2).getUser(), "test974812@student.tudelft.nl");
        assertEquals(reservations1.get(3).getUser(), "test974812@student.tudelft.nl");

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders
                .post("/getReservations")
                .cookie(ses)
                .content(gson.toJson(new GetReservationsRequest(
                        "test97481322@student.tudelft.nl", "active")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result2.getResponse().getContentAsString();
        ReservationsResponse response2 = gson.fromJson(str2, ReservationsResponse.class);
        List<RoomReservationResponse> reservations2 = response2.getRoomReservationResponses();
        assertFalse(reservations2.size() > 0);
    }

    @Test
    @Transactional
    public void getCurrentBikeReservationsForUserTest() throws Exception {
        CreateUserRequest createUserRequest =
                new CreateUserRequest("test974812@student.tudelft.nl",12312321);
        Gson gson = new GsonBuilder().create();
        userController.addNewUser(gson.toJson(createUserRequest), req, res);
        CreateUserRequest createUserRequest1 =
                new CreateUserRequest("test97481322@student.tudelft.nl", 12312321);
        userController.addNewUser(gson.toJson(createUserRequest1), req, res);
        Calendar d1 = Calendar.getInstance();
        d1.set(1988, 3, 28, 7, 0, 0);

        Calendar d1take2 = Calendar.getInstance();
        d1take2.set(1988, 3, 28, 12, 0, 0);

        CreateBikeRentalRequest reservationRequest1 =
                new CreateBikeRentalRequest(
                        "test974812@student.tudelft.nl", "faculty98561", "faculty98561",
                        d1, d1take2);

        Calendar d2 = Calendar.getInstance();
        d2.set(2030, 3, 28, 7, 0, 0);
        Calendar d2take2 = Calendar.getInstance();
        d2take2.set(2030, 3, 28, 12, 0, 0);
        CreateBikeRentalRequest reservationRequest2 =
                new CreateBikeRentalRequest(
                        "test974812@student.tudelft.nl", "faculty98561", "faculty98561",
                        d2, d2take2);

        CreateBikeRentalRequest reservationRequest3 =
                new CreateBikeRentalRequest(
                        "tqwe812@student.tudelft.nl", "faculty98561", "faculty98561",
                        d2, d2take2);

        bikeReservationController.rentBike(gson.toJson(reservationRequest1), req, res);
        bikeReservationController.rentBike(gson.toJson(reservationRequest1), req, res);
        bikeReservationController.rentBike(gson.toJson(reservationRequest2), req, res);
        bikeReservationController.rentBike(gson.toJson(reservationRequest2), req, res);
        bikeReservationController.rentBike(gson.toJson(reservationRequest2), req, res);
        bikeReservationController.rentBike(gson.toJson(reservationRequest2), req, res);
        bikeReservationController.rentBike(gson.toJson(reservationRequest3), req, res);
        bikeReservationController.rentBike(gson.toJson(reservationRequest3), req, res);
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .post("/getReservations")
                .cookie(ses)
                .content(gson.toJson(new GetReservationsRequest("admin@tudelft.nl", "active")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        ReservationsResponse response = gson.fromJson(str, ReservationsResponse.class);
        List<BikeReservationResponse> reservations = response.getBikeReservationResponses();
        assertTrue(reservations.size() >= 6);

        MvcResult result1 = mvc.perform(MockMvcRequestBuilders
                .post("/getReservations")
                .cookie(ses)
                .content(gson.toJson(
                        new GetReservationsRequest("test974812@student.tudelft.nl", "active")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str1 = result1.getResponse().getContentAsString();
        ReservationsResponse response1 = gson.fromJson(str1, ReservationsResponse.class);
        List<BikeReservationResponse> reservations1 = response1.getBikeReservationResponses();
        assertEquals(4, reservations1.size());

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders
                .post("/getReservations")
                .cookie(ses)
                .content(gson.toJson(new GetReservationsRequest(
                        "test97481322@student.tudelft.nl", "active")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result2.getResponse().getContentAsString();
        ReservationsResponse response2 = gson.fromJson(str2, ReservationsResponse.class);
        List<BikeReservationResponse> reservations2 = response2.getBikeReservationResponses();
        assertFalse(reservations2.size() > 0);
    }

    @Test
    @Transactional
    public void getPreviousRoomReservationsForUserTest() throws Exception {
        CreateUserRequest createUserRequest =
                new CreateUserRequest("test974812@student.tudelft.nl",
                        12312321);
        Gson gson = new GsonBuilder().create();
        userController.addNewUser(gson.toJson(createUserRequest), req, res);
        CreateUserRequest createUserRequest1 =
                new CreateUserRequest("test97481322@student.tudelft.nl", 12312321);
        userController.addNewUser(gson.toJson(createUserRequest1), req, res);
        Calendar d1 = Calendar.getInstance();
        d1.set(1988, 3, 28);
        CreateRoomReservationRequest reservationRequest1 = new CreateRoomReservationRequest(d1,
                "15:00-17:00",
                "test974812@student.tudelft.nl",
                "remove",
                "123",
                "please");
        roomReservationController.reserveRoom(gson.toJson(reservationRequest1), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest1), req, res);

        CreateRoomReservationRequest reservationRequest0 = new CreateRoomReservationRequest(d1,
                "15:00-17:00",
                "234@student.tudelft.nl",
                "remove",
                "123",
                "please");
        roomReservationController.reserveRoom(gson.toJson(reservationRequest0), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest0), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest0), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest0), req, res);

        Calendar d2 = Calendar.getInstance();
        d2.set(2030, 3, 28);
        CreateRoomReservationRequest reservationRequest2 = new CreateRoomReservationRequest(d2,
                "15:00-17:00",
                "test974812@student.tudelft.nl",
                "remove",
                "123",
                "please");
        roomReservationController.reserveRoom(gson.toJson(reservationRequest2), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest2), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest2), req, res);
        roomReservationController.reserveRoom(gson.toJson(reservationRequest2), req, res);



        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .post("/getReservations")
                .cookie(ses)
                .content(gson.toJson(new GetReservationsRequest("admin@tudelft.nl", "previous")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        ReservationsResponse response = gson.fromJson(str, ReservationsResponse.class);
        List<RoomReservationResponse> reservations = response.getRoomReservationResponses();
        assertTrue(reservations.size() >= 6);

        MvcResult result1 = mvc.perform(MockMvcRequestBuilders
                .post("/getReservations")
                .cookie(ses)
                .content(gson.toJson(new GetReservationsRequest(
                        "test974812@student.tudelft.nl", "previous")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str1 = result1.getResponse().getContentAsString();
        ReservationsResponse response1 = gson.fromJson(str1, ReservationsResponse.class);
        List<RoomReservationResponse> reservations1 = response1.getRoomReservationResponses();
        assertEquals(2, reservations1.size());
        assertEquals(reservations1.get(0).getUser(), "test974812@student.tudelft.nl");
        assertEquals(reservations1.get(1).getUser(), "test974812@student.tudelft.nl");

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders
                .post("/getReservations")
                .cookie(ses)
                .content(gson.toJson(new GetReservationsRequest(
                        "test97481322@student.tudelft.nl", "previous")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result2.getResponse().getContentAsString();
        ReservationsResponse response2 = gson.fromJson(str2, ReservationsResponse.class);
        List<RoomReservationResponse> reservations2 = response2.getRoomReservationResponses();
        assertFalse(reservations2.size() > 0);
    }

    @Test
    @Transactional
    public void getPreviousBikeReservationsForUserTest() throws Exception {
        CreateUserRequest createUserRequest =
                new CreateUserRequest("test974812@student.tudelft.nl",
                        12312321);
        Gson gson = new GsonBuilder().create();
        userController.addNewUser(gson.toJson(createUserRequest), req, res);
        CreateUserRequest createUserRequest1 =
                new CreateUserRequest("test97481322@student.tudelft.nl", 12312321);
        userController.addNewUser(gson.toJson(createUserRequest1), req, res);
        Calendar d1 = Calendar.getInstance();
        d1.set(1988, 3, 28, 7, 0, 0);
        Calendar d1take2 = Calendar.getInstance();
        d1take2.set(1988, 3, 28, 12, 0, 0);

        CreateBikeRentalRequest reservationRequest1 =
                new CreateBikeRentalRequest(
                        "test974812@student.tudelft.nl", "faculty98561", "faculty98561",
                        d1, d1take2);
        bikeReservationController.rentBike(gson.toJson(reservationRequest1), req, res);
        bikeReservationController.rentBike(gson.toJson(reservationRequest1), req, res);

        CreateBikeRentalRequest reservationRequest0 =
                new CreateBikeRentalRequest(
                        "sddsad@student.tudelft.nl", "faculty98561", "faculty98561",
                        d1, d1take2);
        bikeReservationController.rentBike(gson.toJson(reservationRequest0), req, res);
        bikeReservationController.rentBike(gson.toJson(reservationRequest0), req, res);
        bikeReservationController.rentBike(gson.toJson(reservationRequest0), req, res);
        bikeReservationController.rentBike(gson.toJson(reservationRequest0), req, res);

        Calendar d2 = Calendar.getInstance();
        d2.set(2030, 3, 28, 7, 0, 0);
        Calendar d2take2 = Calendar.getInstance();
        d2take2.set(2030, 3, 28, 12, 0, 0);
        CreateBikeRentalRequest reservationRequest2 =
                new CreateBikeRentalRequest(
                        "test974812@student.tudelft.nl", "faculty98561", "faculty98561",
                        d2, d2take2);

        bikeReservationController.rentBike(gson.toJson(reservationRequest2), req, res);
        bikeReservationController.rentBike(gson.toJson(reservationRequest2), req, res);
        bikeReservationController.rentBike(gson.toJson(reservationRequest2), req, res);
        bikeReservationController.rentBike(gson.toJson(reservationRequest2), req, res);


        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .post("/getReservations")
                .cookie(ses)
                .content(gson.toJson(new GetReservationsRequest("admin@tudelft.nl", "previous")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        ReservationsResponse response = gson.fromJson(str, ReservationsResponse.class);
        List<BikeReservationResponse> reservations = response.getBikeReservationResponses();
        assertTrue(reservations.size() >= 6);

        MvcResult result1 = mvc.perform(MockMvcRequestBuilders
                .post("/getReservations")
                .cookie(ses)
                .content(gson.toJson(new GetReservationsRequest(
                        "test974812@student.tudelft.nl", "previous")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str1 = result1.getResponse().getContentAsString();
        ReservationsResponse response1 = gson.fromJson(str1, ReservationsResponse.class);
        List<BikeReservationResponse> reservations1 = response1.getBikeReservationResponses();
        assertTrue(reservations1.size() == 2);

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders
                .post("/getReservations")
                .cookie(ses)
                .content(gson.toJson(new GetReservationsRequest(
                        "test97481322@student.tudelft.nl", "previous")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result2.getResponse().getContentAsString();
        ReservationsResponse response2 = gson.fromJson(str2, ReservationsResponse.class);
        List<BikeReservationResponse> reservations2 = response2.getBikeReservationResponses();
        assertFalse(reservations2.size() > 0);
    }

    @Test
    public void getReservationsRoomsTest() {

        Calendar d1 = Calendar.getInstance();
        d1.set(2020, 2, 1);
        Calendar d2 = Calendar.getInstance();
        d2.set(2030, 5, 19);
        Calendar d3 = Calendar.getInstance();
        d3.set(2020, 1, 13);
        Calendar d4 = Calendar.getInstance();
        d4.set(1987, 4, 19);

        RoomReservation r1 = new RoomReservation(1, d1,
                "09:00-11:00",
                "E.Voorneveld@student.tudelft.nl",
                "None",
                "Drebbelweg",
                "Project Room 1");

        RoomReservation r2 = new RoomReservation(2, d2,
                "15:00-17:00",
                "E.Voorneveld@student.tudelft.nl",
                "EEMCS",
                "Pulse",
                "Pulse Hall 6");

        RoomReservation r3 = new RoomReservation(3, d2,
                "13:00-15:00",
                "E.Voorneveld@student.tudelft.nl",
                "None",
                "Aula",
                "Aula Hall C");

        RoomReservation r4 = new RoomReservation(3, d3,
                "13:00-17:00",
                "E.Voorneveld@student.tudelft.nl",
                "None",
                "Aula",
                "Aula Hall C");

        RoomReservation r5 = new RoomReservation(3, d4,
                "13:00-15:00",
                "E.Voorneveld@student.tudelft.nl",
                "None",
                "Aula",
                "Aula Hall C");

        ReservationsController rrc = new ReservationsController();

        List<RoomReservation> reservations = new ArrayList<>();
        reservations.add(r1);
        reservations.add(r2);
        reservations.add(r3);
        reservations.add(r4);
        reservations.add(r5);

        List<RoomReservation> reservations1 = rrc.getReservationsRooms("active", reservations);
        assertEquals(reservations1.size(), 2);

        List<RoomReservation> reservations2 = rrc.getReservationsRooms("previous", reservations);
        assertEquals(reservations2.size(), 3);

    }

    @Test
    public void getReservationsBikesTest() {

        Calendar d1 = Calendar.getInstance();
        d1.set(2020, 2, 1, 11, 0, 0);
        Calendar d5 = Calendar.getInstance();
        d5.set(2020, 2, 3, 11, 0, 0);

        Calendar d2 = Calendar.getInstance();
        d2.set(2030, 5, 19, 11, 0, 0);
        Calendar d6 = Calendar.getInstance();
        d6.set(2050, 5, 30, 11, 0, 0);

        Calendar d3 = Calendar.getInstance();
        d3.set(2020, 2, 13, 11, 0, 0);
        Calendar d7 = Calendar.getInstance();
        d7.set(2020, 2, 20, 11, 0, 0);

        Calendar d4 = Calendar.getInstance();
        d4.set(1987, 4, 19, 11, 0, 0);
        Calendar d8 = Calendar.getInstance();
        d8.set(2001, 4, 19, 11, 0, 0);

        BikeReservation r1 = new BikeReservation("donghwikim@student.tudelft.nl",
                "Drebbelweg",
                "Drebbelweg",
                d1, d5);

        BikeReservation r2 = new BikeReservation("donghwikim@student.tudelft.nl",
                "Drebbelweg",
                "Drebbelweg",
                d2, d6);

        BikeReservation r3 = new BikeReservation("donghwikim@student.tudelft.nl",
                "Drebbelweg",
                "Drebbelweg",
                d2, d6);

        BikeReservation r4 = new BikeReservation("donghwikim@student.tudelft.nl",
                "Drebbelweg",
                "Drebbelweg",
                d3, d7);

        BikeReservation r5 = new BikeReservation("donghwikim@student.tudelft.nl",
                "Drebbelweg",
                "Drebbelweg",
                d4, d8);

        ReservationsController rrc = new ReservationsController();

        List<BikeReservation> reservations = new ArrayList<>();
        reservations.add(r1);
        reservations.add(r2);
        reservations.add(r3);
        reservations.add(r4);
        reservations.add(r5);

        // expected first, actual next please.
        List<BikeReservation> reservations1 = rrc.getReservationsBikes("active", reservations);
        assertEquals(2, reservations1.size());

        List<BikeReservation> reservations2 = rrc.getReservationsBikes("previous", reservations);
        assertEquals(3, reservations2.size());

    }
}
