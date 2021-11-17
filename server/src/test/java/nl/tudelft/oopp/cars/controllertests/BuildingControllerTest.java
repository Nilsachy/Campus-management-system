package nl.tudelft.oopp.cars.controllertests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import javax.servlet.http.Cookie;

import nl.tudelft.oopp.cars.controllers.BuildingController;

import nl.tudelft.oopp.cars.entities.Building;
import nl.tudelft.oopp.shared.requests.create.AddBuildingRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveBuildingRequest;
import nl.tudelft.oopp.shared.requests.read.GetByAddressRequest;
import nl.tudelft.oopp.shared.requests.read.GetByFacultiesRequest;
import nl.tudelft.oopp.shared.requests.read.GetByFacultyRequest;
import nl.tudelft.oopp.shared.requests.read.GetByNameRequest;
import nl.tudelft.oopp.shared.responses.content.BuildingResponse;
import nl.tudelft.oopp.shared.responses.content.BuildingsResponse;
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
public class BuildingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    BuildingController controller;

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
    public void addBuildingTest() throws Exception {

        Gson gson = new GsonBuilder().create();

        AddBuildingRequest request = new
                AddBuildingRequest(500, "Test Building", "address", "test");

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/addBuilding")
                .cookie(ses)
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        assertEquals("Building saved.", str);

        AddBuildingRequest request2 = new
                AddBuildingRequest(500, "Test Building", "address", "test2");

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders.post("/addBuilding")
                .cookie(ses)
                .content(gson.toJson(request2))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result.getResponse().getContentAsString();
        assertEquals("Building saved.", str2);

        controller.removeBuilding(gson.toJson(new RemoveBuildingRequest(500)), req, res);
    }

    @Test
    public void removeTest() throws Exception {

        Gson gson = new GsonBuilder().create();

        AddBuildingRequest request = new
                AddBuildingRequest(500, "Test Building", "address", "test");

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/addBuilding")
                .cookie(ses)
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        assertEquals("Building saved.", str);

        RemoveBuildingRequest request2 = new RemoveBuildingRequest(500);

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders.post("/removeBuilding")
                .cookie(ses)
                .content(gson.toJson(request2))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result2.getResponse().getContentAsString();
        assertEquals("Building deleted.", str2);

        MvcResult result3 = mvc.perform(MockMvcRequestBuilders.post("/removeBuilding")
                .cookie(ses)
                .content(gson.toJson(request2))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str3 = result3.getResponse().getContentAsString();
        assertEquals("Building not deleted from database.", str3);

    }

    @Test
    public void getAllBuildingsTest() throws Exception {

        Gson gson = new GsonBuilder().create();

        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/getAllBuildings")
                .cookie(ses)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        BuildingsResponse buildings = gson.fromJson(str, BuildingsResponse.class);
        assertTrue(buildings.getBuildings().size() > 0);

    }

    @Test
    public void getByAddressTest() throws Exception {

        Gson gson = new GsonBuilder().create();

        AddBuildingRequest request = new
                AddBuildingRequest(500, "Test Building", "address", "test");

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/addBuilding")
                .cookie(ses)
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        assertEquals("Building saved.", str);

        GetByAddressRequest request2 = new GetByAddressRequest("address");

        MvcResult result2 = mvc.perform(MockMvcRequestBuilders.post("/getBuildingByAddress")
                .cookie(ses)
                .content(gson.toJson(request2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result2.getResponse().getContentAsString();
        Building building = gson.fromJson(str2, Building.class);

        assertEquals("Test Building", building.getName());

        RemoveBuildingRequest request3 = new RemoveBuildingRequest(building.getId());
        controller.removeBuilding(gson.toJson(request3), req, res);
    }

    @Test
    public void getByNameTest() throws Exception {

        Gson gson = new GsonBuilder().create();

        AddBuildingRequest request = new
                AddBuildingRequest(500, "Test Building", "address", "test");
        controller.addBuilding(gson.toJson(request), req, res);

        GetByNameRequest request2 = new GetByNameRequest("Test Building");
        MvcResult result2 = mvc.perform(MockMvcRequestBuilders.post("/getBuildingByName")
                .cookie(ses)
                .content(gson.toJson(request2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result2.getResponse().getContentAsString();

        BuildingsResponse buildings = gson.fromJson(str2, BuildingsResponse.class);

        BuildingResponse building = buildings.getBuildings().get(0);

        assertEquals("address", building.getAddress());

        RemoveBuildingRequest request3 = new RemoveBuildingRequest(building.getId());
        controller.removeBuilding(gson.toJson(request3), req, res);
    }

    @Test
    public void getBuildingByFacultyTest() throws Exception {

        Gson gson = new GsonBuilder().create();

        AddBuildingRequest request = new
                AddBuildingRequest(500, "Test Building", "address", "test");
        controller.addBuilding(gson.toJson(request), req, res);

        GetByFacultyRequest request2 = new GetByFacultyRequest("test");
        MvcResult result2 = mvc.perform(MockMvcRequestBuilders.post("/getBuildingsByFaculty")
                .cookie(ses)
                .content(gson.toJson(request2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str2 = result2.getResponse().getContentAsString();
        BuildingsResponse buildings = gson.fromJson(str2, BuildingsResponse.class);
        assertTrue(buildings.getBuildings().size() > 0);

        RemoveBuildingRequest request3 = new RemoveBuildingRequest(500);
        controller.removeBuilding(gson.toJson(request3), req, res);
    }

    @Test
    public void filterTest() throws Exception {
        Gson gson = new GsonBuilder().create();

        ArrayList<String> faculties = new ArrayList<>();
        faculties.add("General");
        faculties.add("asd");

        GetByFacultiesRequest request = new GetByFacultiesRequest(faculties);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/getBuildingsByFilters")
                .cookie(ses)
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        BuildingsResponse buildings = gson.fromJson(str, BuildingsResponse.class);
        //  assertEquals(1, buildings.size());
    }

    //TODO: implement the getBuildingImageTest?
    @Test
    public void getBuildingImageTest() {
        Gson gson = new GsonBuilder().create();

        assertTrue(true);
    }
}
