module org.jenjetsu.com.finch {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;


    opens org.jenjetsu.com.finch to javafx.fxml;
    exports org.jenjetsu.com.finch;
}