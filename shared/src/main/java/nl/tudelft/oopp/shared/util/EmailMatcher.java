package nl.tudelft.oopp.shared.util;

import java.util.regex.Pattern;

public class EmailMatcher {

    private static EmailMatcher instance = null;

    private Pattern pattern;

    /**
     * Constructs the regex pattern matcher to check email addresses with.
     * Pattern string is from https://emailregex.com/ and is supposedly very accurate
     */
    private EmailMatcher() {
        pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+"
                + "(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\"
                + "x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f"
                + "]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?"
                + ":[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-"
                + "z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]"
                + "?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-"
                + "9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x"
                + "0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x"
                + "09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    }

    /**
     * Gets a singleton instance of the EmailMatcher.
     * @return the instance of the EmailMatcher
     */
    public static EmailMatcher getInstance() {
        if (instance == null) {
            instance = new EmailMatcher();
        }
        return instance;
    }

    /**
     * Checks if a given email address is valid.
     * @param email The address to check
     * @return Whether it's correct or not
     */
    public boolean isValid(String email) {
        return pattern.matcher(email).matches();
    }
}
