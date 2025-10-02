# student-tracker

An application that allows Kumon center owners to track which students are in their center and log the time they spent working there.

## Description

An application that will allow Kumon center owners to track which students are in their center and log the time they spent working there.

### Stack

- Java Maven for project management
- JavaFX for frontend
- JDBC/SQLite for database
- **Deployment tool TBD**

## Contribute

### Setting up development environment (For Windows 10)

- Install Maven 3.9.10
- Make sure you are using Java 21. If you're not sure if you have it setup properly, look at the bullets below
  - Identify where you `jdk-21` folder is (Typically in `C:\Program Files\Java`)
  - Look up "environment variables" in the Windows search bar and select "Edit the system environment variables"
  - Select "Environment Variables"
  - Under "System Variables", if `JAVA_HOME` does not already exist, create it and set it to your `jdk-21`'s path. Save it.
  - Under "System Variables" click on "Path". If `%JAVA_HOME%/bin` is not already in the list, click "new" then add it and save it.
  - Select "ok" on all dialogs
- Pull project from repository
- Open IDE (Any IDE compatible with Maven works)

NOTE: Soon to be rewritten to be easier to setup

### Deploying

This section will be written in a stream-of-consciousness style to document where we're at in this process.

Currently, I want to use jpackage to deploy the application. It should be done via this command:

`mvn clean verify jpackage:jpackage`

`java -p . --add-modules javafx.base,javafx.fxml,javafx.controls,javafx.graphics,io.leangen.geantyref,org.jdbi.v3.core,org.jdbi.v3.sqlobject,org.slf4j,org.xerial.sqlitejdbc --module student.tracker/io.github.unchangingconstant.studenttracker.Launcher`

**Update 1:**

However, the created .exe does not run. When I run it from command line it does not even give any output. My theory is that the binaries that JavaFX requires aren't being added to the module path, so the application can't start. I'm going to try and solve this by manually putting all the required binaries into a `mods` folder in this project and setting it as the module path in the jpackage plugin's configuration in the pom.

If this doesn't work, my next theory is that the database is not being successfully initialized. But this is unlikely. (Not unlikely for the database to be completely non-functional, just unlikely for it to be the cause of this particular problem.)

**Update 2:**

I seperated the main class from the Application class and the `.exe` now triggers a pop-up which says "Failed to start JVM". However, if I run the app using the commandline, the fatal error is a failure to setup the database! So, no more "JavaFX runtime components can't be found" errors!

As a personal reminder to myself: We basically need 3 components to deploy this: A custom runtime (To avoid depending on the installer machine for one), all dependencies, and the JavaFX binaries (which need to be put into the module path)

**Update 3:**

I added some sketchy database creation code so that the deployed application's database would work. The .exe still gives the same error as last time. But, if I run the code via command line with deployed/app/\* as the classpath, it works with some warning messages. So, either the .exe is executing with the wrong module/class path configurations or there is something wrong with the custom runtime.

My guess is, whatever the issue is, the fix will have to be done from the pom.xml.

**Update 4: IT WORKS!**

Just as I thought, it was a module path issue. Here's a history of why this took so long to figure out:

Originally, I thought JavaFX runtime components were JavaFX .jars that needed to be placed in module path. So, my first solution was to try and run the application as a modular Java application, which meant specifying a module path in the jpackage plugin.

However, specifying a module path and running the application from a main jar (In other words, trying to run it with both modular and non-modular components) created a bunch of issues.

So, I tried to run it completely as a modular application. I used the --module option to invoke my launcher class and manually added each dependency as a module via the --add-modules option that jpackage provides. The issue is, jpackage uses jlink under the hood, and jlink hates automatic modules (Whenever a jar does not have a module-info.java file and it is invoked as a module, it runs as an automatic module). So, it refused to create a runtime image.

Now, anytime I deployed the project and it didn't work, I went into the /app directory of the application and tried manually running it using the java command line tool. What I discovered is that it works (with some warnings) if you just dump everything into class path and run it non-modularly.

Additionally, I came to the realization that jpackage options essentially mirror regular JVM command line tools. So, all I had to do was get the packaged application to run itself with `java -cp app/* my.package.Launcher`. I got rid of any mention of modules from my jpackage plugin configuration and voila! The .exe worked!

I'm keeping this branch along with all comments for future reference.

## Help

Any answers to FAQ will be put here.

## Authors

Me!

## License

No license, for now...
