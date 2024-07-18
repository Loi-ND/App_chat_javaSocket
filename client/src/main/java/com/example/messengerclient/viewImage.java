package com.example.messengerclient;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class viewImage {
    @FXML
    private ImageView image;
    private Image imageObject;
    private StackPane stackpane;
    @FXML
    private void showImage(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ShowImage.fxml"));
        try{
            Pane pane = (Pane) fxmlLoader.load();
            ShowImage controller = (ShowImage) fxmlLoader.getController();
            controller.setImage(imageObject);
            stackpane.getChildren().add(pane);
            controller.setStackPane(stackpane);
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Bạn đã nhấn chuột vào ảnh");
    }

    public void setImageObject(Image imageObject) {
        this.imageObject = imageObject;
    }

    public void setStackpane(StackPane stackpane) {
        this.stackpane = stackpane;
    }
    public void setViewImage(Image img) {
        this.image.setImage(img);
        this.image.setFitHeight(250);
        this.image.setFitWidth(250);
        this.image.setPreserveRatio(true);
    }
}
