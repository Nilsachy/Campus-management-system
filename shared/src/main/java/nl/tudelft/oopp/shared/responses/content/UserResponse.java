package nl.tudelft.oopp.shared.responses.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    /**
     * The user's full e-mail address.
     */
    @NonNull
    String email;

    /**
     * The user's role within the system, can be "student", "staff" or "admin".
     */
    @NonNull
    String role;

    /**
     * The hash of the user's password.
     */
    int passwordHash;
}
