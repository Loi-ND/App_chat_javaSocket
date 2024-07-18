package Firebase;

import Model.MessageType;
import com.google.cloud.firestore.*;
import org.example.Main;
import com.google.api.core.ApiFuture;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CRUDfirebase {
    private boolean key;
    public boolean readFirebase(){
        key=false;
        ApiFuture<QuerySnapshot>future= Main.db.collection("Messages").get();
        try{
            List<QueryDocumentSnapshot> documents=future.get().getDocuments();
            if(documents.size()>0){
                System.out.println("Reading data...");

                for(int i=documents.size()-1;i>-1;i--){
                    System.out.println(documents.get(i).getId()+"=> "+documents.get(i).getData().get("message"));
                }
            }else {
                System.out.println("Don't have any data");
            }
            key=true;
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }catch (ExecutionException e){
            throw new RuntimeException(e);
        }
        return  key;
    }
    public void updateFirebase(String id,boolean status){
        DocumentReference docref=Main.db.collection("User").document(id);
        String isOnline;
        if(status){
            isOnline="true";
        }else{
            isOnline="false";
        }
        try{
            ApiFuture<WriteResult> future=docref.update("isOnline",isOnline);
            System.out.println("Updated :"+future.get().getUpdateTime());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean addFirebase(String DocumentID, String message, String imageData, String senderID, String messageType){
        key=false;

        Map<String, Object> docMessage = new HashMap<>();
        docMessage.put("message",message);
        docMessage.put("image",imageData);
        docMessage.put("timestamp", FieldValue.serverTimestamp());
        docMessage.put("senderID",senderID);
        if(messageType!=null){
            docMessage.put("messageType",messageType);
        }
        ApiFuture<WriteResult> future = Main.db.collection(DocumentID).document(UUID.randomUUID().toString()).set(docMessage);

        try {
            System.out.println("Update time : " +future.get().getUpdateTime());
            key=true;
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        return key;
    }
}

