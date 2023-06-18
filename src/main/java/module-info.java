module com.example.searchproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.searchproject to javafx.fxml;
    exports com.example.searchproject;
}