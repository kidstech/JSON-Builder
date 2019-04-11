// Note to developer: As of writing this comment, Gluon makes a version of scene builder that I've been using.

package sample;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.sql.SQLOutput;
import java.util.*;
import java.util.function.Predicate;

public class Controller {

    @FXML
    AnchorPane topParent;
    Stage primaryStage;

    String qwe = "{ \"first\": \"first value\", \"second\": \"second value\" }";
    String contextPackName;
    String iconPath = "not set"; // directory, not a file
    Boolean isEnabled = true;  // top-level (contextPack) enabled

    Map contextPackMap = new HashMap();



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

    public void saveWordForms(ActionEvent event) throws IOException {

        String forms = "forms"; // To hold the word forms saved
        Button saveButton = (Button) event.getSource(); // source button
        HBox newFormsParent = (HBox) saveButton.getParent();
        // Parent gets casted to HBox, so we may modify its children.

        // Here we grab the word forms from the textfield and store them for later.
        for (Node child : newFormsParent.getChildren() )  {
            if (child.getId().equals("wordForms")) {
                forms = ((TextField) child).getText();
                break;
            }
        }

        // Now we have to remove newFormsParent and replace it with savedFormsParent
        // Start at the parent of newFormsParent
        VBox wordFormsRoot = (VBox)newFormsParent.getParent();

        // Since newFormsParent is always the last child, we can delete it by index.
        wordFormsRoot.getChildren().remove(wordFormsRoot.getChildren().size() - 1); // delete newFormsParent

        // Now we create the new node and update it's child to contain our word-forms
        HBox savedFormsParent = FXMLLoader.load(getClass().getResource("savedFormsParent.fxml"));
        for (Node child : savedFormsParent.getChildren()) {
            if (child.getId().equals("savedForms")) {
                ((Label) child).setText(forms);
                System.out.println(forms);
                break;
            }
        }

        // Finally we assign this as a child of wordFormsRoot
        wordFormsRoot.getChildren().add(savedFormsParent);

        // Now it's time to add newFormsParent so we can continue adding word forms
        newFormsParent = FXMLLoader.load(getClass().getResource("newFormsParent.fxml"));
        wordFormsRoot.getChildren().add(newFormsParent);
    }

    public void saveEditedWordForms(ActionEvent event) throws IOException{

        String forms = "forms"; // To hold the word forms saved
        Button saveButton = (Button) event.getSource(); // source button

        // Get the parent and cast it to HBox.
        HBox newFormsParent = (HBox) saveButton.getParent();

        // Here we grab the word forms from the textfield and store them for later.
        for (Node child : newFormsParent.getChildren() )  {
            if (child.getId().equals("wordForms")) {
                forms = ((TextField) child).getText();
                break;
            }
        }

        // Now we have to remove newFormsParent and replace it with savedFormsParent
        // First we'll reset the id, so we can easily find it later.
        newFormsParent.setId("toBeReplaced");

        VBox wordFormsRoot = (VBox)newFormsParent.getParent();
        int indexCounter = 0;
        for (Node child : wordFormsRoot.getChildren() ) {
            if (child.getId().equals("toBeReplaced")) {
                wordFormsRoot.getChildren().remove(indexCounter);
                break;
            }
            indexCounter++;
        }

        // Now we create the new node
        HBox savedFormsParent = FXMLLoader.load(getClass().getResource("savedFormsParent.fxml"));

        // Find the 'savedForms' child of savedFormsParent and set the text the old forms
        for (Node child : savedFormsParent.getChildren()) {
            if (child.getId().equals("savedForms")) {
                ((Label) child).setText(forms);
                break;
            }
        }

        // Finally, update wordFormsRoot children to contain an updated savedFormsParent
        wordFormsRoot.getChildren().add(indexCounter, savedFormsParent);

    }

    public void deleteWordForms(ActionEvent event) {
        // We need to find the element to be deleted
        // Start at the button..
        Button deleteButton = (Button) event.getSource();

        // We go to the button's parent, and mark it for deletion
        HBox savedFormsParent = (HBox) deleteButton.getParent();
        savedFormsParent.setId("toBeDeleted");

        // Now we go to the parent of the element "toBeDeleted" and remove the appropriate child...
        VBox wordFormsRoot = (VBox) savedFormsParent.getParent();
        wordFormsRoot.getChildren().remove(savedFormsParent);

    }

    public void editWordForms(ActionEvent event) {
        String forms = "forms";

        Button editButton = (Button) event.getSource(); // source button
        HBox savedFormsParent = (HBox) editButton.getParent();
        forms = ((Label) savedFormsParent.getChildren().get(0)).getText(); // getting the forms
        savedFormsParent.setId("toBeRemoved");
        VBox wordFormsRoot = (VBox)(savedFormsParent.getParent());

        int indexCount = 0;
        for (Node child : wordFormsRoot.getChildren()) {
            if (child.getId().equals("toBeRemoved")) {
                wordFormsRoot.getChildren().remove(indexCount);
                break;
            }
            indexCount++;
        }

        // Now we replace them with a textfield
        // An IOException may be thrown...
        try {
            //Exception can get thrown on the line below
            HBox newFormsParent = FXMLLoader.load(getClass().getResource("tempNewFormsParent.fxml"));

            // Now find the wordForms TextField in the new element, and place the old word forms in there
            for (Node child : newFormsParent.getChildren()) {
                if (child.getId().equals("wordForms")) {
                    ((TextField) child).setText(forms);

                    // Once done, the new element to the wordFormsRoot children and break the loop
                    wordFormsRoot.getChildren().add(indexCount, newFormsParent);
                    break;
                }
            }
        }
        catch (IOException ioException) {
            System.out.println("There was a problem loading \"newFormsParent.fxml\". ");
        }

    }


    ///////////////////////////////////////////////
    //////////    Export Functions   //////////////
    ///////////////////////////////////////////////


    // Exports the current entries into a JSON file
    @FXML
    public void exportContextPack(ActionEvent event) throws IOException {

        Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();

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
                    else if (boxChild.getId().equals("wordFormsRoot")) {
                        wordPackMap.put(wordType, parseWordForms(boxChild)); // see function below...
                        System.out.println("wordPackMap updated to = " + wordPackMap); // for debugging
                    }
                }
            }
        }
    }

    public ArrayList<ArrayList<String>> parseWordForms(Node wordFormsRoot) {

        // This list will be used to hold a base word / word forms.
        ArrayList<String> singleWordForms;

        // This list of lists will hold all the smaller lists.
        ArrayList<ArrayList<String>> allWordsForms = new ArrayList<>();

        for (Node rootChild : ((Parent) wordFormsRoot).getChildrenUnmodifiable()) {
            if (rootChild.getId().equals("savedFormsParent"))  {
                for (Node newFormsChild : ((Parent) rootChild).getChildrenUnmodifiable()) {
                    if (newFormsChild.getId().equals("savedForms")) {
                        if ( ((Label) newFormsChild).getText().isEmpty() ) {
                            System.out.println("Found an empty one");
                            continue;
                        }
                        singleWordForms = new ArrayList<>();

                        String[] formsArray = ((Label) newFormsChild).getText().split(",[ ]*");
                        for (String form: formsArray) {
                            singleWordForms.add(form);
                        }
                        allWordsForms.add(singleWordForms);
                        System.out.println("allWordsForms updated to " + allWordsForms);
                    }
                }
            }
        }
        System.out.println("COMPLETE: allWordsForms = " + allWordsForms);
        return allWordsForms;
    }


    ////////////////////////////////////////////////
    /////////////   Import Functions   /////////////
    ////////////////////////////////////////////////


    public void importJson(ActionEvent event)  {
        try {
            Gson gson = new Gson();
            Map contextPackMap = gson.fromJson(new FileReader(chooseJsonFile()), Map.class);
            // Here, we are converting a json file into a hashmap, which makes it easy to deal with later.
            analyzeJSON(contextPackMap);

            Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create(); // for debugging
            String jsonFromJavaMap = gsonBuilder.toJson(contextPackMap); // for debugging

            resetScene(event);

            System.out.println(jsonFromJavaMap);

        }
        catch (Exception fnfe ) {
            System.out.println("Welp, that sucks.... something broke. ");
        }
    }

    // This function is only used by importJson().
    public File chooseJsonFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a JSON file");

        // this just sets the dialog directory to whatever the project folder is
        fileChooser.setInitialDirectory( new File(System.getProperty("user.dir")) );
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

    // This gives us a new scene to work in our
    public void resetScene(ActionEvent event) throws IOException{

        Parent freshTemplate = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene freshScene = new Scene(freshTemplate);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(freshScene);

        window.show();
    }

    public void analyzeJSON(Map json) {
        System.out.println(iconPath);
        iconPath = (String)json.get("icon");
        System.out.println(iconPath);
    }


}












