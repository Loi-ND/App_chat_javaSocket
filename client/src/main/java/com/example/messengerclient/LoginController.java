package com.example.messengerclient;

import Model.Owner;
import Model.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private TextField fullname;
    @FXML
    private TextField password;
    @FXML
    private TextField password2;
    @FXML
    private TextField id;
    @FXML
    private CheckBox showpass;
    @FXML
    private Button signInButton;
    @FXML
    private Button signUpButton;
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
        if(Main.cruDfirebase.readUserFirebase()){
            System.out.println("Read success user");
        }else{
            System.out.println("Can't read user");
        }
        signUpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                StageAndScene.showRegisterScene();
            }
        });
        signInButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!fullname.getText().isEmpty()
                        && !password.getText().isEmpty()
                        && !id.getText().isEmpty()) {
                    if(Main.cruDfirebase.findUser(id.getText().trim(),fullname.getText().trim(), password.getText())){
                        ObservableList<User>listUser=Main.cruDfirebase.getListUsers();
                        int index=Main.cruDfirebase.findID(id.getText().trim(),fullname.getText().trim(),password.getText().trim());
                        User user=listUser.get(index);
                        Controller.owner=new Owner(user.getName(), user.getId(), user.getAvatar());
                        StageAndScene.showMainScene();
                        System.out.println("Account is correct!!");
                    }else{
                        System.out.println("Account isn't correct!!");
                    }
                }
            }
        });
    }
}
