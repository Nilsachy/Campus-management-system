package nl.tudelft.oopp.shared.util;

public class StringUtilities {

    /**
     * Checks if a given email is valid, supposedly 99.9% correct.
     * @param email The email address to check
     * @return Whether it was valid or not
     */
    public static boolean isValidEmail(String email) {
        return EmailMatcher.getInstance().isValid(email);
    }

}
