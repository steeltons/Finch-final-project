package org.jenjetsu.com.finch;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.jenjetsu.com.finch.library.Finch;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class HelloController implements Initializable{

    private final URI BUTTON_PRESSED_IMAGE_URI = Paths.get("src/main/resources/image/arrow.png").toUri();
    private final URI BUTTON_RELEASED_IMAGE_URI = Paths.get("src/main/resources/image/arrow_tint.png").toUri();
    private final int MAX_FINCH_VISION_DISTANCE_SM = 250;
    private AtomicReference<KeyCode> CURRENT_PRESSED_KEY;
    private Timer finchUpdater = new Timer();
    @FXML private Slider redRGBSlider;
    @FXML private Slider greenRGBSlider;
    @FXML private Slider blueRGBSlider;
    @FXML private Pane mainPane;
    @FXML private Canvas finchScreen;
    @FXML private Button upButton;
    @FXML private Button downButton;
    @FXML private Button leftButton;
    @FXML private Button rightButton;

    private final Finch finch;

    private int red;
    private int green;
    private int blue;

    public HelloController() {
        finch = new Finch("A");
        CURRENT_PRESSED_KEY = new AtomicReference<>(null);
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
        GraphicsContext gc = finchScreen.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, finchScreen.getWidth(), finchScreen.getHeight());
        changeFinchColor();
        upButton.setPadding(Insets.EMPTY);
        downButton.setPadding(Insets.EMPTY);
        leftButton.setPadding(Insets.EMPTY);
        rightButton.setPadding(Insets.EMPTY);
        finchUpdater.schedule(new TimerTask() {
            @Override
            public void run() {
                changeAllButtonsImage();
                changeDistance(finch.getDistance());
                updateFinchScreen(finch.getDistance());
                changeLight(finch.getLight("L"));
                rotateCompassArrow(finch.getCompass());
                if(CURRENT_PRESSED_KEY.get() != null) {
                    String keyCode = CURRENT_PRESSED_KEY.get().getName();
                    changeArrowImage(CURRENT_PRESSED_KEY.get().getName(), false);
                    moveFinch(keyCode, false);
                } else {
                    moveFinch("", true);
                }
            }
        }, 1000, 30);
    }

    public void close() {
        System.out.println("stopped");
        finchUpdater.cancel();
        finchUpdater.purge();
        finch.setBeak(0, 0, 0);
        finch.stop();
        finch.disconnect();
    }

    private synchronized Finch getFinch() {
        return finch;
    }

    // Управление Finchом
    @FXML
    private void handleKeyPressed(KeyEvent ke) {
        if(ke.getCode() == KeyCode.W || ke.getCode() == KeyCode.S || ke.getCode() == KeyCode.D || ke.getCode() == KeyCode.A) {
            CURRENT_PRESSED_KEY.set(ke.getCode());
        }
    }

    @FXML
    private void releaseKeyPress(KeyEvent ke) {
        if(ke.getCode() == KeyCode.W || ke.getCode() == KeyCode.S || ke.getCode() == KeyCode.D || ke.getCode() == KeyCode.A) {
            CURRENT_PRESSED_KEY.set(null);
        }
    }

    @FXML
    private void releaseArrowButtonPress(MouseEvent me) {
        String key = Converter.convertButtonIdToKey(((Button) me.getSource()).getId());
        moveFinch(key, true);
        changeArrowImage(key, true);
        updateFinchScreen(finch.getDistance());
    }

    @FXML
    private void handleArrowButtonPress(MouseEvent me) {
        String key = Converter.convertButtonIdToKey(((Button) me.getSource()).getId());
        moveFinch(key, false);
        changeArrowImage(key, false);
        updateFinchScreen(finch.getDistance());
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

    private void changeDistance(int distance) {
        Text distanceText = (Text) mainPane.getScene().lookup("#distanceText");
        distanceText.setText(distance + " sm");
    }

    @FXML
    private void showMessageOnDisplay() {
        TextField field = (TextField) mainPane.getScene().lookup("#messageField");
        String message = field.getText();
        for(Character ch : message.toCharArray()) {
            int[] display = RussionLetterConverter.convertToDisplay(ch);
            finch.setDisplay(display);
            finch.pause(0.8);
            finch.setDisplay(RussionLetterConverter.STANDART_DISPLAY);
        }
    }

    private void changeLight(int light) {
        Text lightText = (Text) mainPane.getScene().lookup("#lightText");
        lightText.setText(String.valueOf(light));
    }

    private void rotateCompassArrow(int degAngle) {
        ImageView compassArrow = (ImageView) mainPane.getScene().lookup("#compassArrow");
        compassArrow.setRotate(degAngle);
    }

    private void changeAllButtonsImage() {
        Set<Node> buttons = mainPane.lookupAll(".moveButton");
        for(Node node : buttons) {
            ImageView view = (ImageView) ((Button) node).getChildrenUnmodifiable().get(0);
            if(view.getImage().getUrl().toString().endsWith("arrow_tint.png")) {
                view.setImage(new Image(BUTTON_PRESSED_IMAGE_URI.toString()));
            }
        }
    }

    private void updateFinchScreen(int distance) {
        GraphicsContext gc = finchScreen.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, finchScreen.getWidth(), finchScreen.getHeight());
        if ((distance <= MAX_FINCH_VISION_DISTANCE_SM)) {
            gc.setStroke(Color.RED);
            double cx = finchScreen.getWidth() / 2.0;
            double cy = finchScreen.getHeight() / 2.0;
            double x1 = (cx - 50) / distance;
            double y1 = (cy - 50) / distance;
            double x2 = (cx + 50) / distance;
            double y2 = (cy + 50) / distance;
            gc.beginPath();
            gc.moveTo(x1, y1);
            gc.lineTo(x2, y1);
            gc.lineTo(x2, y2);
            gc.lineTo(x1, y2);
            gc.lineTo(x1, y1);
            gc.stroke();
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