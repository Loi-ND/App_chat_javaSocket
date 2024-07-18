package org.example;

import Firebase.ConnectionFirebase;
import com.google.cloud.firestore.Firestore;

public class Main {
    private static final ConnectionFirebase connectionFirebase=new ConnectionFirebase();
    public static Firestore db;
    public static void main(String[] args) {
        db=connectionFirebase.StartFirebase();
        ChatServer chatServer=new ChatServer();
        chatServer.startServer();
    }
}