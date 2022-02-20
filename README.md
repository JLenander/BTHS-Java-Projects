OpenJDK version 11.0.9.1, Javafx version 11.0.2

# Instructions for packaging the javafx file
### These are not user instructions but are self reminders.

note launcher class is required.
see [docs link](https://openjfx.io/openjfx-docs/#modular) and [example github](https://github.com/openjfx/samples/tree/master/CommandLine/Non-modular/Maven) for more info

1. go into directory `picturePuzzleJavaFX`
2. To run, `mvn clean javafx:run`
3. To make a jar file, `mvn compile package`
4. jar file under `picturePuzzleJavaFX/shade` is the one that includes the javafx packages.

jar file compilation depends on the *javafx-maven-plugin* as well as the *maven-shade-plugin* in *pom.xml*
jar file needs the *images* directory to be in the same directory as it is passed to.

Image Credit: Nasa, Valve, Nintendo, XCOM, Frontier, u/ryanchatfieldimages