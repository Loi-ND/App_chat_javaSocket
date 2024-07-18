package com.example.messengerclient;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class FriendsItem {
    @FXML
    private ImageView imageView;

    @FXML
    private Circle frame;

    @FXML
    private Label label;

    @FXML
    private Circle statusIcon;

    @FXML
    private Label statusLabel;

    public void setFriend(String name, Image image,boolean isOnline){
        imageView.setImage(image);
        label.setText(name);
        if(isOnline){
            setOnlineStatus();
        }else{
            setOfflineStatus();
        }
    }
    public void setOnlineStatus(){
        statusIcon.setFill(Color.rgb(0,170,0));
    }
    public void setOfflineStatus(){
        statusIcon.setFill(Color.rgb(245,0,0,1));
    }
}
