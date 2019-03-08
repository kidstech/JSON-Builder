// Note to developer: As of writing this comment, Gluon makes a version of scene builder that I've been using.

package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Collection;
import java.util.Random;

public class Controller {

    @FXML
    AnchorPane topParent;

    @FXML
    public TextField wordBox;

    public void addNewWord(ActionEvent event) throws IOException {
        Object sourceButton = event.getSource();
        VBox parent = (VBox) ((Button) sourceButton).getParent();
        Parent newWordBox = FXMLLoader.load(getClass().getResource("wordBox.fxml"));
        parent.getChildren().add(newWordBox);
    }

    public void addNewWordPack(ActionEvent event) throws IOException {
        Object sourceButton = event.getSource();
        VBox parent = (VBox) ((Button) sourceButton).getParent().getParent().getParent().getParent();
        Parent newWordPack = FXMLLoader.load(getClass().getResource("horiz2.fxml"));
        parent.getChildren().add(newWordPack);
    }

    public void addNewContextPack(ActionEvent event) throws IOException {
        Object sourceButton = event.getSource();
        VBox parent = (VBox) ((Button) sourceButton).getParent().getParent().getParent().getParent().getParent();
        Parent newSubPack = FXMLLoader.load(getClass().getResource("horiz1.fxml"));
        parent.getChildren().add(newSubPack);
    }

    @FXML
    public void createContextPack(ActionEvent event) {

//        Object sourceButton = event.getSource();
//        AnchorPane parent = (AnchorPane) ((Button) sourceButton).getParent().getParent();
//        for (Node child : parent.getChildren()) {
//            if (child.getId() != null) {
//                Node scrollVBox = ((ScrollPane) child).getContent();
//                Node anchor1 = (((VBox) scrollVBox).getChildren().get(0));
//                Node horiz1 = (((AnchorPane) anchor1).getChildren().get(0));
//                for (Node child1 : ((HBox) horiz1).getChildren()) {
//                    if (child1.getId().equals("anchor2")) {
//                        Node ContextPackBlock = ((AnchorPane) child1).getChildren().get(0);
//                        for (Node child2 : ((VBox) ContextPackBlock).getChildren()) {
//                            if (child2.getTypeSelector().equals("TextField")) {
//                                StringProperty x = new SimpleStringProperty(wordBox.getText());
//                                StringProperty y = new SimpleStringProperty( ((TextField) child2).getText() );
//                                x.bindBidirectional(y);
//                            }
//                        }
//                    }
//
//                }
//            }
//        }

    }
}


