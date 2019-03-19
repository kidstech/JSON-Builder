// Note to developer: As of writing this comment, Gluon makes a version of scene builder that I've been using.

package sample;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jdk.nashorn.internal.parser.JSONParser;

import java.io.*;
import java.util.*;

public class Controller {

    @FXML
    AnchorPane topParent;
    Stage primaryStage;

    String contextPackName;
    String iconPath = "not set"; // directory, not a file
    Boolean isEnabled = true;  // top-level (contextPack) enabled



    //////////////////////////////////////////////////
    /////////////   General Functions   //////////////
    //////////////////////////////////////////////////


    // For choosing the icon directory (not the actual file).
    public void chooseDir(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        iconPath = chooser.showDialog(primaryStage).toString();
        System.out.println(iconPath);
    }

    // For the main context pack. Pushing either Radio button will trigger this and set the "enabled" field
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



    ///////////////////////////////////////////////
    ///////////   Export Functions   //////////////
    ///////////////////////////////////////////////


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
                        parseWordPack(wordPackBox, contextPackMap); // See the function below this one...
                        // The wordPackBox contains everything in a word-pack. We pass the node and our
                        // context pack map into the function to add values.
                    }
                }
            }
        }

        String jsonFromJavaMap = gsonBuilder.toJson(contextPackMap); //Used for debugging
        System.out.println(jsonFromJavaMap); // Used for debugging

        // Now for the actual writing...
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
                parseWordTypes(wordPackChild, wordPackMap); // See function below this one...
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
                String wordType = "notFound"; // in case it never gets set

                for (Node boxChild : ((Parent) rootChild).getChildrenUnmodifiable()) {

                    // This is where we find the word-type field (noun, adjective, etc).
                    if (boxChild.getId().equals("wordTypeInfo")) {
                        for (Node infoChild : ((VBox) boxChild).getChildren()) {
                            if (infoChild.getId().equals("chooseWordType")) {
                                wordType = ((TextField) infoChild).getText();
                                System.out.println("FOUND WORD TYPE = " + wordType); // for debugging
                            }
                        }
                    }

                    // This where we find the subtree for word-forms
                    else if (boxChild.getId().equals("wordFormRoot")) {
                        wordPackMap.put(wordType, parseWordForms(boxChild)); // see function below...
                        System.out.println("wordPackMap updated to = " + wordPackMap); // for debugging
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
            if (rootChild.getId().equals("wordForms"))  {
                if ( ((TextField) rootChild).getText().isEmpty() ) {
                    System.out.println("Found an empty one");
                    continue;
                }
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



    ////////////////////////////////////////////////
    /////////////   Import Functions   /////////////
    ////////////////////////////////////////////////


    public void importJson() throws FileNotFoundException {
        try {
            Gson gson = new Gson();
            Map contextPackMap = gson.fromJson(new FileReader(chooseJsonFile()), Map.class);
            // Here, we are converting a json file into a hashmap, which makes it easy to deal with later.

            Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create(); // for debugging
            String jsonFromJavaMap = gsonBuilder.toJson(contextPackMap); // for debugging
            System.out.println(jsonFromJavaMap); // for debugging
        }
        catch (FileNotFoundException fnfe ) {
            System.out.println("Welp, that sucks.... The file wasn't found! ");
        }
    }

    // This function is only used by importJson().
    public File chooseJsonFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a JSON file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON files", "*.JSON"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
                // optional, just in case real json objects aren't being recognized
        );
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            return selectedFile;
        }
        return null;
    }

}












