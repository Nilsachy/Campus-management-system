package nl.tudelft.oopp.cars.controllertests;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.Cookie;

import nl.tudelft.oopp.shared.requests.read.GetUserValidationRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class TestSession {
    private static TestSession instance;

    private static MockMvc mvc;

    private Cookie sessionCookie;

    /**
     * Get the id. If it doesn't exist, make it.
     * @return Cookie of the session
     */
    protected Cookie getSessionCookie() {
        return sessionCookie;
    }

    /**
     * Make a session to test with.
     * @param mockMvc can't autowire in here, so we pass it through
     * @return an instance of a TestSession, this is so we use the same TestSession for all tests.
     *      No need to make more than necessary.
     * @throws Exception an exception cam be thrown by mcv.perform
     */
    public static TestSession getInstance(MockMvc mockMvc) throws Exception {
        if (instance == null) {
            mvc = mockMvc;
            instance = new TestSession();
        }
        return instance;
    }

    private TestSession() throws Exception {
        Gson gson = new GsonBuilder().create();
        System.out.println("Mcv: " + mvc);
        sessionCookie = mvc.perform(MockMvcRequestBuilders.post("/validateLogin")
                .content(gson.toJson(new
                        GetUserValidationRequest("admin@tudelft.nl", "admin".hashCode())))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getCookie("JSESSION");
    }
}
