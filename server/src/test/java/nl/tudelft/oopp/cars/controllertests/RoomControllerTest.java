package nl.tudelft.oopp.cars.controllertests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.Cookie;

import nl.tudelft.oopp.cars.controllers.RoomController;
import nl.tudelft.oopp.cars.controllers.RoomReservationController;
import nl.tudelft.oopp.shared.requests.create.CreateRoomRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveRoomByIdRequest;
import nl.tudelft.oopp.shared.requests.read.GetByBuildingIdRequest;
import nl.tudelft.oopp.shared.requests.read.GetByFacultyRequest;
import nl.tudelft.oopp.shared.requests.read.GetByRoomCapacityRequest;
import nl.tudelft.oopp.shared.responses.content.RoomsResponse;

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
public class RoomControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    RoomController roomController;

    @Autowired
    RoomReservationController roomReservationController;

    
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
    public void addNewRoomTest() throws Exception {
        Gson gson = new GsonBuilder().create();

        final String jsonStr1 = gson.toJson(new CreateRoomRequest("addRoomTest18989", 10001,
                "room", 1, true, false));
        final String jsonStr2 = gson.toJson(new CreateRoomRequest("addRoomTest18989", 10001,
                "room2", 3, false, true));
        final String jsonStr3 = gson.toJson(new CreateRoomRequest("addRoomTest18989", 10001,
                "room3", 6, true, true));
        final String jsonStr4 = gson.toJson(new CreateRoomRequest("addRoomTest18989", 10001,
                "room4", 9, false, false));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .post("/addNewRoom")
                .cookie(ses)
                .content(jsonStr1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        assertEquals("Room saved", str);

        roomController.addNewRoom(jsonStr2, req, res);
        roomController.addNewRoom(jsonStr3, req, res);
        roomController.addNewRoom(jsonStr4, req, res);

        req.setCookies(ses);

        String roomResponse = roomController
                .getRoomsByFaculty(gson
                        .toJson(new GetByFacultyRequest("addRoomTest18989")), req, res);
        RoomsResponse response = gson.fromJson(roomResponse, RoomsResponse.class);

        assertEquals(response.getRooms().size(), 4);
    }

    @Test
    @Transactional
    public void removeRoomTest() throws Exception {
        Gson gson = new GsonBuilder().create();

        final String jsonStr1 = gson.toJson(new CreateRoomRequest("addRoomTest18989", 10001,
                "room", 1, true, false));
        final String jsonStr2 = gson.toJson(new CreateRoomRequest("addRoomTest18989", 10001,
                "room2", 3, false, true));
        final String jsonStr3 = gson.toJson(new CreateRoomRequest("addRoomTest18989", 10001,
                "room3", 6, true, true));
        final String jsonStr4 = gson.toJson(new CreateRoomRequest("addRoomTest18989", 10001,
                "room4", 9, false, false));

        roomController.addNewRoom(jsonStr1, req, res);
        roomController.addNewRoom(jsonStr2, req, res);
        roomController.addNewRoom(jsonStr3, req, res);
        roomController.addNewRoom(jsonStr4, req, res);

        String roomResponse = roomController
                .getRoomsByFaculty(gson
                        .toJson(new GetByFacultyRequest("addRoomTest18989")), req, res);
        RoomsResponse response = gson.fromJson(roomResponse, RoomsResponse.class);

        assertEquals(response.getRooms().size(), 4);

        int rmId1 = (int)response.getRooms().get(0).getId();
        int rmId2 = (int)response.getRooms().get(1).getId();

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .post("/removeRoom")
                .cookie(ses)
                .content(gson.toJson(new RemoveRoomByIdRequest(rmId1)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        assertEquals("Deleted", str);

        String roomResponse1 = roomController
                .getRoomsByFaculty(gson
                        .toJson(new GetByFacultyRequest("addRoomTest18989")), req, res);
        RoomsResponse response1 = gson.fromJson(roomResponse1, RoomsResponse.class);

        assertEquals(response1.getRooms().size(), 3);
    }

    @Test
    @Transactional
    public void getByBuildingTest() throws Exception {
        Gson gson = new GsonBuilder().create();

        final String jsonStr1 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 10001,
                "room", 1, true, false));
        final String jsonStr2 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 1008516,
                "room2", 3, false, true));
        final String jsonStr3 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 10001,
                "room3", 6, true, true));
        final String jsonStr4 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 1008516,
                "room4", 9, false, false));

        roomController.addNewRoom(jsonStr1, req, res);
        roomController.addNewRoom(jsonStr2, req, res);
        roomController.addNewRoom(jsonStr3, req, res);
        roomController.addNewRoom(jsonStr4, req, res);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .post("/getRoomByBuilding")
                .cookie(ses)
                .content(gson.toJson(new GetByBuildingIdRequest(1008516)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        RoomsResponse roomsResponse = gson.fromJson(str, RoomsResponse.class);

        assertEquals(roomsResponse.getRooms().size(), 2);

        assertEquals(roomsResponse.getRooms().get(0).getBuilding(), 1008516);
        assertEquals(roomsResponse.getRooms().get(1).getBuilding(), 1008516);

    }

    @Test
    @Transactional
    public void getAllRoomsTest() throws Exception {
        Gson gson = new GsonBuilder().create();

        final String jsonStr1 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 10001,
                "room", 1, true, false));
        final String jsonStr2 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 100856,
                "room2", 3, false, true));
        final String jsonStr3 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 10001,
                "room3", 6, true, true));
        final String jsonStr4 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 100856,
                "room4", 9, false, false));

        roomController.addNewRoom(jsonStr1, req, res);
        roomController.addNewRoom(jsonStr2, req, res);
        roomController.addNewRoom(jsonStr3, req, res);
        roomController.addNewRoom(jsonStr4, req, res);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/getAllRooms")
                .cookie(ses)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        RoomsResponse roomsResponse = gson.fromJson(str, RoomsResponse.class);

        assertTrue(roomsResponse.getRooms().size() >= 4);

    }

    @Test
    @Transactional
    public void getByFacultyTest() throws Exception {
        Gson gson = new GsonBuilder().create();

        final String jsonStr1 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 10001,
                "room", 1, true, false));
        final String jsonStr2 = gson.toJson(new CreateRoomRequest("addRoomTest18829", 100856,
                "room22", 3, false, true));
        final String jsonStr3 = gson.toJson(new CreateRoomRequest("addRoomTest18829", 10001,
                "room22", 6, true, true));
        final String jsonStr4 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 100856,
                "room4", 9, false, false));

        roomController.addNewRoom(jsonStr1, req, res);
        roomController.addNewRoom(jsonStr2, req, res);
        roomController.addNewRoom(jsonStr3, req, res);
        roomController.addNewRoom(jsonStr4, req, res);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .post("/getRoomsByFaculty")
                .cookie(ses)
                .content(gson.toJson(new GetByFacultyRequest("addRoomTest18829")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        RoomsResponse roomsResponse = gson.fromJson(str, RoomsResponse.class);

        assertEquals(roomsResponse.getRooms().size(), 2);

        assertEquals(roomsResponse.getRooms().get(0).getName(), "room22");
        assertEquals(roomsResponse.getRooms().get(1).getName(), "room22");
    }

    @Test
    @Transactional
    public void getByCapacityTest() throws Exception {
        Gson gson = new GsonBuilder().create();

        final String jsonStr1 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 10001,
                "room", 1, true, false));
        final String jsonStr2 = gson.toJson(new CreateRoomRequest("addRoomTest18829", 100856,
                "room22", 3, false, true));
        final String jsonStr3 = gson.toJson(new CreateRoomRequest("addRoomTest18829", 10001,
                "room22", 12, true, true));
        final String jsonStr4 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 100856,
                "room4", 12, false, false));

        roomController.addNewRoom(jsonStr1, req, res);
        roomController.addNewRoom(jsonStr2, req, res);
        roomController.addNewRoom(jsonStr3, req, res);
        roomController.addNewRoom(jsonStr4, req, res);


        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .post("/getRoomsByCapacity")
                .cookie(ses)
                .content(gson.toJson(new GetByRoomCapacityRequest(12)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        RoomsResponse roomsResponse = gson.fromJson(str, RoomsResponse.class);

        assertEquals(roomsResponse.getRooms().size(), 2);

        assertEquals(roomsResponse.getRooms().get(0).getCapacity(), 12);
        assertEquals(roomsResponse.getRooms().get(1).getCapacity(), 12);
    }

    @Test
    @Transactional
    public void getByMinCapacityTest() throws Exception {
        Gson gson = new GsonBuilder().create();

        final String jsonStr1 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 10001,
                "room", 1, true, false));
        final String jsonStr2 = gson.toJson(new CreateRoomRequest("addRoomTest18829", 100856,
                "room22", 7, false, true));
        final String jsonStr3 = gson.toJson(new CreateRoomRequest("addRoomTest18829", 10001,
                "room22", 12, true, true));
        final String jsonStr4 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 100856,
                "room4", 12, false, false));

        roomController.addNewRoom(jsonStr1, req, res);
        roomController.addNewRoom(jsonStr2, req, res);
        roomController.addNewRoom(jsonStr3, req, res);
        roomController.addNewRoom(jsonStr4, req, res);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .post("/getRoomsByMinCapacity")
                .cookie(ses)
                .content(gson.toJson(new GetByRoomCapacityRequest(6)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        RoomsResponse roomsResponse = gson.fromJson(str, RoomsResponse.class);

        assertTrue(roomsResponse.getRooms().size() >= 3);

        assertTrue(roomsResponse.getRooms().get(0).getCapacity() >= 6);
        assertTrue(roomsResponse.getRooms().get(1).getCapacity() >= 6);
        assertTrue(roomsResponse.getRooms().get(2).getCapacity() >= 6);
    }

    @Test
    @Transactional
    public void getWithWhiteboardTest() throws Exception {
        Gson gson = new GsonBuilder().create();

        final String jsonStr1 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 10001,
                "room", 1, true, false));
        final String jsonStr2 = gson.toJson(new CreateRoomRequest("addRoomTest18829", 100856,
                "room22", 7, false, true));
        final String jsonStr3 = gson.toJson(new CreateRoomRequest("addRoomTest18829", 10001,
                "room22", 12, true, true));
        final String jsonStr4 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 100856,
                "room4", 12, false, false));

        roomController.addNewRoom(jsonStr1, req, res);
        roomController.addNewRoom(jsonStr2, req, res);
        roomController.addNewRoom(jsonStr3, req, res);
        roomController.addNewRoom(jsonStr4, req, res);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/getRoomWhiteboard")
                .cookie(ses)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        RoomsResponse roomsResponse = gson.fromJson(str, RoomsResponse.class);

        assertTrue(roomsResponse.getRooms().size() >= 2);

        assertTrue(roomsResponse.getRooms().get(0).isHasWhiteboard());
        assertTrue(roomsResponse.getRooms().get(1).isHasWhiteboard());

    }

    @Test
    @Transactional
    public void getStaffOnlyTest() throws Exception {
        Gson gson = new GsonBuilder().create();

        final String jsonStr1 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 10001,
                "room", 1, true, false));
        final String jsonStr2 = gson.toJson(new CreateRoomRequest("addRoomTest18829", 100856,
                "room22", 7, false, true));
        final String jsonStr3 = gson.toJson(new CreateRoomRequest("addRoomTest18829", 10001,
                "room22", 12, true, true));
        final String jsonStr4 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 100856,
                "room4", 12, false, false));

        roomController.addNewRoom(jsonStr1, req, res);
        roomController.addNewRoom(jsonStr2, req, res);
        roomController.addNewRoom(jsonStr3, req, res);
        roomController.addNewRoom(jsonStr4, req, res);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/getRoomsStaffOnly")
                .cookie(ses)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        RoomsResponse roomsResponse = gson.fromJson(str, RoomsResponse.class);

        assertTrue(roomsResponse.getRooms().size() >= 2);

        assertTrue(roomsResponse.getRooms().get(0).isStaffOnly());
        assertTrue(roomsResponse.getRooms().get(1).isStaffOnly());
    }

    @Test
    @Transactional
    public void getNotStaffOnlyTest() throws Exception {
        Gson gson = new GsonBuilder().create();

        final String jsonStr1 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 10001,
                "room", 1, true, false));
        final String jsonStr2 = gson.toJson(new CreateRoomRequest("addRoomTest18829", 100856,
                "room22", 7, false, true));
        final String jsonStr3 = gson.toJson(new CreateRoomRequest("addRoomTest18829", 10001,
                "room22", 12, true, true));
        final String jsonStr4 = gson.toJson(new CreateRoomRequest("addRoomTest1889", 100856,
                "room4", 12, false, false));

        roomController.addNewRoom(jsonStr1, req, res);
        roomController.addNewRoom(jsonStr2, req, res);
        roomController.addNewRoom(jsonStr3, req, res);
        roomController.addNewRoom(jsonStr4, req, res);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/getRoomsNotStaffOnly")
                .cookie(ses)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        RoomsResponse roomsResponse = gson.fromJson(str, RoomsResponse.class);

        assertTrue(roomsResponse.getRooms().size() >= 4);
    }

    @Test
    @Transactional
    public void getAvailableRoomsTest() throws Exception {
        //        Gson gson = new GsonBuilder().create();
        //
        //        final String jsonStr1 =
        //                  gson.toJson(new CreateRoomRequest("addRoomTest1889", 10001,
        //                "room", 1, true, false));
        //        final String jsonStr2 =
        //              gson.toJson(new CreateRoomRequest("addRoomTest18829", 100856,
        //                "room22", 7, false, true));
        //        final String jsonStr3 =
        //              gson.toJson(new CreateRoomRequest("addRoomTest18829", 10001,
        //                "room222", 12, true, true));
        //        final String jsonStr4 =
        //              gson.toJson(new CreateRoomRequest("addRoomTest1889", 100856,
        //                "room4", 12, false, false));
        //
        //        roomController.addNewRoom(jsonStr1);
        //        roomController.addNewRoom(jsonStr2);
        //        roomController.addNewRoom(jsonStr3);
        //        roomController.addNewRoom(jsonStr4);
        //
        //        Calendar d2 = Calendar.getInstance();
        //        d2.set(2030, 3, 28);
        //        CreateRoomReservationRequest
        //              reservationRequest3 = new CreateRoomReservationRequest(d2,
        //                "15:00-17:00",
        //                "test9ss74812@tudelft.nl",
        //                "remove",
        //                "123",
        //                "room222");
        //
        //        roomReservationController.reserveRoom(gson.toJson(reservationRequest3));
        //        MvcResult result = mvc.perform(MockMvcRequestBuilders
        //                .post("/getAvailableRooms")
        //                .content(gson.toJson(new GetAvailableRoomsRequest(d2, "15:00-17:00", 12)))
        //                .contentType(MediaType.APPLICATION_JSON)
        //                .accept(MediaType.APPLICATION_JSON))
        //                .andDo(print())
        //                .andExpect(status().isOk())
        //                .andReturn();
        //        String str = result.getResponse().getContentAsString();
        //        RoomsResponse roomsResponse = gson.fromJson(str, RoomsResponse.class);
        //        assertTrue(roomsResponse.getRooms().size() >= 3);
        //        assertNotEquals(roomsResponse.getRooms().get(0).getName(), "room222");
        //        assertNotEquals(roomsResponse.getRooms().get(1).getName(), "room222");
        //        assertNotEquals(roomsResponse.getRooms().get(2).getName(), "room222");
    }

}
