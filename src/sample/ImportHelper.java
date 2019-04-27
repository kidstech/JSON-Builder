package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ImportHelper {

    ImportHelper() {
        System.out.println("ImportHelper created");
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

    public void addChildNewFormsParent(VBox wordFormsRoot) {
        try {
            // Grab the newFormsParent template from resources and make those a child of wordFormsRoot
            Parent newFormsParent = FXMLLoader.load(getClass().getResource("newFormsParent.fxml"));
            wordFormsRoot.getChildren().add(newFormsParent);
        }
        catch (IOException ioException) {
            System.out.println("There was a problem loading newFormsParent.fxml");
            System.out.println(ioException);
        }
    }

    public void setTopEnabledButton(Node child) {
        for (Node subchild : ((AnchorPane) child).getChildren()) {
            // If it's the RadioButton that sets enabled...
            if (subchild.getId().equals("Enabled")) {
                ((RadioButton) subchild).setSelected(false); // set enabled to false
            }
            // If it's the RadioButton that sets disabled...
            if (subchild.getId().equals("Disabled")) {
                ((RadioButton) subchild).setSelected(true); // set disabled to true
            }
        }
    }


}
