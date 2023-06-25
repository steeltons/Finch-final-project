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

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
public class HelloController implements Initializable{

    private final URI BUTTON_PRESSED_IMAGE_URI = Paths.get("src/main/resources/image/arrow.png").toUri();
    private final URI BUTTON_RELEASED_IMAGE_URI = Paths.get("src/main/resources/image/arrow_tint.png").toUri();
    private final int MAX_FINCH_VISION_DISTANCE_SM = 200;
    private double MAX_SPEED = 30;
    private Timer finchUpdater = new Timer();
    private ConcurrentLinkedDeque<Character> message;
    private boolean isAutopilotActive;
    private int[] color = new int[3];
    private int[] predColor = new int[]{-1, -1, -1};
    private boolean isBeepSignal;

    @FXML private Slider redRGBSlider;
    @FXML private Slider greenRGBSlider;
    @FXML private Slider blueRGBSlider;
    @FXML private Slider speedSlider;
    @FXML private Pane mainPane;
    @FXML private Canvas finchScreen;

    private ConcurrentLinkedQueue<KeyCode> keys;

    private final Finch finch;
    private Timer finchAutopilotTimer = new Timer();
    private long letterTimer;

    public HelloController() {
        finch = new Finch("A");
        keys = new ConcurrentLinkedQueue<>();
        color[0] = 0;
        color[1] = 0;
        color[2] = 0;
        letterTimer = 0l;
        message = new ConcurrentLinkedDeque<>();
    }

    public void initialize(URL url, ResourceBundle rb) {
        redRGBSlider.valueProperty().addListener((observable, oldValue, newValue) -> color[0] = newValue.intValue());
        greenRGBSlider.valueProperty().addListener((observable, oldValue, newValue) -> color[1] = newValue.intValue());
        blueRGBSlider.valueProperty().addListener((observable, oldValue, newValue) -> color[2] = newValue.intValue());
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            MAX_SPEED = newValue.doubleValue();
            Text text = (Text) mainPane.getScene().lookup("#speedText");
            text.setText(String.format("SPEED: %d", (int) MAX_SPEED));
        });
        GraphicsContext gc = finchScreen.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, finchScreen.getWidth(), finchScreen.getHeight());
        mainPane.lookupAll(".moveButton").forEach((node) -> ((Button) node).setPadding(Insets.EMPTY));
        finchUpdater.schedule(createCustomControlTask(), 1000, 30);
    }

    public void close() {
        System.out.println("stopped");
        FinchMidiUtil.stopCurrentSong();
        if(isAutopilotActive) {
            finchAutopilotTimer.cancel();
            finchAutopilotTimer.purge();
        } else  {
            finchUpdater.cancel();
            finchUpdater.purge();
        }
        finch.setBeak(0, 0, 0);
        finch.stop();
        finch.disconnect();
    }

    // Управление Finchом
    @FXML
    private void handleKeyPressed(KeyEvent ke) {
        if (ke.getCode() == KeyCode.W || ke.getCode() == KeyCode.S || ke.getCode() == KeyCode.D || ke.getCode() == KeyCode.A) {
            if(!keys.contains(ke.getCode())) {
                keys.add(ke.getCode());
            }
        } else if (ke.getCode() == KeyCode.E || ke.getCode() == KeyCode.R) {
            if (FinchMidiUtil.isSongPlaying()) {
                FinchMidiUtil.stopCurrentSong();
            } else {
                File f;
                if (ke.getCode() == KeyCode.E) {
                    f = new File("src/main/resources/midi_songs/Megalovania.mid");
                } else {
                    f = new File("src/main/resources/midi_songs/mario1.mid");
                }
                FinchMidiUtil.playSong(finch, f);
            }
        }
    }

    @FXML
    private void releaseKeyPress(KeyEvent ke) {
        if(ke.getCode() == KeyCode.W || ke.getCode() == KeyCode.S || ke.getCode() == KeyCode.D || ke.getCode() == KeyCode.A) {
            if(keys.contains(ke.getCode())) {
                keys.remove(ke.getCode());
            }
        }
    }

    @FXML
    private void releaseArrowButtonPress(MouseEvent me) {
        String key = Converter.convertButtonIdToKey(((Button) me.getSource()).getId());
        changeArrowImage();
        updateFinchScreen(finch.getDistance());
    }

    @FXML
    private void handleArrowButtonPress(MouseEvent me) {
        String key = Converter.convertButtonIdToKey(((Button) me.getSource()).getId());
        changeArrowImage();
        updateFinchScreen(finch.getDistance());
    }

    @FXML
    private void showMessageOnDisplay() {
        if(message.isEmpty()) {
            TextField field = (TextField) mainPane.getScene().lookup("#messageField");
            char[] characters = field.getText().toCharArray();
            for (char ch : characters) {
                message.add(ch);
            }
        }
    }

    @FXML
    private void beep() {
        isBeepSignal = true;
    }

    private void changeFinchColor() {
        if(!Arrays.equals(color, predColor)) {
            predColor[0] = color[0];
            predColor[1] = color[1];
            predColor[2] = color[2];
            ((Text) mainPane.getScene().lookup("#redText")).setText("RED: " + color[0]);
            ((Text) mainPane.getScene().lookup("#greenText")).setText("GREEN: " + color[1]);
            ((Text) mainPane.getScene().lookup("#blueText")).setText("BLUE: " + color[2]);
            finch.setBeak(predColor[0], predColor[1], predColor[2]);
        }
    }

    // Подуправление Finchом
    private void moveFinch() {
        double HALF_SPEED = MAX_SPEED / 2;
        if(keys.size() == 1) {
            KeyCode code = keys.peek();
            switch (code.getName()) {
                case "W" -> finch.setMotors(MAX_SPEED, MAX_SPEED);
                case "S" -> finch.setMotors(-MAX_SPEED, -MAX_SPEED);
                case "A" -> finch.setMotors(-HALF_SPEED, HALF_SPEED);
                case "D" -> finch.setMotors(HALF_SPEED, -HALF_SPEED);
            }
        } else if (keys.size() == 2) {
            if(keys.contains(KeyCode.W) && keys.contains(KeyCode.D)) {
                finch.setMotors(MAX_SPEED, HALF_SPEED);
            } else if(keys.contains(KeyCode.W) && keys.contains(KeyCode.A)) {
                finch.setMotors(HALF_SPEED, MAX_SPEED);
            } else if (keys.contains(KeyCode.S) && keys.contains(KeyCode.D)) {
                finch.setMotors(-MAX_SPEED, -HALF_SPEED);
            } else {
                finch.setMotors(-HALF_SPEED, -MAX_SPEED);
            }
        } else {
            finch.setMotors(0, 0);
        }
    }

    private void changeArrowImage() {
        for(KeyCode key : keys) {
            String buttonName = Converter.convertKeyToButtonId(key.getName());
            Button pressedButton = (Button) mainPane.getScene().lookup("#"+buttonName);
            ImageView img = (ImageView) pressedButton.getChildrenUnmodifiable().get(0);
            img.setImage(new Image(BUTTON_RELEASED_IMAGE_URI.toString()));
        }
    }

    private void changeAllButtonsImage() {
        Set<Node> buttons = mainPane.lookupAll(".moveButton");
        for(Node node : buttons) {
            Button button = (Button) node;
            String keyName = Converter.convertButtonIdToKey(button.getId());
            if(keys.contains(KeyCode.getKeyCode(keyName))) continue;
            ImageView view = (ImageView) button.getChildrenUnmodifiable().get(0);
            if(view.getImage().getUrl().toString().endsWith("arrow_tint.png")) {
                view.setImage(new Image(BUTTON_PRESSED_IMAGE_URI.toString()));
            }
        }
    }

    private void changeDistance(int distance) {
        Text distanceText = (Text) mainPane.getScene().lookup("#distanceText");
        distanceText.setText(distance + " sm");
    }

    private void showOnDisplay() {
        if(!message.isEmpty()) {
            if(letterTimer < System.currentTimeMillis()) {
                letterTimer = System.currentTimeMillis() + 1000l;
                char letter = message.pop();
                if((letter >= 'a' && letter <= 'z') || (letter >= 'A' && letter <= 'Z') || (letter >= '0' && letter <= '9') || letter == ' ') {
                    finch.print(""+letter);
                } else {
                    finch.setDisplay(RussionLetterConverter.STANDART_DISPLAY);
                    int[] display = RussionLetterConverter.convertToDisplay(letter);
                    finch.setDisplay(display);
                    if (message.isEmpty()) {
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                finch.setDisplay(RussionLetterConverter.STANDART_DISPLAY);
                            }
                        }, 1000l);
                    }
                }
            }
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

    private void updateFinchScreen(int distance) {
        GraphicsContext gc = finchScreen.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, finchScreen.getWidth(), finchScreen.getHeight());
        if ((distance <= MAX_FINCH_VISION_DISTANCE_SM)) {
            gc.setStroke(Color.RED);
            if(distance < 1) distance = 1;
            double cx = finchScreen.getWidth() / 2.0;
            double cy = finchScreen.getHeight() / 2.0;
            double d = 30;
            double x1 = cx - d * cx / (d + distance);
            double y1 = cy - d * cy / (d + distance);
            double x2 = cx + d * cx / (d + distance);
            double y2 = cy + d * cy / (d + distance);
            gc.beginPath();
            gc.moveTo(x1, y1);
            gc.lineTo(x2, y1);
            gc.lineTo(x2, y2);
            gc.lineTo(x1, y2);
            gc.lineTo(x1, y1);
            gc.stroke();
        }
    }

    @FXML
    private void activateAutoNavigation() {
        if(!isAutopilotActive) {
            finchUpdater.cancel();
            finchUpdater.purge();
            finchAutopilotTimer = new Timer();
            finchAutopilotTimer.schedule(createAutopilotTask(), 1000, 20);
            isAutopilotActive = !isAutopilotActive;
        } else {
            finchAutopilotTimer.cancel();
            finchAutopilotTimer.purge();
            finchUpdater = new Timer();
            finchUpdater.schedule(createCustomControlTask(), 1000, 20);
            isAutopilotActive = !isAutopilotActive;
        }
    }

    private TimerTask createAutopilotTask() {
        return new TimerTask() {
            @Override
            public void run() {
                if(isBeepSignal) {
                    finch.playNote(50, 2);
                    isBeepSignal = false;
                }
                int distance = finch.getDistance();
                rotateCompassArrow(finch.getCompass());
                changeFinchColor();
                updateFinchScreen(distance);
                changeDistance(distance);
                if(distance < 20) {
                    finch.setMove("B", 30, 50);
                } else if(distance < 40) {
                    finch.setTurn("L", 30, 50);
                    int left = finch.getDistance();
                    updateFinchScreen(left);
                    finch.setTurn("R", 60, 50);
                    int right = finch.getDistance();
                    updateFinchScreen(right);
                    finch.setTurn("L", 30, 50);
                    if(left > right){
                        finch.setTurn("L", 45, 50);
                    } else {
                        finch.setTurn("R", 45, 50);
                    }
                } else {
                    finch.setMove("F", 10, 50);
                }
            }
        };
    }

    private TimerTask createCustomControlTask() {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    if(isBeepSignal) {
                        finch.playNote(50, 2);
                        isBeepSignal = false;
                    }
                    changeAllButtonsImage();
                    showOnDisplay();
                    changeFinchColor();
                    changeDistance(finch.getDistance());
                    updateFinchScreen(finch.getDistance());
                    changeLight(finch.getLight("L"));
                    rotateCompassArrow(finch.getCompass());
                    moveFinch();
                    changeArrowImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
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
            case "leftButton" : retVal = "A";
                break;
            case "rightButton"  : retVal = "D";
                break;
        };
        return retVal;
    }
}