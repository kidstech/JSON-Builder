# JSON-Builder
JavaFx application for creating/modifying JSON context packs

This program is designed to create Contextpacks for the University of Minnesota Morris's StoryBuilder project. It's aim is to be a user-friendly interface to manually enter values to construct a 'ContextPack' in JSON form.

The application was developed in using an interface called 'SceneBuilder' (by Gluon). It's free to download, and is a good alternative to the analog interface that Oracle no longer supports (at least at the time of writing this).

## Dependencies

Parsing the JSON uses a Google library called GSON. As writing this, you can get the .jar file from [this source](https://repo1.maven.org/maven2/com/google/code/gson/gson/2.6.2/).

To add a dependency in IntelliJ, select File > Project Structure. On the left-hand side of the window, select "modules" and then select the "dependencies" tab. On the right-hand side of the dependencies pane, click the '+' icon, and select 'JARs or directories. Then select the JAR file that you downloaded. Once set into the project, you should be able to use GSON to play with Json files.
