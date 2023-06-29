module com.example.chessproject {
  requires javafx.controls;
  requires javafx.fxml;
  requires org.controlsfx.controls;
  requires com.dlsc.formsfx;

  opens com.example.chessproject to
      javafx.fxml;

  exports com.example.chessproject;
  exports com.example.chessproject.view;
  opens com.example.chessproject.view to javafx.fxml;
  exports com.example.chessproject.controller;
  opens com.example.chessproject.controller to javafx.fxml;
  exports com.example.chessproject.model;
}
