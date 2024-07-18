package org.example;


import Model.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;

public class ClientHandler implements Runnable{
    private Socket mySocket;
    private ChatServer chatServer;
    private String id;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private Queue<Message> messages;
    public ClientHandler(Socket mySocket, ChatServer chatServer,Queue<Message> messages) {
        try {
            this.messages=messages;
            this.chatServer=chatServer;
            this.mySocket = mySocket;
            this.objectInputStream=new ObjectInputStream(mySocket.getInputStream());
            this.objectOutputStream=new ObjectOutputStream(mySocket.getOutputStream());
            Message message=(Message)objectInputStream.readObject();
            id=message.getText();
            System.out.println("ID: "+id);
            ChatServer.crudFirebase.updateFirebase(id,true);
        }catch (IOException | ClassNotFoundException exception){
            exception.printStackTrace();
            System.out.println("Kết nối không thành công");
        }
    }

    @Override
    public void run() {
        try {
            while (mySocket.isConnected()){
                Message message=(Message) objectInputStream.readObject();
                String receiverID=message.getReceiverID();
                 boolean tmp=messages.add(message);
                 if(tmp){
                     System.out.println("Added message to queue");
                 }
                if(receiverID.equals("01")){
                    chatServer.broadcastMessage(message);
                }else {
                    chatServer.sendMessageToSpecificUser(message,receiverID);
                }
                System.out.println("Nhận tin nhắn thành công");
            }
        }catch (Exception e){
            e.printStackTrace();
            ChatServer.crudFirebase.updateFirebase(id,false);
            ChatServer.removeClient(id);
            closeEverything();
        }
    }
    public void sendMessage(Message message){
        try{
            objectOutputStream.writeObject(message);
            System.out.println("Đã gửi tin nhắn đi cho người nhận");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public String getId() {
        return id;
    }
    private void closeEverything(){
        try{
            if(objectOutputStream!=null){
                objectOutputStream.close();
            }
            if(objectInputStream!=null){
                objectInputStream.close();
            }
            if(mySocket!=null){
                mySocket.close();
            }
            System.out.println("Đã đóng cổng !!");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
