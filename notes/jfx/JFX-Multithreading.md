*Ripped from the following website: https://docs.oracle.com/javase/8/javafx/interoperability-tutorial/concurrency.htm*

# `javafx.concurrent` Package

The Java platform provides a complete set of concurrency libraries available through the java.util.concurrent package. The javafx.concurrent package leverages the existing API by considering the JavaFX Application thread and other constraints faced by GUI developers.

The javafx.concurrent package consists of the Worker interface and two concrete implementations, Task and Service classes. The Worker interface provides APIs that are useful for a background worker to communicate with the UI. The Task class is a fully observable implementation of the java.util.concurrent.FutureTask class. The Task class enables developers to implement asynchronous tasks in JavaFX applications. The Service class executes tasks.

The WorkerStateEvent class specifies an event that occurs whenever the state of a Worker implementation changes. Both the Task and Service classes implement the EventTarget interface and thus support listening to the state events.

## Worker Interface

The Worker interface defines an object that performs some work on one or more background threads. The state of the Worker object is observable and usable from the JavaFX Application thread.

The lifecycle of the Worker object is defined as follows. When created, the Worker object is in the READY state. Upon being scheduled for work, the Worker object transitions to the SCHEDULED state. After that, when the Worker object is performing the work, its state becomes RUNNING. Note that even when the Worker object is immediately started without being scheduled, it first transitions to the SCHEDULED state and then to the RUNNING state. The state of a Worker object that completes successfully is SUCCEEDED, and the value property is set to the result of this Worker object. Otherwise, if any exceptions are thrown during the execution of the Worker object, its state becomes FAILED and the exception property is set to the type of the exception that occurred. At any time before the end of the Worker object the developer can interrupt it by invoking the cancel method, which puts the Worker object into the CANCELLED state.