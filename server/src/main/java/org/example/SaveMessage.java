package org.example;

import Firebase.CRUDfirebase;
import Model.Message;

import java.util.Queue;

public class SaveMessage implements Runnable{
    private Queue<Message> messages;
    private CRUDfirebase cruDfirebase=new CRUDfirebase();
    public SaveMessage(Queue<Message> messages){
        this.messages=messages;
    }
    @Override
    public void run() {
        while(true){
            if(!messages.isEmpty()){
                Message message= messages.poll();
                String text=message.getText();
                String image=message.getImage();
                String senderID=message.getSenderID();
                String receiverID=message.getReceiverID();
                String messageType=message.getMessageType().toString();
                if(message.getReceiverID().equals("01")){
                    cruDfirebase.addFirebase("01",text,image,senderID,messageType);
                }
                else{
                    String documentID;
                    if(senderID.compareTo(receiverID)<0){
                        documentID=senderID+receiverID;
                    }
                    else {
                        documentID=receiverID+senderID;
                    }
                    cruDfirebase.addFirebase(documentID,text,image,senderID,messageType);
                }
                System.out.println("Save message successfully");
            }
        }
    }
}
