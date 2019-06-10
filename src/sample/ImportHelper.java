package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ImportHelper {

    ImportHelper() {
        System.out.println("ImportHelper created");
    }

    public void addNewWordType(ActionEvent event) {
        try {
            Button sourceButton = (Button) event.getSource();
            VBox wordTypeRoot = (VBox) sourceButton.getParent().getParent().getParent();
            Parent newWordTypeBox = FXMLLoader.load(getClass().getResource("wordTypeBox.fxml"));
            wordTypeRoot.getChildren().add(newWordTypeBox);
        } catch (IOException ioException) {
            System.out.println("addNewWordType failed");
            System.out.println(ioException);
        }
    }

    public void addNewWordPack(ActionEvent event, VBox wordPackRoot) {
        try {
            Parent newSubPack = FXMLLoader.load(getClass().getResource("wordPackBox.fxml"));
            wordPackRoot.getChildren().add(newSubPack);
        }
        catch (IOException ioException) {
            System.out.println("addNewWordPack failed");
            System.out.println(ioException);
        }
    }

    public void addChildWordPackBox(VBox wordPackRoot) {
        try {
            // Grab the wordPackBox template from resources and make those a child of wordPackRoot
            Parent wordPackBox = FXMLLoader.load(getClass().getResource("wordPackBox.fxml"));
            wordPackRoot.getChildren().add(wordPackBox);
        }
        catch (IOException ioException) {
            System.out.println("There was a problem loading wordPackBox.fxml");
            System.out.println(ioException);
        }
    }

    public void addChildWordTypeBox(VBox wordTypeRoot) {
        try {
            // Grab the wordTypeBox template from resources and make those a child of wordPackRoot
            Parent wordTypeBox = FXMLLoader.load(getClass().getResource("wordTypeBox.fxml"));
            wordTypeRoot.getChildren().add(wordTypeBox);
        }
        catch (IOException ioException) {
            System.out.println("There was a problem loading wordTypeBox.fxml");
            System.out.println(ioException);
        }
    }

    public void addChildSavedFormsParent(VBox wordFormsRoot) {

        int offset = 0;
        // In the case that the wordFormsRoot has an empty spacer at the top,
        // we have to add children at a different index (add at 1, instead of 0 since the spacer is at 0).
        for (Node child : wordFormsRoot.getChildren()) {
            if (child.getId().equals("emptySpace")) {
                offset = 1;
            }
        }
        try {
            // Grab the newFormsParent template from resources and make those a child of wordFormsRoot
            Parent savedFormsParent = FXMLLoader.load(getClass().getResource("savedFormsParent.fxml"));
            wordFormsRoot.getChildren().add(0 + offset, savedFormsParent);
        }
        catch (IOException ioException) {
            System.out.println("There was a problem loading savedFormsParent.fxml");
            System.out.println(ioException);
        }
    }

}
