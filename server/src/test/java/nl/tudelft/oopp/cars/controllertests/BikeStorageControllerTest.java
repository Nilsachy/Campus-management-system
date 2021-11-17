package nl.tudelft.oopp.cars.controllertests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import javax.servlet.http.Cookie;

import nl.tudelft.oopp.cars.controllers.BikeStorageController;
import nl.tudelft.oopp.cars.entities.BikeStorage;
import nl.tudelft.oopp.shared.requests.create.AddBikeStorageRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveBikeStorageByFacultyRequest;
import nl.tudelft.oopp.shared.requests.read.GetByFacultyRequest;
import nl.tudelft.oopp.shared.responses.content.AvailableBikeStoragesResponse;
import nl.tudelft.oopp.shared.responses.content.BikeStorageResponse;
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
public class BikeStorageControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    BikeStorageController bikeStorageController;

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
    public void addBikeStorageTest() throws Exception {
        Gson gson = new GsonBuilder().create();

        AddBikeStorageRequest addBikeStorageRequest =
                new AddBikeStorageRequest("faculty7865", 8887);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/addBikeStorage")
                .cookie(ses)
                .content(gson.toJson(addBikeStorageRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        assertEquals("Bike storage saved.", str);
        assertNotNull(bikeStorageController
                .getBikeStorageForFaculty(gson.toJson(new GetByFacultyRequest("faculty7865")),
                        req, res));

    }

    @Test
    @Transactional
    public void removeBikeStorageTest() throws Exception {
        Gson gson = new GsonBuilder().create();

        AddBikeStorageRequest addBikeStorageRequest = new AddBikeStorageRequest("faculty59", 8887);
        bikeStorageController.addBikeStorage(gson.toJson(addBikeStorageRequest), req, res);

        GetByFacultyRequest getByFacultyRequest = new GetByFacultyRequest("faculty59");
        String json = bikeStorageController
                .getBikeStorageForFaculty(gson.toJson(getByFacultyRequest), req, res);
        BikeStorage bikeStorage = gson.fromJson(json, BikeStorage.class);
        assertEquals(bikeStorage.getMaxAvailable(), 8887);

        RemoveBikeStorageByFacultyRequest removeBikeStorageByFacultyRequest =
                new RemoveBikeStorageByFacultyRequest("faculty59");

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders.post("/removeBikeStorage")
                .cookie(ses)
                .content(gson.toJson(removeBikeStorageByFacultyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result2.getResponse().getContentAsString();
        assertEquals("Bike Storage removed", str2);

        String json2 = bikeStorageController
                .getBikeStorageForFaculty(gson.toJson(getByFacultyRequest), req, res);
        BikeStorage bikeStorage2 = gson.fromJson(json2, BikeStorage.class);

        assertNull(bikeStorage2);
    }

    @Test
    @Transactional
    public void getAllBikeStoragesTest() throws Exception {
        Gson gson = new GsonBuilder().create();
        AddBikeStorageRequest addBikeStorageRequest = new AddBikeStorageRequest("faculty59", 8887);
        bikeStorageController.addBikeStorage(gson.toJson(addBikeStorageRequest), req, res);
        AddBikeStorageRequest addBikeStorageRequest1 = new AddBikeStorageRequest("faculty51", 8887);
        bikeStorageController.addBikeStorage(gson.toJson(addBikeStorageRequest1), req, res);
        AddBikeStorageRequest addBikeStorageRequest2 = new AddBikeStorageRequest("faculty52", 8887);
        bikeStorageController.addBikeStorage(gson.toJson(addBikeStorageRequest2), req, res);
        AddBikeStorageRequest addBikeStorageRequest3 = new AddBikeStorageRequest("faculty53", 8887);
        bikeStorageController.addBikeStorage(gson.toJson(addBikeStorageRequest3), req, res);
        AddBikeStorageRequest addBikeStorageRequest4 = new AddBikeStorageRequest("faculty54", 8887);
        bikeStorageController.addBikeStorage(gson.toJson(addBikeStorageRequest4), req, res);

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders.get("/getAllBikeStorages")
                .cookie(ses)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result2.getResponse().getContentAsString();
        AvailableBikeStoragesResponse b =
                gson.fromJson(str2, AvailableBikeStoragesResponse.class);
        List<BikeStorageResponse> storages = b.getBikeStorages();
        assertTrue(storages.size() >= 5);
    }

    @Test
    @Transactional
    public void getBikeStorageForFacultyTest() throws Exception {
        Gson gson = new GsonBuilder().create();

        AddBikeStorageRequest addBikeStorageRequest1 =
                new AddBikeStorageRequest("faculty592", 1887);
        bikeStorageController.addBikeStorage(gson.toJson(addBikeStorageRequest1), req, res);
        GetByFacultyRequest getByFacultyRequest = new GetByFacultyRequest("faculty592");

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders.post("/getBikeStorageForFaculty")
                .cookie(ses)
                .content(gson.toJson(getByFacultyRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result2.getResponse().getContentAsString();
        BikeStorage storage = gson.fromJson(str2, BikeStorage.class);

        assertEquals(storage.getMaxAvailable(), 1887);
        bikeStorageController.removeBikeStorage(gson
                .toJson(new RemoveBikeStorageByFacultyRequest("faculty592")), req, res);

        MvcResult result3 = mvc.perform(MockMvcRequestBuilders.post("/getBikeStorageForFaculty")
                .cookie(ses)
                .content(gson.toJson(getByFacultyRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str3 = result3.getResponse().getContentAsString();
        BikeStorage storage2 = gson.fromJson(str3, BikeStorage.class);
        assertNull(storage2);
    }
}
