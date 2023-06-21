module org.jenjetsu.com.finch {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.jenjetsu.com.finch to javafx.fxml;
    exports org.jenjetsu.com.finch;
}