# student-tracker

An application that allows Kumon center owners to track which students are in their center and log the time they spent working there.

## Description

An application that will allow Kumon center owners to track which students are in their center and log the time they spent working there.

### Stack

- Maven for project management
- JavaFX for GUI
- JDBI/SQLite for database
- Guice for dependency injection
- Mockito / Instancio for testing
- Panteleyev's JPackage plugin for deployment

## Contribute

### Development Environment (Tested on Windows 10 and Linux Mint 22.2)

- Maven 3.9.11
- Java 21
- sqlite3 command line tool (Optional, but useful for testing)

### Deploying

Currently, this package is deployed by copying all dependencies' .jar files into 'target' directory. This is done by reconfiguring the default Maven plugins for Maven's jar and dependency jobs/stages/steps (I don't know the exact Maven lingo for this). This is done so that the jpackage step is simpler.

The jpackage plugin packages everything from the output directory of the jar/dependency plugins. It packages this program as a non-modular application, so all dependencies, modular or not, are run from class path.

Steps to deployment:

- Run this command `mvn clean verify jpackage:jpackage`
- Look at the output directory for the jpackage plugin (Currently target/distribution)
- To run it on Windows 10 or Linux Mint 22.2:
  - In StudentTrackerApp, double click the deployed.exe to run your program. This should run on any Windows machine.
  - In StudentTrackerApp/bin, double click on StudentTrackerApp to run the program. This works on Linux Mint 22.2

Currently, no installers are created by these steps, just a portable application.

## Help

Any answers to FAQ will be put here.

## Authors

Me!

## License

No license, for now...
