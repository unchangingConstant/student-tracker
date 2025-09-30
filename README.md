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

However, the created .exe does not run. When I run it from command line it does not even give any output. My theory is that the binaries that JavaFX requires aren't being added to the module path, so the application can't start. I'm going to try and solve this by manually putting all the required binaries into a `mods` folder in this project and setting it as the module path in the jpackage plugin's configuration in the pom.

If this doesn't work, my next theory is that the database is not being successfully initialized. But this is unlikely. (Not unlikely for the database to be completely non-functional, just unlikely for it to be the cause of this particular problem.)

## Help

Any answers to FAQ will be put here.

## Authors

Me!

## License

No license, for now...
