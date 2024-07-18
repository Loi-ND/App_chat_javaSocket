package Model;

import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;

import static com.almasb.fxgl.texture.ImagesKt.toBufferedImage;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private MessageType messageType;
    private String text;
    private byte[] imageData;
    private String senderID;
    private String format;
    private String receiverID;
    public Message(String text, String imageString,String messageType, String senderID, String receiverID) {
        this.text = text;
        this.senderID = senderID;
        this.receiverID = receiverID;
        if(messageType.equals("IMAGE")){
            this.imageData = Base64.getDecoder().decode(imageString);
            this.messageType = MessageType.IMAGE;
        }
        else if(messageType.equals("TEXT")){
            this.imageData=null;
            this.messageType=MessageType.TEXT_MESSAGE;
        }
        System.out.println("Client: messageType = " + this.messageType);
    }

    public Message(String text, Image image,String format,String messageType, String senderID, String receiverID){
        this.text = text;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.format=format;
        if(messageType.equals("IMAGE")){
            this.messageType = MessageType.IMAGE;
            BufferedImage bufferedImage = toBufferedImage(image);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                ImageIO.write(bufferedImage,format,byteArrayOutputStream);
                imageData=byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        else if(messageType.equals("TEXT")){
            this.messageType=MessageType.TEXT_MESSAGE;
        }
        System.out.println("Client: messageType = " + this.messageType);
    }

    public Message(String message,String imageString,String messageType,String senderID){
        this.text = message;
        if(messageType.equals("IMAGE")){
            this.messageType = MessageType.IMAGE;
        }else {
            this.messageType=MessageType.TEXT_MESSAGE;
        }
        this.senderID = senderID;
        if(this.messageType==MessageType.IMAGE){
            this.imageData=Base64.getDecoder().decode(imageString);
        }else{
            this.imageData=null;
        }
    }

    public String getText() {

        return text;
    }

    public void setText(String text) {
        this.text = text;
    }



    public String getSenderID() {

        return senderID;
    }

    public String getReceiverID() {

        return this.receiverID;
    }

    public MessageType getMessageType() {

        return this.messageType;
    }

    public void setSenderID(String senderID) {

        this.senderID = senderID;
    }

    public Image getImage(){
        return byteToImage(this.imageData);
    }

    private Image byteToImage(byte[] byteArray){
        if (byteArray != null ) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            return new Image(byteArrayInputStream);
        } else {
            return null;
        }
    }
}
