package nl.tudelft.oopp.cars.controllers.logictests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.oopp.cars.logic.ErrorMessageLogic;
import org.junit.jupiter.api.Test;

public class ErrorMessageLogicTest {

    String invalid1 = null;
    String invalid2 = "";
    String valid1 = "Hello! There!";
    String valid2 = "anythingreally@#";
    String whenInvalidPassword = "Please enter a non-empty password";
    String whenInvalidEvent = "Please select an event from the list";
    String whenValid = "";

    @Test
    public void accountSettingsTest() {
        assertEquals(ErrorMessageLogic.accountSettingsMessage(invalid1),
                whenInvalidPassword);
        assertEquals(ErrorMessageLogic.accountSettingsMessage(invalid2),
                whenInvalidPassword);
        assertEquals(ErrorMessageLogic.accountSettingsMessage(valid1),
                whenValid);
        assertEquals(ErrorMessageLogic.accountSettingsMessage(valid2),
                whenValid);
    }

    @Test
    public void eventsErrorNull() {
        assertEquals(ErrorMessageLogic.accountEventsMessage(invalid1),
                whenInvalidEvent);
        assertEquals(ErrorMessageLogic.accountEventsMessage(invalid2),
                whenInvalidEvent);
        assertEquals(ErrorMessageLogic.accountEventsMessage(valid1),
                whenValid);
        assertEquals(ErrorMessageLogic.accountEventsMessage(valid2),
                whenValid);
    }

    @Test
    public void fromToErrorMessageTest() {

        String fromDayStr = "03032019";
        String toDayStr = "07032019";
        String fromTimeStr = "11:11";
        String toTimeStr = "11:12";
        String errorStr1 = "";
        String errorStr2 = null;

        String response1 = ErrorMessageLogic
                .bikeFromToMessage(fromDayStr, toDayStr, fromTimeStr,toTimeStr);
        assertEquals(response1, "");

        String response2 = ErrorMessageLogic
                .bikeFromToMessage(errorStr1, toDayStr, fromTimeStr,toTimeStr);
        assertEquals(response2, "Please enter a date to rent from");

        String response3 = ErrorMessageLogic
                .bikeFromToMessage(fromDayStr, errorStr2, fromTimeStr,toTimeStr);
        assertEquals(response3,  "Please enter a date to rent to");

        String response4 = ErrorMessageLogic
                .bikeFromToMessage(fromDayStr, toDayStr, errorStr1,toTimeStr);
        assertEquals(response4, "Please enter a time to rent from");

        String response5 = ErrorMessageLogic
                .bikeFromToMessage(fromDayStr, toDayStr, fromTimeStr,errorStr2);
        assertEquals(response5, "Please enter a time to rent to");

        String response6 = ErrorMessageLogic
                .bikeFromToMessage(null, toDayStr, fromTimeStr,null);
        assertEquals(response6, "Please enter a time to rent to");

    }

    @Test
    public void rentErrorMessageTest() {
        String invalidDepartBikeStorage = "";
        String validDepartBikeStorage = "EEMCS";
        String invalidReturnBikeStorage = "";
        String validReturnBikeStorage = "EEMCS";

        String response1 = ErrorMessageLogic.bikeRentMessage(
                invalidDepartBikeStorage, invalidReturnBikeStorage);
        assertEquals(response1,"Please select a depart building");

        String response2 = ErrorMessageLogic.bikeRentMessage(
                invalidDepartBikeStorage, validReturnBikeStorage);
        assertEquals(response2,"Please select a depart building");

        String response3 = ErrorMessageLogic.bikeRentMessage(
                validDepartBikeStorage, invalidReturnBikeStorage);
        assertEquals(response3,"Please select a return building");

        String response4 = ErrorMessageLogic.bikeRentMessage(
                validDepartBikeStorage, validReturnBikeStorage);
        assertEquals(response4,"");
    }
}
