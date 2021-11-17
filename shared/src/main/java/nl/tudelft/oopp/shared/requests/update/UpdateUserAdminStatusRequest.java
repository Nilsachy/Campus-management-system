package nl.tudelft.oopp.shared.requests.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserAdminStatusRequest {
    /**
     * The email of the user that is to be turned into an admin.
     */
    private @NonNull String email;
}
