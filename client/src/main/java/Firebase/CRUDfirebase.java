package Firebase;

import Model.Message;
import Model.User;
import com.example.messengerclient.Main;
import com.google.cloud.firestore.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.google.api.core.ApiFuture;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CRUDfirebase {
    private boolean key;
    private ObservableList<Message>listMessages= FXCollections.observableArrayList();
    private ObservableList<User>listUsers=FXCollections.observableArrayList();
    public ObservableList<Message>getListMessages(){
        return listMessages;
    }
    public ObservableList<User>getListUsers(){
        return listUsers;
    }
    //Đọc các tin nhắn có trong hộp thoại đang sử dụng
    public boolean readMessageFirebase(String DocumentID){
        key=false;
        if(!listMessages.isEmpty()){
            listMessages.clear();
        }
        ApiFuture<QuerySnapshot>future= Main.db.collection(DocumentID).orderBy("timestamp", Query.Direction.ASCENDING).get();
        try{
            List<QueryDocumentSnapshot> documents=future.get().getDocuments();
            if(documents.size()>0){
                System.out.println("Reading data...");

                for(QueryDocumentSnapshot document:documents){
                    String message=document.getData().get("message").toString();
                    String image="";
                    if(document.getData().get("image")!=null){

                        image=document.getData().get("image").toString();
                    }
                    String messageType=document.getData().get("messageType").toString();
                    String senderID=document.getData().get("senderID").toString();
                    listMessages.add(new Message(message,image,messageType,senderID));
                }
            }
            key=true;
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }catch (ExecutionException e){
            throw new RuntimeException(e);
        }
        return  key;
    }
    //Đọc tất cả người dùng có trong cơ sở dữ liệu
    public boolean readUserFirebase(){
        key=false;

        ApiFuture<QuerySnapshot>future= Main.db.collection("User").orderBy("id", Query.Direction.ASCENDING).get();
        try{
            List<QueryDocumentSnapshot> documents=future.get().getDocuments();
            if(documents.size()>0){
                System.out.println("Reading data...");
                listUsers.clear();
                for(QueryDocumentSnapshot document:documents){
                    String id=document.getData().get("id").toString();
                    String fullname=document.getData().get("fullname").toString();
                    String password=document.getData().get("password").toString();
                    String avatar=document.getData().get("avatar").toString();
                    boolean isOnline;
                    if(document.getData().get("isOnline").toString().equals("true")){
                        isOnline=true;
                    }
                    else {
                        isOnline=false;
                    }
                    User user=new User(id,fullname,password,avatar,isOnline);
                    listUsers.add(user);
                }
            }
            key=true;
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }catch (ExecutionException e){
            throw new RuntimeException(e);
        }
        return  key;
    }
    //Tìm kiếm theo id người dùng xem id đã tồn tại hay chưa
    public int findID(String id,String fullname,String password){
        return Collections.binarySearch(listUsers, new User(id, fullname, password), (user1, user2) -> user1.getId().compareTo(user2.getId()));
    }
    //So sánh tên người dùng và mật khẩu xem có trùng với tài khoản đã đăng kí hay không
    public boolean findUser(String id,String fullname,String password){
        key=false;
        int index = Collections.binarySearch(listUsers, new User(id, fullname, password), (user1, user2) -> user1.getId().compareTo(user2.getId()));

        if (index >= 0) {
            User user=listUsers.get(index);
            if(user.getName().equals(fullname)&&user.getPassword().equals(password)){
                key=true;
            }
        }
        return key;
    }
    //Thêm tài khoản vào cơ sở dữ liệu
    public boolean addAccToFirebase(String id,String fullname,String password,String avatar, boolean isOnline){
        key=false;
        Map<String, Object> docUser = new HashMap<>();
        docUser.put("id",id);
        docUser.put("fullname",fullname);
        docUser.put("password",password);
        docUser.put("avatar",avatar);
        docUser.put("isOnline",isOnline);
        ApiFuture<WriteResult> future = Main.db.collection("User").document(id).set(docUser);
        try {
            System.out.println("Update time : " +future.get().getUpdateTime());
            key=true;
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        return  key;
    }
    //Cập nhật danh sách người dùng và vẫn đảm bảo thứ tự tăng dần về id trong danh sách
    public int updateListUsers(User user){
        int index=Collections.binarySearch(listUsers,user);
        if(index>=0){
            listUsers.remove(index);
            listUsers.add(index,user);
        }
        return index;
    }
    //Thêm vào người dùng mà vẫn đảm bảo thứ tự tăng dần về id
    public int addUser(User user){
        int index=Collections.binarySearch(listUsers,user);
        if(index<0){
            index=-(index+1);
            listUsers.add(index,user);
        }
        return index;
    }

    public void updateFirebase(String id,String imageString){
        DocumentReference docref=Main.db.collection("User").document(id);
        try{
            ApiFuture<WriteResult> future=docref.update("avatar",imageString);
            System.out.println("Updated :"+future.get().getUpdateTime());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
