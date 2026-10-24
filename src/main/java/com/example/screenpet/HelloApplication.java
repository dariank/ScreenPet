package com.example.screenpet;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        // 1. Картинка
        ImageView imageView = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream("/sprites/idle.png"));
            imageView.setImage(img);
            imageView.setFitWidth(180);
            imageView.setFitHeight(180);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("Картинка для меню не найдена");
        }

        // 2. Кнопка
        Button startButton = new Button("Выпустить погулять");
        startButton.setStyle(
                "-fx-background-color: #ff69b4; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 20; " +
                        "-fx-padding: 10 20 10 20; " +
                        "-fx-cursor: hand;"
        );

        startButton.setOnAction(e -> {
            // Запускаем питомца (без аргументов)
            PetWindow pet = new PetWindow();
            pet.start();
            stage.close();
        });

        // 3. Сборка меню
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #1e1e1e;");

        if (imageView.getImage() != null) {
            root.getChildren().add(imageView);
        }
        root.getChildren().add(startButton);

        // 4. Окно
        Scene scene = new Scene(root, 380, 380);
        stage.setTitle("Меню Screen Pet");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}