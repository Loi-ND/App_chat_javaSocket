package com.example.messengerclient;

import Model.Message;
import Model.Owner;
import Model.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.almasb.fxgl.texture.ImagesKt.toBufferedImage;

public class Controller implements Initializable {
    @FXML
    private Button button_send;

    @FXML
    private TextField tf_message;
    @FXML
    private VBox vbox_messages;

    @FXML
    private ScrollPane sp_main;

    @FXML
    private Button button_choose_img;

    @FXML
    private ListView list_user;

    @FXML
    private StackPane componentStack;

    @FXML
    private StackPane userStack;

    @FXML
    private Button Button_logout;
    public static boolean showed=false;
    private static ListView<HBox> listUserReference;

    private static List<FriendsItem>friendsItemList=new ArrayList<>();

    private static ContactUser contactUser;

    private Client client;

    public static String receiverID;

    public static Owner owner;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listUserReference=list_user;
        try{
            client=new Client(new Socket("localhost",41981),owner,this);
            System.out.println("Connected to server");
        }catch (IOException e){
            e.printStackTrace();
        }
        vbox_messages.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                sp_main.setVvalue((Double)newValue);
            }
        });
        Button_logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Logout");
                alert.setHeaderText("You're about to logout!");
                alert.setContentText("Are you sure you want to logout?");
                if(alert.showAndWait().get() == ButtonType.OK){
                    StageAndScene.showLoginScene();
                }
            }
        });
        FXMLLoader loader=new FXMLLoader(getClass().getResource("contactUser.fxml"));
        try {
            userStack.getChildren().add(loader.load());
            contactUser=loader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for(User user:Main.cruDfirebase.getListUsers()){
            if(user.getId().equals(owner.getID())){
                user.setOline(true);
            }
            list_user.getItems().add(createFriendsItem(user.getName(), user.getAvatar(),user.isOline()));
        }
        User firstUser=Main.cruDfirebase.getListUsers().get(0);
        contactUser.setContactUser(firstUser);
        list_user.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            String tmp=Main.cruDfirebase.getListUsers().get(list_user.getItems().indexOf(newValue)).getId();
            if(!tmp.equals(receiverID)){
                receiverID=tmp;
                System.out.println(list_user.getItems().indexOf(newValue));
                Platform.runLater(()->{
                    vbox_messages.getChildren().clear();
                    contactUser.setContactUser(Main.cruDfirebase.getListUsers().get(list_user.getItems().indexOf(newValue)));
                    ADDChatHistory(receiverID);
                });
            }
        });
        client.receiveMessageFromServer(vbox_messages);

    }
    private void ADDChatHistory(String receiverID){
        if(receiverID.equals("01")){
            Main.cruDfirebase.readMessageFirebase("01");
            addChatHistory(Main.cruDfirebase.getListMessages());
        }
        else {
            String documentID;
            if(owner.getID().compareTo(receiverID)<0){
                documentID=owner.getID()+receiverID;
            }
            else {
                documentID=receiverID+owner.getID();
            }
            Main.cruDfirebase.readMessageFirebase(documentID);
            addChatHistory(Main.cruDfirebase.getListMessages());
        }
    }

    public static void updateFriendItem(int index, User user){
        Platform.runLater(() -> {
            if (index >= 0 && index < friendsItemList.size() && friendsItemList.get(index) != null) {
                friendsItemList.get(index).setFriend(user.getName(), user.getAvatar(), user.isOline());
                if (user.getId().equals(receiverID)) {
                    contactUser.setContactUser(user);
                }
            }
            if(user.getId().equals(receiverID)){
                contactUser.setContactUser(user);
            }
        });
    }

    public static void addFriendsItem(int index,User user){
        if(showed){
            Platform.runLater(()->{
                if(!listUserReference.getItems().isEmpty()){
                    listUserReference.getItems().add(index, createFriendsItem(user.getName(), user.getAvatar(),user.isOline()));
                }
            });
        }
    }

    private ImageView createViewImage(Image image){
        FXMLLoader loader=new FXMLLoader(getClass().getResource("image.fxml"));
        try{
            ImageView imageView=loader.load();
            viewImage controller=loader.getController();
            controller.setImageObject(image);
            controller.setViewImage(image);
            controller.setStackpane(componentStack);
            return imageView;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
    private static HBox createFriendsItem(String name, Image image, boolean isOnline){
        FXMLLoader loader = new FXMLLoader(Controller.class.getResource("friends-item.fxml"));
        try {
            HBox friendItem = loader.load();
            FriendsItem controller = loader.getController();
            friendsItemList.add(controller);
            if (controller != null) {
                controller.setFriend(name, image,isOnline);
            }
            return friendItem;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void addChatHistory(ObservableList<Message> listMessages){
        for(Message message:listMessages){
            switch (message.getMessageType()){
                case TEXT_MESSAGE :
                    if(message.getSenderID().equals(owner.getID())){
                        HBox hBox=new HBox();
                        hBox.setAlignment(Pos.CENTER_RIGHT);
                        hBox.setPadding(new Insets(5,5,5,10));
                        Text text=new Text(message.getText());
                        Font font = new Font("Arial", 16);
                        text.setFont(font);
                        TextFlow textFlow=new TextFlow(text);
                        textFlow.setStyle("-fx-color: rgb(239,242,255);"+"-fx-background-color: rgb(15,125,242);"+"-fx-background-radius: 20px");
                        textFlow.setPadding(new Insets(5,10,5,10));
                        text.setFill(Color.color(0.934,0.945,0.996));
                        textFlow.setMaxWidth(500);
                        hBox.getChildren().add(textFlow);
                        vbox_messages.getChildren().add(hBox);
                    }
                    else{
                        HBox hBox=new HBox();
                        hBox.setAlignment(Pos.CENTER_LEFT);
                        hBox.setPadding(new Insets(5,5,5,10));
                        Text text=new Text(message.getText());
                        TextFlow textFlow=new TextFlow(text);
                        Font font = new Font("Arial", 16);
                        text.setFont(font);
                        textFlow.setStyle("-fx-background-color: rgb(233,233,235);"+"-fx-background-radius: 20px");
                        textFlow.setPadding(new Insets(5,10,5,10));
                        textFlow.setMaxWidth(500);
                        hBox.getChildren().add(textFlow);
                        vbox_messages.getChildren().add(hBox);
                    }
                    break;
                case IMAGE:
                    if(message.getSenderID().equals(owner.getID())){
                        HBox hbox=new HBox();
                        hbox.setAlignment(Pos.CENTER_RIGHT);
                        hbox.setPadding(new Insets(5,5,5,10));
                        ImageView imageView=createViewImage(message.getImage());
                        hbox.getChildren().add(imageView);
                        vbox_messages.getChildren().add(hbox);
                    }else{
                        HBox hbox=new HBox();
                        hbox.setAlignment(Pos.CENTER_LEFT);
                        hbox.setPadding(new Insets(5,5,5,10));
                        ImageView imageView=createViewImage(message.getImage());
                        hbox.getChildren().add(imageView);
                        vbox_messages.getChildren().add(hbox);
                    }
            }
        }
    }

    public void addLabel1(Image image,VBox vBox,String senderID){
        if(senderID.equals(receiverID)){
            HBox hBox=new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5,5,5,10));
            ImageView imageView=createViewImage(image);
            hBox.getChildren().addAll(imageView);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    vBox.getChildren().add(hBox);
                }
            });
        }
    }

    public  void addLabel(String msgFromServer,VBox vBox,String senderID){
        if(senderID.equals(receiverID)){
            HBox hBox=new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5,5,5,10));
            Text text=new Text(msgFromServer);
            Font font = new Font("Arial", 16);
            text.setFont(font);
            TextFlow textFlow=new TextFlow(text);
            textFlow.setStyle("-fx-background-color: rgb(233,233,235);"+"-fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5,10,5,10));
            textFlow.setMaxWidth(500);
            hBox.getChildren().add(textFlow);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    vBox.getChildren().add(hBox);
                }
            });
        }
    }
    @FXML
    private void sendMessage(ActionEvent event){
        String messageToSend=tf_message.getText();
        if(!messageToSend.isEmpty()){
            HBox hBox=new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(5,5,5,10));
            Text text=new Text(messageToSend);
            Font font = new Font("Arial", 16);
            text.setFont(font);
            TextFlow textFlow=new TextFlow(text);
            textFlow.setStyle("-fx-color: rgb(239,242,255);"+"-fx-background-color: rgb(15,125,242);"+"-fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5,10,5,10));
            text.setFill(Color.color(0.934,0.945,0.996));
            textFlow.setMaxWidth(500);
            hBox.getChildren().add(textFlow);
            vbox_messages.getChildren().add(hBox);
            client.sendMessageToServer(messageToSend);
            tf_message.clear();
        }
    }
    @FXML
    private void chooseImage(ActionEvent event){
        Stage stage = (Stage) button_choose_img.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        File selectedFile = fileChooser.showOpenDialog(stage);
        if(selectedFile!=null){
            HBox hBox=new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(5,5,5,10));
            Image image=new Image(selectedFile.toURI().toString());
            ImageView imageView=createViewImage(image);
            hBox.getChildren().add(imageView);
            vbox_messages.getChildren().add(hBox);
            String path=selectedFile.getPath().toString();
            int indexDot=path.indexOf('.');
            String format=path.substring(indexDot+1);
            client.sendImageToServer(image,format);
        }
    }
    @FXML
    private void textFillMessage(ActionEvent event){
        String messageToSend=tf_message.getText();
        if(!messageToSend.isEmpty()){
            HBox hBox=new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(5,5,5,10));
            Text text=new Text(messageToSend);
            Font font = new Font("Arial", 16);
            text.setFont(font);
            TextFlow textFlow=new TextFlow(text);
            textFlow.setStyle("-fx-color: rgb(239,242,255);"+"-fx-background-color: rgb(15,125,242);"+"-fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5,10,5,10));
            text.setFill(Color.color(0.934,0.945,0.996));
            textFlow.setMaxWidth(500);
            hBox.getChildren().add(textFlow);
            vbox_messages.getChildren().add(hBox);
            client.sendMessageToServer(messageToSend);
            tf_message.clear();
        }
    }
    @FXML
    private void updateAvatar(ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Update Avatar");
        alert.setContentText("Are you want to set up an avatar?");
        if(alert.showAndWait().get()==ButtonType.OK){
            Stage stage = (Stage) button_choose_img.getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image File");
            File selectedFile = fileChooser.showOpenDialog(stage);
            if(selectedFile!=null){
                String path=selectedFile.getPath().toString();
                int indexDot=path.indexOf('.');
                String format=path.substring(indexDot+1);
                Image image=new Image(selectedFile.toURI().toString());
                BufferedImage bufferedImage = toBufferedImage(image);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    ImageIO.write(bufferedImage,format,byteArrayOutputStream);
                    byte[]imageData=byteArrayOutputStream.toByteArray();
                    String imageString=Base64.encodeBase64String(imageData);
                    Main.cruDfirebase.updateFirebase(owner.getID(),imageString);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}