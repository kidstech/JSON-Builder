// Note to developer: As of writing this comment, Gluon makes a version of scene builder that I've been using.

package sample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class Controller {

    @FXML
    AnchorPane topParent;

    String contextPackName;
    String iconPath = "not set";
    Boolean isEnabled = true;

    public String[] createWordFormArray(String rawWordForms) {
        return rawWordForms.split(",");
    }


    public void chooseDir(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        iconPath = chooser.showDialog(null).toString();
        System.out.println(iconPath);
    }


    public void setIsEnabled (ActionEvent event) {
        if ( ((RadioButton) event.getSource()).getId().equals("enabled") ) {
            isEnabled = true;
        } else {
            isEnabled = false;
        }
    }


    // Creates a new Word-Form TextField.
    public void addNewWord(ActionEvent event) throws IOException {
        Object sourceButton = event.getSource();
        VBox parent = (VBox) ((Button) sourceButton).getParent();
        Parent newWordBox = FXMLLoader.load(getClass().getResource("wordForms.fxml"));
        parent.getChildren().add(newWordBox);
    }


    // Creates a new Word-Type field (including a word-form field)
    public void addNewWordType(ActionEvent event) throws IOException {
        Object sourceButton = event.getSource();
        VBox parent = (VBox) ((Button) sourceButton).getParent().getParent().getParent();
        Parent newWordPack = FXMLLoader.load(getClass().getResource("wordTypeBox.fxml"));
        parent.getChildren().add(newWordPack);
    }


    // Creates a new WordPack (including word-type / word-form fields)
    public void addNewWordPack(ActionEvent event) throws IOException {
        Object sourceButton = event.getSource();
        VBox parent = (VBox) ((Button) sourceButton).getParent().getParent().getParent();
        Parent newSubPack = FXMLLoader.load(getClass().getResource("wordPackBox.fxml"));
        parent.getChildren().add(newSubPack);
    }


    // Exports the current entries into a JSON file
    @FXML
    public void exportContextPack(ActionEvent event) throws IOException {

        Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
        Map contextPackMap = new HashMap();

        // We start at the top level (The Context-Pack Level).
        // Icon/enabled are set first.
        contextPackMap.put("icon", iconPath);
        contextPackMap.put("enabled", isEnabled);

        // Now we start to set the other values. Use event.getSource() to get the button where the event originated.
        // Then, use getParent() to get element containing the button.
        Object sourceButton = event.getSource();
        Parent topAnchor = ((Button) sourceButton).getParent(); // Cast it to a button, so we can getParent().

        // Names like 'topAnchor' are the actual fxID's of the objects they reference. This was done on purpose, so
        // that we can follow along in the scenebuilder, to be sure we are selecting the correct objects.

        // Here we get the name of the context pack.
        for (Node topAnchorChild : topAnchor.getChildrenUnmodifiable()) {
            if (topAnchorChild.getId().equals("contextPackNameField")) {
                contextPackName = ((TextField) topAnchorChild).getText();
                break;
                // We break it there, since we were only looking for one thing.
            }
        }

        // "topParent" is the top of the entire tree. Now we work our way down towards the word-packs.
        Parent topParent =  topAnchor.getParent();
        for (Node topParentChild : topParent.getChildrenUnmodifiable() ) {
            if (topParentChild.getId().equals("topScrollPane")) {
                Node wordPackRoot = ((ScrollPane) topParentChild).getContent();
                for (Node wordPackBox : ((VBox) wordPackRoot).getChildren()) {
                    if (wordPackBox.getId().equals("wordPackBox")) {
                        parseWordPack(wordPackBox, contextPackMap);
                        // The wordPackBox contains everything in a word-pack. We pass the node and our
                        // context pack map into the function to add values.
                    }
                }
            }
        }

        Map treemap = new TreeMap<String, Object>(contextPackMap);
        String jsonFromJavaMap = gsonBuilder.toJson(contextPackMap);
        System.out.println(jsonFromJavaMap);
        String jsonFromJavaMap2 = gsonBuilder.toJson(treemap);

        try (Writer writer = new FileWriter(contextPackName + ".json")) {
            gsonBuilder.toJson(contextPackMap, writer);
        }
    }



    public void parseWordPack(Node wordPackBox, Map contextPackMap) {
        Map wordPackMap = new HashMap();
        String wordPackName = "notFound"; // In case we don't find a name...
        Boolean wordPackEnabled = true; // Set default

        for (Node wordPackChild : ((Parent) wordPackBox).getChildrenUnmodifiable()) {
            // We cast it as a parent, since node has no method to get children.

            if (wordPackChild.getId().equals("wordTypeRoot")) {
                // Now we get the word types.
                parseWordTypes(wordPackChild, wordPackMap);
            }

            else if (wordPackChild.getId().equals("wordPackInfo")) {
                // This is where 'enabled' and the word pack name are contained.

                for (Node infoChild : ((Parent) wordPackChild).getChildrenUnmodifiable()) {
                    if (infoChild.getId().equals("wordPackNameField")) {
                        wordPackName = ((TextField) infoChild).getText();
                        // Found the word pack name.
                    }

                    else if (infoChild.getId().equals("wordPackDisabled")) {
                        // Found the 'disabled' radio button.
                        if (((RadioButton) infoChild).isSelected()) {
                            wordPackEnabled = false;
                        }
                    }
                    else if (infoChild.getId().equals("wordPackEnabled")) {
                        // Found the 'enabled' radio button
                        if ( ((RadioButton) infoChild).isSelected()) {
                            wordPackEnabled = true;
                        }
                    }
                    wordPackMap.put("enabled", wordPackEnabled); // Set the 'enabled' value.
                }
            }
        }
        // After we deal with word types and word forms, we add everything to our map.
        contextPackMap.put(wordPackName, wordPackMap);
    }



    public void parseWordTypes(Node wordTypeRoot, Map wordPackMap) {
        for (Node rootChild : ((Parent) wordTypeRoot).getChildrenUnmodifiable()) {

            if (rootChild.getId().equals("wordTypeBox")) {
                String wordType = "notFound";
                for (Node boxChild : ((Parent) rootChild).getChildrenUnmodifiable()) {
                    if (boxChild.getId().equals("wordTypeInfo")) {

                        for (Node infoChild : ((VBox) boxChild).getChildren()) {
                            if (infoChild.getId().equals("chooseWordType")) {
                                wordType = ((TextField) infoChild).getText();
                                System.out.println("FOUND WORD TYPE = " + wordType);
                            }
                        }
                    }
                    else if (boxChild.getId().equals("wordFormRoot")) {
                        wordPackMap.put(wordType, parseWordForms(boxChild));
                        System.out.println("wordPackMap updated to = " + wordPackMap);
                    }
                }
            }
        }
    }

    public ArrayList<ArrayList<String>> parseWordForms(Node wordFormRoot) {

        // This list will be used to hold a base word / word forms.
        ArrayList<String> singleWordForms;

        // This list of lists will hold all the smaller lists.
        ArrayList<ArrayList<String>> allWordsForms = new ArrayList<>();

        for (Node rootChild : ((Parent) wordFormRoot).getChildrenUnmodifiable()) {
            if (rootChild.getId().equals("wordForms")) {
                singleWordForms = new ArrayList<>();
                // Empty list to add base word / forms

                String[] formsArray = ((TextField) rootChild).getText().split(",[ ]*");
                for (String form : formsArray) {
                    singleWordForms.add(form);
                }
                allWordsForms.add(singleWordForms); // add the smaller list to larger list.
            }
            System.out.println("allWordsForms updated to " + allWordsForms);
        }
        System.out.println("COMPLETE: allWordsForms = " + allWordsForms);
        return allWordsForms;
    }

}












