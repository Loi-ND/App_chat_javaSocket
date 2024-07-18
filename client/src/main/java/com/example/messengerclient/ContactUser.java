package com.example.messengerclient;


import Model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ContactUser {
    @FXML
    private ImageView avatar;

    @FXML
    private Label nameLabel;

    @FXML
    private Circle statusIcon;

    @FXML
    private Label statusLabel;

    public void setAvatar(Image avatar,String name) {
        this.avatar.setImage(avatar);
        this.nameLabel.setText(name);
    }
    public void setOnlineStatus(){
        statusIcon.setFill(Color.rgb(0,170,0));
        statusLabel.setText("Active now");
        statusLabel.setTextFill(Color.rgb(0,170,0));
    }
    public void setOfflineStatus(){
        statusIcon.setFill(Color.rgb(245,0,0));
        statusLabel.setText("Offline");
        statusLabel.setTextFill(Color.rgb(245,0,0));
    }
    public void setContactUser(User user) {
        setAvatar(user.getAvatar(),user.getName());
        if(user.isOline()){
            setOnlineStatus();
        }else{
            setOfflineStatus();
        }
    }
    @FXML
    private void action(MouseEvent event){
        System.out.println("Bạn đã nhấn vào ảnh");
    }
}
