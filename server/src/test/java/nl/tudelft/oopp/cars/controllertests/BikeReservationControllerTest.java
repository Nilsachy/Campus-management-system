package nl.tudelft.oopp.cars.controllertests;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import nl.tudelft.oopp.cars.controllers.BikeStorageController;
import nl.tudelft.oopp.cars.entities.BikeReservation;
import nl.tudelft.oopp.cars.entities.BikeStorage;
import nl.tudelft.oopp.shared.requests.create.AddBikeStorageRequest;
import nl.tudelft.oopp.shared.requests.create.CreateBikeRentalRequest;
import nl.tudelft.oopp.shared.requests.delete.DeleteBikeReservationRequest;
import nl.tudelft.oopp.shared.requests.read.GetAvailableBikeStoragesRequest;
import nl.tudelft.oopp.shared.requests.read.GetByEmailRequest;
import nl.tudelft.oopp.shared.requests.read.GetByFacultyRequest;
import nl.tudelft.oopp.shared.responses.content.AvailableBikeStoragesResponse;
import nl.tudelft.oopp.shared.responses.content.BikeReservationResponse;
import nl.tudelft.oopp.shared.responses.content.BikeStorageResponse;
import nl.tudelft.oopp.shared.responses.content.ReservationsResponse;

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
class BikeReservationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    BikeReservationController bikeReservationController;

    @Autowired
    BikeStorageController bikeStorageController;

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
    public void findBikeReservationByUserTest() throws Exception {

        Calendar from = Calendar.getInstance();
        from.set(2020, 3, 5, 7, 0, 0);

        Calendar from1 = Calendar.getInstance();
        from1.set(2020, 3, 2, 7, 0, 0);

        Calendar to = Calendar.getInstance();
        to.set(2020, 3, 7, 12, 0, 0);

        Gson gson = new GsonBuilder().create();
        CreateBikeRentalRequest createBikeReservationRequest =
                new CreateBikeRentalRequest(
                        "user97831", "faculty1", "faculty1", from, to);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);

        CreateBikeRentalRequest createBikeReservationRequest1 =
                new CreateBikeRentalRequest(
                        "user97832", "faculty1", "faculty1", from1, to);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest1), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest1), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest1), req, res);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/findBikeReservationByUser")
                .cookie(ses)
                .content(gson.toJson(new GetByEmailRequest("user97831")))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String str = result.getResponse().getContentAsString();

        ReservationsResponse reservationsResponse = gson.fromJson(str, ReservationsResponse.class);
        List<BikeReservationResponse> reservations =
                reservationsResponse.getBikeReservationResponses();
        assertEquals(4, reservations.size());

        MvcResult result1 = mvc.perform(MockMvcRequestBuilders.post("/findBikeReservationByUser")
                .cookie(ses)
                .content(gson.toJson(new GetByEmailRequest("user97832")))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str1 = result1.getResponse().getContentAsString();
        ReservationsResponse reservationsResponse1 =
                gson.fromJson(str1, ReservationsResponse.class);
        List<BikeReservationResponse>  reservations1 =
                reservationsResponse1.getBikeReservationResponses();
        assertEquals(3, reservations1.size());
    }

    @Test
    @Transactional
    public void findByFromFacultyTest() throws Exception {

        Calendar from = Calendar.getInstance();
        from.set(2020, 3, 5, 7, 0, 0);

        Calendar from1 = Calendar.getInstance();
        from1.set(2020, 3, 2, 7, 0, 0);

        Calendar to = Calendar.getInstance();
        to.set(2020, 3, 7, 12, 0, 0);

        Gson gson = new GsonBuilder().create();
        CreateBikeRentalRequest createBikeReservationRequest =
                new CreateBikeRentalRequest(
                        "user97831", "faculty98561", "faculty98561", from, to);

        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);

        CreateBikeRentalRequest createBikeReservationRequest1 =
                new CreateBikeRentalRequest(
                        "user97832", "faculty98562", "faculty98562", from, to);

        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest1), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest1), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest1), req, res);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/findByFromFaculty")
                .cookie(ses)
                .content(gson.toJson(new GetByFacultyRequest("faculty98561")))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String str = result.getResponse().getContentAsString();

        ReservationsResponse reservationsResponse = gson.fromJson(str, ReservationsResponse.class);
        List<BikeReservationResponse> reservations =
                reservationsResponse.getBikeReservationResponses();

        assertEquals(6, reservations.size());

        MvcResult result1 = mvc.perform(MockMvcRequestBuilders.post("/findByFromFaculty")
                .cookie(ses)
                .content(gson.toJson(new GetByFacultyRequest("faculty98562")))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String str1 = result1.getResponse().getContentAsString();
        ReservationsResponse reservationsResponse1 =
                gson.fromJson(str1, ReservationsResponse.class);
        List<BikeReservationResponse> reservations1 =
                reservationsResponse1.getBikeReservationResponses();

        assertEquals(3, reservations1.size());
    }

    @Test
    @Transactional
    public void findByToFacultyTest() throws Exception {

        Calendar from = Calendar.getInstance();
        from.set(2020, 3, 5, 7, 0, 0);

        Calendar from1 = Calendar.getInstance();
        from1.set(2020, 3, 2, 7, 0, 0);

        Calendar to = Calendar.getInstance();
        to.set(2020, 3, 7, 12, 0, 0);

        Gson gson = new GsonBuilder().create();
        CreateBikeRentalRequest createBikeReservationRequest =
                new CreateBikeRentalRequest(
                        "user97831", "faculty98561", "faculty98561", from, to);

        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);

        CreateBikeRentalRequest createBikeReservationRequest1 =
                new CreateBikeRentalRequest(
                        "user97832", "faculty98562", "faculty98562", from, to);

        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest1), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest1), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest1), req, res);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/findByToFaculty")
                .cookie(ses)
                .content(gson.toJson(new GetByFacultyRequest("faculty98561")))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();

        ReservationsResponse reservationsResponse = gson.fromJson(str, ReservationsResponse.class);
        List<BikeReservationResponse> reservations =
                reservationsResponse.getBikeReservationResponses();

        assertEquals(6, reservations.size());

        MvcResult result1 = mvc.perform(MockMvcRequestBuilders.post("/findByToFaculty")
                .cookie(ses)
                .content(gson.toJson(new GetByFacultyRequest("faculty98562")))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str1 = result1.getResponse().getContentAsString();
        ReservationsResponse reservationsResponse1 =
                gson.fromJson(str1, ReservationsResponse.class);
        List<BikeReservationResponse> reservations1 =
                reservationsResponse1.getBikeReservationResponses();

        assertEquals(3, reservations1.size());
    }

    @Test
    @Transactional
    public void rentBikeTest() throws Exception {

        Calendar from = Calendar.getInstance();
        from.set(2020, 3, 5, 7, 0, 0);

        Calendar from1 = Calendar.getInstance();
        from1.set(2020, 3, 2, 7, 0, 0);

        Calendar to = Calendar.getInstance();
        to.set(2020, 3, 7, 12, 0, 0);

        Gson gson = new GsonBuilder().create();
        CreateBikeRentalRequest createBikeReservationRequest =
                new CreateBikeRentalRequest(
                        "user9783132313", "faculty98561", "faculty98561", from, to);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/rentBike")
                .cookie(ses)
                .content(gson.toJson(createBikeReservationRequest))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String str = result.getResponse().getContentAsString();


        assertEquals(str,"Saved.");

        GetByEmailRequest request =
                new GetByEmailRequest("user9783132313");
        String string1 = bikeReservationController
                .findBikeReservationByUser(gson.toJson(request), req, res);
        ReservationsResponse reservationsResponse =
                gson.fromJson(string1, ReservationsResponse.class);
        assertEquals(reservationsResponse
                .getBikeReservationResponses().size(), 1);
        assertEquals(reservationsResponse
                .getBikeReservationResponses().get(0).getUser(), "user9783132313");

    }



    @Test
    @Transactional
    public void getAvailableBikeStoragesTest() throws Exception {
        Gson gson = new GsonBuilder().create();
        bikeStorageController.addBikeStorage(gson.toJson(
                new AddBikeStorageRequest("faculty98561", 10)), req, res);
        bikeStorageController.addBikeStorage(gson.toJson(
                new AddBikeStorageRequest("faculty98562", 10)), req, res);
        bikeStorageController.addBikeStorage(gson.toJson(
                new AddBikeStorageRequest("faculty98563", 10)), req, res);
        bikeStorageController.addBikeStorage(gson.toJson(
                new AddBikeStorageRequest("faculty98564", 10)), req, res);

        Calendar from = Calendar.getInstance();
        from.set(2020, 3, 5, 7, 0, 0);

        Calendar from1 = Calendar.getInstance();
        from1.set(2020, 3, 2, 7, 0, 0);

        Calendar to = Calendar.getInstance();
        to.set(2020, 3, 7, 12, 0, 0);

        Calendar fromGet = Calendar.getInstance();
        fromGet.set(2020, 3, 5, 11, 11, 0);

        Calendar toGet = Calendar.getInstance();
        toGet.set(2020, 3, 7, 11, 11, 0);

        CreateBikeRentalRequest createBikeReservationRequest =
                new CreateBikeRentalRequest(
                        "user97831", "faculty98561", "faculty98561", from, to);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        CreateBikeRentalRequest createBikeReservationRequest1 =
                new CreateBikeRentalRequest(
                        "user97832", "faculty98562", "faculty98562", from, to);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest1), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest1), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest1), req, res);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/getAvailableBikeStorages")
                .cookie(ses)
                .content(gson.toJson(
                        new GetAvailableBikeStoragesRequest(fromGet, toGet)))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String str = result.getResponse().getContentAsString();

        AvailableBikeStoragesResponse response =
                gson.fromJson(str, AvailableBikeStoragesResponse.class);
        List<BikeStorageResponse> bikeStorageResponses = response.getBikeStorages();
        assertTrue(bikeStorageResponses.size() >= 3);
        //assertTrue(bikeStorageResponses.contains(new BikeStorageResponse("faculty98562")));
        //assertFalse(bikeStorageResponses.contains(new BikeStorageResponse("faculty98561")));

        Calendar from2 = Calendar.getInstance();
        from2.set(2020, 3, 5, 0, 0, 0);
        Calendar from2take2 = Calendar.getInstance();
        from2take2.set(2020, 3, 5, 0, 1, 0);

        CreateBikeRentalRequest createBikeReservationRequest2 =
                new CreateBikeRentalRequest(
                        "user97832", "faculty98562", "faculty98561",
                        from2, from2take2);

        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest2), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest2), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest2), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest2), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest2), req, res);

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders
                .post("/getAvailableBikeStorages")
                .cookie(ses)
                .content(gson.toJson(
                        new GetAvailableBikeStoragesRequest(fromGet, toGet)))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String str2 = result2.getResponse().getContentAsString();
        AvailableBikeStoragesResponse response2 =
                gson.fromJson(str2, AvailableBikeStoragesResponse.class);
        List<BikeStorageResponse> bikeStorageResponses2 = response2.getBikeStorages();
        assertTrue(bikeStorageResponses2.size() >= 3);
        //assertFalse(bikeStorageResponses2.contains(new BikeStorageResponse("faculty98562")));
        //assertTrue(bikeStorageResponses2.contains(new BikeStorageResponse("faculty98561")));

    }


    @Test
    @Transactional
    public void getAvailableBikesForReturnTest() throws Exception {
        Gson gson = new GsonBuilder().create();

        bikeStorageController.addBikeStorage(gson.toJson(
                new AddBikeStorageRequest("faculty98561", 10)), req, res);
        bikeStorageController.addBikeStorage(gson.toJson(
                new AddBikeStorageRequest("faculty98562", 10)), req, res);
        bikeStorageController.addBikeStorage(gson.toJson(
                new AddBikeStorageRequest("faculty98563", 10)), req, res);
        bikeStorageController.addBikeStorage(gson.toJson(
                new AddBikeStorageRequest("faculty98564", 10)), req, res);

        Calendar from = Calendar.getInstance();
        from.set(2020, 3, 5, 7, 0, 0);

        Calendar from1 = Calendar.getInstance();
        from1.set(2020, 3, 2, 7, 0, 0);

        Calendar to = Calendar.getInstance();
        to.set(2020, 3, 7, 12, 0, 0);

        Calendar fromGet = Calendar.getInstance();
        fromGet.set(2020, 3, 5, 11, 11, 0);

        Calendar toGet = Calendar.getInstance();
        toGet.set(2020, 3, 7, 11, 11, 0);

        CreateBikeRentalRequest createBikeReservationRequest =
                new CreateBikeRentalRequest(
                        "user97831", "faculty98561", "faculty98561", from, to);

        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);

        CreateBikeRentalRequest createBikeReservationRequest1 =
                new CreateBikeRentalRequest(
                        "user97832", "faculty98562", "faculty98562", from, to);

        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest1), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest1), req, res);
        //bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest1), req, res);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/getAvailableBikesForReturn")
                .cookie(ses)
                .content(gson.toJson(
                        new GetAvailableBikeStoragesRequest(fromGet, toGet)))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String str = result.getResponse().getContentAsString();

        AvailableBikeStoragesResponse response =
                gson.fromJson(str, AvailableBikeStoragesResponse.class);
        List<BikeStorageResponse> bikeStorageResponses = response.getBikeStorages();
        assertTrue(bikeStorageResponses.size() >= 3);

        //assertTrue(bikeStorageResponses.contains(new BikeStorageResponse("faculty98562")));
        //assertFalse(bikeStorageResponses.contains(new BikeStorageResponse("faculty98561")));

        from.set(Calendar.HOUR_OF_DAY, 0);
        Calendar fromtake2 = Calendar.getInstance();
        fromtake2.set(2020, 3, 5, 0, 1, 0);

        CreateBikeRentalRequest createBikeReservationRequest2 =
                new CreateBikeRentalRequest(
                        "user97832", "faculty98562", "faculty98561", from, fromtake2);

        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest2), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest2), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest2), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest2), req, res);
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest2), req, res);

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders
                .post("/getAvailableBikeStorages")
                .cookie(ses)
                .content(gson.toJson(
                        new GetAvailableBikeStoragesRequest(fromGet, toGet)))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result2.getResponse().getContentAsString();
        AvailableBikeStoragesResponse response2 =
                gson.fromJson(str2, AvailableBikeStoragesResponse.class);
        List<BikeStorageResponse> bikeStorageResponses2 = response2.getBikeStorages();
        assertTrue(bikeStorageResponses2.size() >= 3);

        //assertFalse(bikeStorageResponses2.contains(new BikeStorageResponse("faculty98562")));
        //assertTrue(bikeStorageResponses2.contains(new BikeStorageResponse("faculty98561")));

    }

    @Test
    @Transactional
    public void deleteBikeReservationTest() throws Exception {
        Calendar from = Calendar.getInstance();
        from.set(2020, 3, 5, 7, 0, 0);

        Calendar from1 = Calendar.getInstance();
        from1.set(2020, 3, 2, 7, 0, 0);

        Calendar to = Calendar.getInstance();
        to.set(2020, 3, 7, 12, 0, 0);

        CreateBikeRentalRequest createBikeReservationRequest =
                new CreateBikeRentalRequest(
                        "user978sds3sdsd", "faculty98561", "faculty98561", from, to);

        Gson gson = new GsonBuilder().create();
        bikeReservationController.rentBike(gson.toJson(createBikeReservationRequest), req, res);
        GetByEmailRequest request =
                new GetByEmailRequest("user978sds3sdsd");
        String string1 = bikeReservationController
                .findBikeReservationByUser(gson.toJson(request), req, res);
        ReservationsResponse reservationsResponse =
                gson.fromJson(string1, ReservationsResponse.class);
        int id = reservationsResponse.getBikeReservationResponses().get(0).getId();

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders
                .post("/deleteBikeReservation")
                .cookie(ses)
                .content(gson.toJson(
                        new DeleteBikeReservationRequest(id)))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result2.getResponse().getContentAsString();
        assertEquals(str2, "deleted.");

        String string2 = bikeReservationController
                .findBikeReservationByUser(gson.toJson(request), req, res);

        ReservationsResponse reservationsResponse2 =
                gson.fromJson(string2, ReservationsResponse.class);

        assertEquals(reservationsResponse2.getBikeReservationResponses().size(), 0);
    }



    @Test
    void getListOfAvailableBikesTest() {

        Calendar from = Calendar.getInstance();
        from.set(2020, 3, 5, 7, 0, 0);

        Calendar from1 = Calendar.getInstance();
        from1.set(2020, 3, 2, 7, 0,0);

        Calendar to = Calendar.getInstance();
        to.set(2020, 3, 7, 12, 0, 0);

        Calendar fromGet = Calendar.getInstance();
        fromGet.set(2020, 3, 5, 11, 11, 0);

        Calendar toGet = Calendar.getInstance();
        toGet.set(2020, 3, 7, 11, 11, 0);

        List<BikeStorage> bikeStorages = new ArrayList<BikeStorage>();
        bikeStorages.add(new BikeStorage("faculty1", 10));
        bikeStorages.add(new BikeStorage("faculty2", 20));

        List<BikeReservation> bikeReservations = new ArrayList<BikeReservation>();

        bikeReservations.add(new BikeReservation("user1", "faculty1",
                "faculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "faculty1",
                "faculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "faculty1",
                "faculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "faculty1",
                "faculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "faculty1",
                "faculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "faculty1",
                "faculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "faculty1",
                "faculty1", from, to));

        GetAvailableBikeStoragesRequest request = new
                GetAvailableBikeStoragesRequest(fromGet, toGet);
        List<BikeStorage> bikeStorages1 = BikeReservationController
                .getListOfAvailableBikes(request, bikeStorages, bikeReservations);
        assertEquals(bikeStorages1.size(), 1);

        bikeReservations.add(new BikeReservation("user1", "faculty1",
                "faculty1", from, to));
        List<BikeStorage> bikeStorages2 = BikeReservationController
                .getListOfAvailableBikes(request, bikeStorages, bikeReservations);
        assertEquals(bikeStorages2.size(), 1);

        bikeReservations.add(new BikeReservation("user1", "faculty1",
                "faculty1", from1, from));


        List<BikeStorage> bikeStorages3 = BikeReservationController
                .getListOfAvailableBikes(request, bikeStorages, bikeReservations);
        assertEquals(bikeStorages3.size(), 1);

        bikeReservations.add(new BikeReservation("user1", "faculty2",
                "faculty1", from, to));

        List<BikeStorage> bikeStorages4 = BikeReservationController
                .getListOfAvailableBikes(request, bikeStorages, bikeReservations);
        assertEquals(bikeStorages4.size(), 1);
    }


    @Test
    void getListOfAvailableReturnBikesTest() {

        Calendar from = Calendar.getInstance();
        from.set(2020, 3, 5, 7, 0, 0);

        Calendar from1 = Calendar.getInstance();
        from1.set(2020, 3, 2, 7, 0,0);

        Calendar to = Calendar.getInstance();
        to.set(2020, 3, 7, 12, 0, 0);

        Calendar fromGet = Calendar.getInstance();
        fromGet.set(2020, 3, 5, 11, 11, 0);

        Calendar toGet = Calendar.getInstance();
        toGet.set(2020, 3, 7, 11, 11, 0);

        List<BikeStorage> bikeStorages = new ArrayList<BikeStorage>();
        bikeStorages.add(new BikeStorage("faculty1", 10));
        bikeStorages.add(new BikeStorage("faculty2", 20));

        List<BikeReservation> bikeReservations = new ArrayList<BikeReservation>();
        bikeReservations.add(new BikeReservation("user1", "faculty1",
                "faculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "faculty1",
                "faculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "faculty1",
                "faculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "faculty1",
                "faculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "faculty1",
                "faculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "faculty1",
                "faculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "faculty1",
                "faculty1", from, to));


        GetAvailableBikeStoragesRequest request = new
                GetAvailableBikeStoragesRequest(fromGet, toGet);
        List<BikeStorage> bikeStorages1 = BikeReservationController
                .getListOfAvailableReturnBikes(request, bikeStorages, bikeReservations);
        assertEquals(bikeStorages1.size(), 1);

        bikeReservations.add(new BikeReservation("user1", "faculty1",
                "faculty1", from, to));
        List<BikeStorage> bikeStorages2 = BikeReservationController
                .getListOfAvailableReturnBikes(request, bikeStorages, bikeReservations);
        assertEquals(bikeStorages2.size(), 1);

        bikeReservations.add(new BikeReservation("user1", "faculty1",
                "faculty1", from1, from));


        List<BikeStorage> bikeStorages3 = BikeReservationController
                .getListOfAvailableReturnBikes(request, bikeStorages, bikeReservations);
        assertEquals(bikeStorages3.size(), 1);

        bikeReservations.add(new BikeReservation("user1", "faculty2",
                "faculty1", from, to));

        List<BikeStorage> bikeStorages4 = BikeReservationController
                .getListOfAvailableReturnBikes(request, bikeStorages, bikeReservations);
        assertEquals(bikeStorages4.size(), 1);
    }

    @Test
    void filterByFromFacultyTest() {
        Calendar from = Calendar.getInstance();
        from.set(2020, 3, 5, 7, 0, 0);

        Calendar from1 = Calendar.getInstance();
        from1.set(2020, 3, 2, 7, 0,0);

        Calendar to = Calendar.getInstance();
        to.set(2020, 3, 7, 12, 0, 0);

        Calendar fromGet = Calendar.getInstance();
        fromGet.set(2020, 3, 5, 11, 11, 0);

        Calendar toGet = Calendar.getInstance();
        toGet.set(2020, 3, 7, 11, 11, 0);

        List<BikeReservation> bikeReservations = new ArrayList<BikeReservation>();

        bikeReservations.add(new BikeReservation("user1", "fromFaculty1",
                "toFaculty2", from, to));
        bikeReservations.add(new BikeReservation("user1", "fromFaculty1",
                "toFaculty2", from, to));
        bikeReservations.add(new BikeReservation("user1", "fromFaculty1",
                "toFaculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "fromFaculty1",
                "toFaculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "fromFaculty1",
                "toFaculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "fromFaculty2",
                "toFaculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "fromFaculty2",
                "toFaculty1", from, to));

        List<BikeReservation> b1 = BikeReservationController
                .filterByFromFaculty(bikeReservations, "fromFaculty2");
        assertEquals(b1.size(), 2);

        List<BikeReservation> b2 = BikeReservationController
                .filterByFromFaculty(bikeReservations, "fromFaculty1");
        assertEquals(b2.size(), 5);
    }

    @Test
    void filterByToFacultyTest() {
        Calendar from = Calendar.getInstance();
        from.set(2020, 3, 5, 7, 0, 0);

        Calendar from1 = Calendar.getInstance();
        from1.set(2020, 3, 2, 7, 0,0);

        Calendar to = Calendar.getInstance();
        to.set(2020, 3, 7, 12, 0, 0);

        Calendar fromGet = Calendar.getInstance();
        fromGet.set(2020, 3, 5, 11, 11, 0);

        Calendar toGet = Calendar.getInstance();
        toGet.set(2020, 3, 7, 11, 11, 0);

        List<BikeReservation> bikeReservations = new ArrayList<BikeReservation>();

        bikeReservations.add(new BikeReservation("user1", "fromFaculty1",
                "toFaculty2", from, to));
        bikeReservations.add(new BikeReservation("user1", "fromFaculty1",
                "toFaculty2", from, to));
        bikeReservations.add(new BikeReservation("user1", "fromFaculty1",
                "toFaculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "fromFaculty1",
                "toFaculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "fromFaculty1",
                "toFaculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "fromFaculty2",
                "toFaculty1", from, to));
        bikeReservations.add(new BikeReservation("user1", "fromFaculty2",
                "toFaculty1", from, to));

        List<BikeReservation> b1 = BikeReservationController
                .filterByToFaculty(bikeReservations, "toFaculty2");
        assertEquals(b1.size(), 2);

        List<BikeReservation> b2 = BikeReservationController
                .filterByToFaculty(bikeReservations, "toFaculty1");
        assertEquals(b2.size(), 5);
    }
}