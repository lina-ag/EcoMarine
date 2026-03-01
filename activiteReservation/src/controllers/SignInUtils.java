package controllers;

import javafx.stage.Stage;

public class SignInUtils {

    private static Stage currentStage;

    public static void setCurrentStage(Stage stage) {
        currentStage = stage;
    }

    public static Stage getCurrentStage() {
        return currentStage;
    }
}