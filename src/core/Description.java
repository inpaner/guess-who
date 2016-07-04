package core;

/**
 * Created by Ivan Paner on 7/4/2016.
 */
public class Description {
    private String description;
    private String question = "";
    private Description superclass;
    private String group = "";


    Description(String description) {
        this.description = description;
    }


    String getDescription() {
        return description;
    }



}
