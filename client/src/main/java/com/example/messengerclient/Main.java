package com.example.messengerclient;

import Firebase.CRUDfirebase;
import Firebase.ConnectionFirebase;
import Model.User;
import com.google.cloud.firestore.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class Main extends Application {
    private final ConnectionFirebase connectionFirebase=new ConnectionFirebase();
    public static Firestore db;
    public static CRUDfirebase cruDfirebase=new CRUDfirebase();
    private boolean isFirstCall=true;
    @Override
    public void start(Stage stage) throws IOException {
        db=connectionFirebase.StartFirebase();
        StageAndScene.stage=stage;
        db.collection("User").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirestoreException e) {
                if (e != null) {
                    System.out.println("Listen failed: " + e);
                    return;
                }
                if (queryDocumentSnapshots != null) {
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        if(!isFirstCall){
                            String id=dc.getDocument().getData().get("id").toString();
                            String fullname=dc.getDocument().getData().get("fullname").toString();
                            String password=dc.getDocument().getData().get("password").toString();
                            String avatar=dc.getDocument().getData().get("avatar").toString();
                            String isOnline=dc.getDocument().getData().get("isOnline").toString();
                            boolean online;
                            if(isOnline.equals("true")){
                                online=true;
                            }else{
                                online=false;
                            }
                            User user=new User(id,fullname,password,avatar,online);
                            switch (dc.getType()) {
                                case ADDED:
                                    System.out.println("New person: " + dc.getDocument().getData().get("id").toString());
                                    // Xử lý khi có một bản ghi mới được thêm vào
                                    int index=cruDfirebase.addUser(user);
                                    Controller.addFriendsItem(index,user);
                                    break;
                                case MODIFIED:
                                    System.out.println("Modified person: " + dc.getDocument().getData().get("id").toString());
                                    // Xử lý khi một bản ghi được sửa đổi
                                    cruDfirebase.updateListUsers(user);
                                    int index2=cruDfirebase.updateListUsers(user);
                                    Controller.updateFriendItem(index2,user);
                                    break;
                            }
                        }
                    }
                    isFirstCall =false;
                } else {
                    System.out.println("Current data: null");
                }
            }
        });
        StageAndScene.showLoginScene();
    }

    public static void main(String[] args) {
        launch();
    }
}