# student-tracker

An application that allows Kumon center owners to track which students are in their center and log the time they spent working there.

## Description

An application that will allow Kumon center owners to track which students are in their center and log the time they spent working there.

### Stack

- Java Maven for project management
- JavaFX for frontend
- JDBC/SQLite for database
- Panteleyev's JPackage plugin for deployment

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

Currently, this package is deployed by copying all dependencies' .jar files into target directory. This is done by reconfiguring the default Maven plugins for Maven's jar and dependency jobs/stages/steps (I don't know the exact Maven lingo for this). This is done so that the jpackage step is simpler

The jpackage plugin packages everything from the output directory of the jar/dependency plugins. It packages this program as a non-modular application, so all dependencies, modular or not, are run from class path.

Steps to deployment:

- Run this command `mvn clean verify jpackage:jpackage`
- Look at the output directory for the jpackage plugin (Currently target/mods/deployed)
- In deployed/app, double click the deployed.exe to run your program. This should run on any Windows machine.

Currently, no installers are created by these steps, just a portable application.

## Help

Any answers to FAQ will be put here.

## Authors

Me!

## License

No license, for now...
