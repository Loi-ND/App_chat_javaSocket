package org.example;

import Firebase.CRUDfirebase;
import Model.Message;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatServer {
    private static  final int PORT=41981;
    private static List<ClientHandler>clients=new ArrayList<>();
    private Queue<Message>messages=new ConcurrentLinkedQueue<>();
    public static CRUDfirebase crudFirebase=new CRUDfirebase();
    public void startServer(){
        try {
            Thread thread=new Thread(new SaveMessage(messages));
            thread.start();
            ServerSocket serverSocket=new ServerSocket(PORT);
            System.out.println("SERVER started listening on port: "+PORT);
            while (true){
                Socket Clientsocket=serverSocket.accept();
                System.out.println("New client connected !!"+Clientsocket.getInetAddress().getHostAddress());
                ClientHandler clientHandler=new ClientHandler(Clientsocket,this,messages);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void sendMessageToSpecificUser(Message message,String receiverID){
        for(ClientHandler client:clients){
            if(client.getId().equals(receiverID)){
                client.sendMessage(message);
            }
        }
    }
    public static void removeClient(String id){
        for(ClientHandler clientHandler:clients){
            if(clientHandler.getId().equals(id)){
                clients.remove(clientHandler);
            }
        }
    }
    public void broadcastMessage(Message message){
        for(ClientHandler client:clients){
            client.sendMessage(message);
        }
    }
}
