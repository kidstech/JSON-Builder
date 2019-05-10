// Note to developer: As of writing this comment, Gluon makes a version of scene builder that I've been using.

// Note to the KidsTech team....

/// The default directory that the application exports JSON to is inside the project directory.
/// If this was done from a JAR, it would export to the directory the JAR file was inside.

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
    @FXML
    HBox infoHBox;
    @FXML
    TextField contextPackNameField;
    @FXML
    VBox wordPackRoot;


    Stage primaryStage;

    String qwe = "{ \"first\": \"first value\", \"second\": \"second value\" }";
    String contextPackName;
    String iconPath = "not set"; // directory, not a file
    Boolean isEnabled = true;  // top-level (contextPack) enabled

    Map contextPackMap = new HashMap();

    boolean firstWordFormsDone;


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
    public void addNewWordType(ActionEvent event) {
        try {
            Button sourceButton = (Button) event.getSource();
            VBox wordTypeRoot = (VBox) sourceButton.getParent().getParent().getParent();
            Parent newWordTypeBox = FXMLLoader.load(getClass().getResource("wordTypeBox.fxml"));
            wordTypeRoot.getChildren().add(newWordTypeBox);
        }
        catch (IOException ioException) {
            System.out.println("addNewWordType failed");
            System.out.println(ioException);
        }
    }

    // Creates a new WordPack (including word-type / word-form fields)
    public void addNewWordPack(ActionEvent event) {
        try {
            Parent newSubPack = FXMLLoader.load(getClass().getResource("wordPackBox.fxml"));
            wordPackRoot.getChildren().add(newSubPack);
        }
        catch (IOException ioException) {
            System.out.println("addNewWordPack failed");
            System.out.println(ioException);
        }
    }

    public void saveWordForms(ActionEvent event) throws IOException {

        String forms = "forms"; // To hold the word forms saved
        Button saveButton = (Button) event.getSource(); // source button
        HBox newFormsParent = (HBox) saveButton.getParent();

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
        Parent topAnchor = ((Button) sourceButton).getParent().getParent().getParent(); // Cast it to a button, so we can getParent().

        // Names like 'topAnchor' are the actual fxID's of the objects they reference. This was done on purpose, so
        // that we can follow along in the scenebuilder, to be sure we are selecting the correct objects.

        // Here we get the name of the context pack.
            for (Node child : infoHBox.getChildren() ) {
                if (child.getId().equals("contextPackNameField")) {
                    contextPackName = ((TextField) child).getText();
                    System.out.println(contextPackName);
                    break;
                }
            }


        for (Node wordPackBox : wordPackRoot.getChildren()) {
            if (wordPackBox.getId().equals("wordPackBox")) {
                parseWordPack(wordPackBox, contextPackMap); // See the function below this one...
                // The wordPackBox contains everything in a word-pack. We pass the node and our
                // context pack map into the function to add values.
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


    public void importJson(ActionEvent event)  throws IOException {

        ImportHelper importHelper = new ImportHelper();

        Gson gson = new Gson();

        Map contextPackMap = gson.fromJson(new FileReader(chooseJsonFile()), Map.class);

        // First thing, set the current icon path to what is retrieved from the JSON
        iconPath = (String)contextPackMap.get("icon");

//        Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create(); // for debugging
//        String jsonFromJavaMap = gsonBuilder.toJson(contextPackMap); // for debugging

        // Create a fresh template that we can work with and modify
        Parent freshTemplate = resetScene(event);

        System.out.println(contextPackMap);

        // Here we set the contextPackNameField
        for (Node child : freshTemplate.getChildrenUnmodifiable()) {

        }

//        // If top-level enabled is set to false, then we need to change the radio button.
//        if (contextPackMap.get("enabled").equals(false)) {
//            // Go through the top-level children and find "topAnchor"
//            for (Node child : freshTemplate.getChildrenUnmodifiable()) {
//                if ( child.getId().equals("topAnchor") ) {
//                    // Then call the importHelper to set the enabled button to false.
//                    importHelper.setTopEnabledButton(child);
//                }
//            }
//        }

        for (Node child : freshTemplate.getChildrenUnmodifiable()) {
            if ( child.getId().equals("topAnchor") ) {
                for ( Node topChild : ((AnchorPane) child).getChildren() ) {
                    if (topChild.getId().equals("infoHBox")) {
                        for (Node infoChild : ((HBox) topChild).getChildren()) {
                            if (infoChild.getId().equals("contextPackNameField")) {
                                ((TextField) infoChild).setText(contextPackName);
                            }
                            if (contextPackMap.get("enabled").equals(false) &&
                                    infoChild.getId().equals("enabled")) {
                                ((RadioButton) infoChild).setSelected(false);
                            }
                            if (contextPackMap.get("enabled").equals(false) &&
                                    infoChild.getId().equals("disabled")) {
                                ((RadioButton) infoChild).setSelected(true);
                            }
                        }
                    }
                }
            }
        }

        // We are done dealing with these keys, and removing them makes things less complicate as we iterate through.
        contextPackMap.keySet().remove("enabled");
        contextPackMap.keySet().remove("icon");

        VBox wordPackRoot;
        for (Node child : freshTemplate.getChildrenUnmodifiable()) {
            if (child.getId().equals("topScrollPane")) {
                // For a scrollpane, we have to call getContent instead of getChildren
                wordPackRoot = (VBox) ((ScrollPane) child).getContent();

                // For the number of contextPackMap keys that still exist (should only be WordPackBox keys)
                for (int i=0; i<contextPackMap.keySet().size()-1; i++) {
                    // importHelper will append a wordPackBox as a child
                    importHelper.addChildWordPackBox(wordPackRoot);
                }

                populateWordPacks(wordPackRoot, contextPackMap);
            }
        }

    }

    public void populateWordPacks(VBox wordPackRoot, Map contextPackMap) {

        // In theory, the wordPackRoot's only children are wordPackBoxes, and there should be an equal amount of children
        // as there are keys left in our contextPackMap. We can use a counter to iterate through both at the same time.

        // We should check they are equal though.
        if (contextPackMap.keySet().size() == wordPackRoot.getChildren().size()) {
            System.out.println("contextPackMap keyset is equal to wordPackRoot children size");
        } else {
            System.out.println("ERROR: contextPackMap keyset is NOT equal to wordPackRoot children size");
            return;
        }

        // If they are equal, we move forward...
        // For each wordPack in the map...
        for (int i = 0; i < contextPackMap.keySet().size(); i++) {
            firstWordFormsDone = false;
            System.out.println("first word forms set to false");
            // extract the current key so we can use it later
            String currKey = (String) contextPackMap.keySet().toArray()[i];
            System.out.println("Currkey is " + currKey);
            // Now take that key and make a map out of its values
            Map subPackMap = (Map) contextPackMap.get(currKey);
            System.out.println("Currkey values are " + subPackMap);
            // Since the wordPackRoot only has wordPackBoxes as children, we can do this safely.
            HBox wordPackBox = (HBox) wordPackRoot.getChildren().get(i);

            // Now we check to see if the disabled button needs to be 'checked'
            for (Node child : wordPackBox.getChildren()) {
                if (child.getId().equals("wordPackInfo")) {
                    for (Node subChild : ((VBox) child).getChildren() ) {
                        if (subChild.getId().equals("wordPackNameField")) {
                            System.out.println("Found wordPackNameField");
                            ((TextField) subChild).setText(currKey);
                            System.out.println("wordPack name set to " + currKey);
                        }
                        if (subChild.getId().equals("wordPackEnabled")
                        && subPackMap.get("enabled").equals(false)) {
                            ((RadioButton) subChild).setSelected(false);
                        }
                        if (subChild.getId().equals("wordPackDisabled")
                                && subPackMap.get("enabled").equals(false)) {
                            ((RadioButton) subChild).setSelected(true);
                            System.out.println("Disabled set to true");
                        }
                        else {System.out.println("status is enabled. leaving button alone");}
                    }
                }
                if (child.getId().equals("wordTypeRoot")) {
                    ImportHelper importHelper = new ImportHelper();
                    for (int j=0; j<subPackMap.keySet().size()-2; j++) {
                        // importHelper will append a wordPackBox as a child
                        importHelper.addChildWordTypeBox((VBox) child);
                        System.out.println("added a word type box");
                    }
                    System.out.println("now populating word types...");
                    populateWordTypes((VBox) child, subPackMap);
                }
            }

        }
    }

    public void populateWordTypes(VBox wordTypeRoot, Map subPackMap) {

        System.out.println("populating word types...");

        subPackMap.remove("enabled"); // discard this key, since we don't need it in this scope.
        // Just like in the populateWordPack's method , the wordTypeRoot's only children are wordPackBoxes, and there
        // should be an equal amount of children as there are keys left in our contextPackMap.
        // We can use a counter to iterate through both at the same time.

        // We should check they are equal though.
        if (subPackMap.keySet().size() == wordTypeRoot.getChildren().size()) {
            System.out.println("subPackMap keyset is equal to wordTypeRoot children size");
        } else {
            System.out.println("ERROR: subPackMap keyset is NOT equal to wordTypeRoot children size");
            return;
        }

        // If they are equal, we move forward...
        for (int i = 0; i < subPackMap.keySet().size(); i++) {

            // This switch is needed from the next function (populateWordForms). It needs to be set now, and it will be
            //  be reset many times later (explained in the next function).

            // extract the current key so we can use it later
            String currKey = (String) subPackMap.keySet().toArray()[i];
            System.out.println("Currkey for wordType is " + currKey);

            // Now take that key and make a map out of its values
            ArrayList wordFormsArray = (ArrayList) subPackMap.get(currKey);
            System.out.println("Currkey values are " + wordFormsArray);

            // Since the wordPackRoot only has wordTypeBoxes as children, we can do this safely.
            HBox wordTypeBox = (HBox) wordTypeRoot.getChildren().get(i);
            for (Node child : wordTypeBox.getChildren()) {
                if (child.getId().equals("wordTypeInfo")) {
                    for (Node subChild : ((VBox) child).getChildren() ) {
                        if (subChild.getId().equals("chooseWordType")) {
                            ((TextField) subChild).setText(currKey);
                            System.out.println("Set wordType label to " + currKey);
                        }
                    }
                }
                if (child.getId().equals("wordFormsRoot")) {
                    ImportHelper importHelper = new ImportHelper();
                    for (int j = 0; j < wordFormsArray.size(); j++) {
                        importHelper.addChildSavedFormsParent((VBox) child);
                        System.out.println("adding label for wordForms");
                    }
                    System.out.println("populating word forms...");
                    populateWordForms((VBox) child, wordFormsArray);
                }
            }

        }
    }

    public void populateWordForms(VBox wordFormsRoot, ArrayList<ArrayList<String>> wordFormsArray) {

        // wordFormsRoot always has a generic Pane as it's first child (this is done for spacing)
        // Therefore, we need to check ArrayList and child number equality differently (by using minus 1).

        int boolInt;
        int notBoolInt;

        // if the first word forms of the wordPack are not done...
        if (!firstWordFormsDone) {
            boolInt = 1;
            System.out.println("boolint set to " + boolInt);
        } else {
            boolInt = 0;
            System.out.println("boolint set to " + boolInt);
        }

        System.out.println("Bool int = " + boolInt);

        if (wordFormsArray.size()  == wordFormsRoot.getChildren().size() - (1 + boolInt ) ) {
            System.out.println("equality good");
            System.out.println("Array size: " + wordFormsArray.size()   );
            System.out.println("Children number: " + String.valueOf(wordFormsRoot.getChildren().size() ) );
            System.out.println("minus " + String.valueOf((1 + boolInt)));
        } else {
            System.out.println("ERROR: not equal!");
            System.out.println("Array size: " + wordFormsArray.size() );
            System.out.println("Children number: " + String.valueOf(wordFormsRoot.getChildren().size() ) );
            System.out.println("minus " + String.valueOf((1 + boolInt)) );
            return;
        }

        for (int i = 0; i < wordFormsArray.size(); i++) {

            // The current array of word forms.
            ArrayList<String> currArray = wordFormsArray.get(i);

            // The current newFormsParent that we are putting those forms into
            HBox currFormsParent = (HBox) wordFormsRoot.getChildren().get(i+boolInt);

            for (Node child  : currFormsParent.getChildren() ) {
                if (child.getId().equals("savedForms")) {
                    System.out.println("saved Forms FOUND");
                    String formsString = currArray.toString();
                    formsString = formsString.replace("[", "").replace("]", "");
                    ((Label) child).setText(formsString);
                }
            }
        }
        firstWordFormsDone = true;
        System.out.println("DONE!!!!!!!!!!#######");


    }





//    public void setUpWordPackBox(HBox wordPackBox, Map contextPackMap, String currKey) {
//        for (Node child : wordPackBox.getChildren()) {
//            if (child.getId().equals("wordTypeRoot")) {
//                for (Object contextPackMap.get(currentKey).
//            }
//        }
//    }

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
        String[] fileArray = selectedFile.toString().split("\\\\");
        String fileString = fileArray[fileArray.length-1].replace(".json", "");
        contextPackName = fileString;
        System.out.println("file string is " + fileString);
        if (selectedFile != null) {
            return selectedFile;
        }
        return null;
    }

    // This gives us a new scene to work with our imported JSON
    public Parent resetScene(ActionEvent event) throws IOException{

        // First reset the template
        Parent freshTemplate = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene freshScene = new Scene(freshTemplate);

        // Now make a new window with our new scene
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(freshScene);

        // show the window
        window.show();

        // return the template so we can modify it
        return  freshTemplate;
    }

}












