package Model;

import javafx.scene.image.Image;


public class Owner {
    private String name;
    private String id;
    private Image avatar;
    private boolean isOline;

    public Owner(String name, String id, Image image) {
        this.name = name;
        this.id = id;
        this.avatar = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public Image getAvatar() {
        return avatar;
    }


    public boolean isOline() {
        return isOline;
    }

    public void setOline(boolean oline) {
        isOline = oline;
    }
}
