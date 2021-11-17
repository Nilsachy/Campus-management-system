package nl.tudelft.oopp.shared.responses.content;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersResponse {
    /**
     * A list of all the users contained in the response.
     */
    @NonNull
    List<UserResponse> users = new ArrayList<>();
}
