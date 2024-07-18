package com.example.messengerclient;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShowImage {
    @FXML
    private Pane mainPane;
    @FXML
    private ImageView displayImage;
    @FXML
    private Button downLoadImage;
    private StackPane stackPane;
    private Image image;
    public void setImage(Image image) {
        displayImage.setImage(image);
        this.image = image;
    }
    public void setStackPane(StackPane stackPane){
        this.stackPane=stackPane;
    }
    @FXML
    private void downloadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            try {
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image,null);
                ImageIO.write(bufferedImage,getExtension(selectedFile.getAbsolutePath().toString()) , new FileOutputStream(selectedFile));
                System.out.println("Image saved to: " + selectedFile.getAbsolutePath());
                System.out.println("Image format: " + getExtension(selectedFile.getAbsolutePath()));
            } catch (IOException e) {
                System.err.println("Error saving image: " + e.getMessage());
            }
        }
    }
    @FXML
    private void returnToChatBox(){
        int size=stackPane.getChildren().size();
        this.stackPane.getChildren().remove(size-1);
    }
    private String getExtension(String filePath) {
        String[] parts = filePath.split("\\.");
        return parts[parts.length - 1];
    }

}
