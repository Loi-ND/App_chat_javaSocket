package com.example.messengerclient;

import Model.Message;
import Model.MessageType;
import Model.Owner;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private Owner owner;
    private Controller controller;
    public Client(Socket socket,Owner owner,Controller controller){
        try{
            this.socket=socket;
            this.objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream=new ObjectInputStream(socket.getInputStream());
            this.owner=owner;
            this.controller=controller;
            Message message=new Message(owner.getID(),"","","","");
            objectOutputStream.writeObject(message);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error connecting to server");
        }
    }
    public void sendImageToServer(Image image,String format){
        try{
            Message message=new Message("",image,format,"IMAGE",owner.getID(),Controller.receiverID);
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        }catch (IOException e){
            closeEveryThing(socket,objectInputStream,objectOutputStream);
            System.out.println("Error sending image to server");
            e.printStackTrace();
        }
    }
    public void receiveMessageFromServer(VBox vBox){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(socket.isConnected()){
                    try{
                        Message message=(Message) objectInputStream.readObject();
                        switch (message.getMessageType()){
                            case IMAGE :
                                controller.addLabel1(message.getImage(),vBox,message.getSenderID());
                                break;
                            case TEXT_MESSAGE:
                                controller.addLabel(message.getText(), vBox,message.getSenderID());
                        }
                        System.out.println("Nhận tin nhắn thành công");
                    }catch (IOException e){
                        e.printStackTrace();
                        System.out.println("Error receiving information from server");
                        closeEveryThing(socket,objectInputStream,objectOutputStream);
                    }catch (ClassNotFoundException e){
                        e.printStackTrace();
                        System.out.println("Error receiving information from server");
                        closeEveryThing(socket,objectInputStream,objectOutputStream);
                    }
                }
            }
        }).start();
    }
    public void sendMessageToServer(String messageToServer){
        try {
            Message message = new Message(messageToServer,"","TEXT",owner.getID(),Controller.receiverID);
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Error sending message tho the client");
            closeEveryThing(socket,objectInputStream,objectOutputStream);
        }
    }
    public void closeEveryThing(Socket socket,ObjectInputStream objectInputStream,ObjectOutputStream objectOutputStream){
        try{
            if(socket!=null){
                socket.close();
            }
            if(objectOutputStream!=null){
                objectOutputStream.close();
            }
            if(objectInputStream!=null){
                objectInputStream.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
