package com.example.screenpet;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HelloController {

    @FXML
    private Button startButton;

    @FXML
    protected void handleStartButton() {
        PetWindow pet = new PetWindow();
        pet.start();
        Stage stage = (Stage) startButton.getScene().getWindow();
        stage.close();
    }

    public void onReleasePet() {
    }
}