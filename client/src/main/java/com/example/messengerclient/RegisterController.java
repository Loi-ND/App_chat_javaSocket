package com.example.messengerclient;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    @FXML
    private Button registerButton;
    @FXML
    private Button returnButton;
    @FXML
    private TextField fullname;
    @FXML
    private TextField password;
    @FXML
    private TextField password2;
    @FXML
    private CheckBox showpass;
    @FXML
    private TextField id;
    @FXML
    private void togglePasswordVisibility() {
        if (showpass.isSelected()) {
            password2.setText(password.getText());
            password.setVisible(false);
            password2.setVisible(true);
        }
        else {
            password.setText(password2.getText());
            password2.setVisible(false);
            password.setVisible(true);
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        registerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!fullname.getText().equals("")&&!password.getText().equals("")&&!id.getText().equals("")){
                    if(id.getText().length()!=4){
                        Alert alert=new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Fail ID");
                        alert.setContentText("Your ID isn't valid");
                        alert.showAndWait();
                    }
                    else if(Main.cruDfirebase.findID(id.getText().trim(),fullname.getText().trim(), password.getText())>=0){
                        Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Sign Up");
                        alert.setHeaderText("you want to register an account ?");
                        alert.setContentText("This ID already exists, please choose another ID");
                        alert.showAndWait();
                    }else{
                        try {
                            Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Sign Up");
                            alert.setHeaderText("you want to register an account ?");
                            alert.setContentText("You have successfully registered an account");
                            BufferedImage bufferedImage = ImageIO.read(new File("src/main/resources/avtar.png"));
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(bufferedImage, "png", baos);
                            byte[] imageData = baos.toByteArray();
                            String encodedString = Base64.getEncoder().encodeToString(imageData);
                            Main.cruDfirebase.addAccToFirebase(id.getText().trim(),fullname.getText().trim(),password.getText().trim(),encodedString,false);
                            alert.showAndWait();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        returnButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                StageAndScene.showLoginScene();
            }
        });
    }
}
