package org.jenjetsu.com.finch;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.jenjetsu.com.finch.library.Finch;

import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    private final URI BUTTON_PRESSED_IMAGE_URI = Paths.get("src/main/resources/image/arrow.jpg").toUri();
    private final URI BUTTON_RELEASED_IMAGE_URI = Paths.get("src/main/resources/image/arrow_tint.jpg").toUri();
    @FXML private Slider redRGBSlider;
    @FXML private Slider greenRGBSlider;
    @FXML private Slider blueRGBSlider;
    @FXML private Pane mainPane;
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

    public void initialize(URL url, ResourceBundle rb) {
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

    // Управление Finchом
    @FXML
    private void handleKeyPressed(KeyEvent ke) {
        moveFinch(ke.getCode().getName(), false);
        changeArrowImage(ke.getCode().getName(), false);
    }

    @FXML
    private void releaseKeyPress(KeyEvent ke) {
        moveFinch(ke.getCode().getName(), true);
        changeArrowImage(ke.getCode().getName(), true);
    }

    @FXML
    private void releaseArrowButtonPress(MouseEvent me) {
        String key = Converter.convertButtonIdToKey(((Button) me.getSource()).getId());
        moveFinch(key, true);
        changeArrowImage(key, true);
    }

    @FXML
    private void handleArrowButtonPress(MouseEvent me) {
        String key = Converter.convertButtonIdToKey(((Button) me.getSource()).getId());
        moveFinch(key, false);
        changeArrowImage(key, false);
    }

    private void changeFinchColor() {
        finch.setBeak(red, green, blue);
    }

    // Подуправление Finchом
    private void moveFinch(String key, boolean isStop) {
        if(!isStop) {
            if(key.equals("W")) {
                finch.setMotors(100, 100);
            } else if (key.equals("S")) {
                finch.setMotors(-100, -100);
            } else if (key.equals("A")) {
                finch.setMotors(-50, 50);
            } else if (key.equals("D")) {
                finch.setMotors(50, -50);
            }
        } else {
            finch.setMotors(0, 0);
        }
    }

    private void changeArrowImage(String key, boolean isStop) {
        URI imageUri = isStop ? BUTTON_PRESSED_IMAGE_URI : BUTTON_RELEASED_IMAGE_URI;
        Button pressedButton = (Button) mainPane.getScene().lookup("#"+Converter.convertKeyToButtonId(key));
        ImageView img = (ImageView) pressedButton.getChildrenUnmodifiable().get(0);
        if(!img.getImage().getUrl().equals(imageUri)) {
            img.setImage(new Image(imageUri.toString()));
        }
    }
}

// Конвертеры для кнопок
class Converter{

    static String convertKeyToButtonId(String key) {
        String retVal = "";
        switch (key) {
            case "W" : retVal = "upButton";
                break;
            case "S" : retVal = "downButton";
                break;
            case "D" : retVal = "rightButton";
                break;
            case "A" : retVal = "leftButton";
                break;
        };
        return retVal;
    }

    static String convertButtonIdToKey(String buttonId) {
        String retVal = "";
        switch (buttonId) {
            case "upButton"    : retVal = "W";
                break;
            case "downButton"  : retVal = "S";
                break;
            case "leftButton" : retVal = "D";
                break;
            case "rightButton"  : retVal = "A";
                break;
        };
        return retVal;
    }
}