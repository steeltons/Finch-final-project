package org.jenjetsu.com.finch;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.jenjetsu.com.finch.library.Finch;

public class HelloController {

    @FXML private Slider redRGBSlider;
    @FXML private Slider greenRGBSlider;
    @FXML private Slider blueRGBSlider;
    private final Finch finch;
    private int red;
    private int green;
    private int blue;

    public HelloController() {
        finch = new Finch("A");
        red = 100;
        green = 100;
        blue = 100;
    }

    public void initialize() {
        redRGBSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals(oldValue)) {
                red = newValue.intValue();
                changeFinchColor();
            }
        });
        greenRGBSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals(oldValue)) {
                green = newValue.intValue();
                changeFinchColor();
            }
        });
        blueRGBSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals(oldValue)){
                blue = newValue.intValue();
                changeFinchColor();
            }
        });
        changeFinchColor();
    }

    private void changeFinchColor() {
        finch.setBeak(red, green, blue);
    }

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private void handleKeyPressed(KeyEvent ke) {
        KeyCode key = ke.getCode();
        if(key == KeyCode.W) {
            finch.setMotors(100, 100);
        } else if(key == KeyCode.S) {
            finch.setMotors(-100, -100);
        } else if (key == KeyCode.A) {
            finch.setMotors(-50, 50);
        } else if (key == KeyCode.D) {
            finch.setMotors(50, -50);
        } else {
            System.out.println("Not supported");
        }
    }

    @FXML
    private void stopFinch(KeyEvent ke) {
        finch.setMotors(0, 0);
    }
}