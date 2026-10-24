package com.example.screenpet;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PetWindow petWindow = new PetWindow();
            petWindow.start();
        });
    }
}