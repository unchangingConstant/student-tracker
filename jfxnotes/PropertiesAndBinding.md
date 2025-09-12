# JavaFX Scene Graph

## Overview

The JavaFX scene graph maintains an internal model of all graphical objects in your application. At any given time, it knows what objects to display, what areas of the screen need repainting, and how to render it all in the most efficient manner. Instead of invoking primitive drawing methods directly, you instead use the scene graph API and let the system automatically handle the rendering details.

The JavaFX scene graph is the starting point for constructing a JavaFX app. The scene graph is a hierarchical tree of nodes that represents all visual elements of the application's UI. It can handle input and be rendered.

Each node of a graph has an id, style class, and bounding volume except for the root node. Each has exactly one parent except for the root node.

The `javafx.scene` package defines more than a dozen classes, but three in particular are most important when it comes to learning how the API is structured:

- Node: The abstract base class for all scene graph nodes.

- Parent: The abstract base class for all branch nodes. (This class directly extends Node).

- Scene: The base container class for all content in the scene graph.

## Properties and Binding

JavaFX properties are often used in conjunction with binding, a mechanism for expressing direct relationships between variables. When objects participate in bindings, changes made to one object will automatically be reflected in another object.

Bindings are assembled from one or more sources, known as dependencies. A binding observes its list of dependencies for changes, and then updates itself automatically after a change has been detected.

### Brief JavaBeans overview:

Java makes it possible to encapsulate data within an object and it does not require any specific naming convention when creating methods. read_first(), firstName(), getFN() are all possible but not necessarily meaningful to other devs.

The JavaBeans component architecture addressed this problem by defining some simple naming conventions that bring consistency across projects. In JavaBeans programming, the full signatures for these methods would be: public void setFirstName(String name), public String getFirstName(), public void setLastName(String name), and public String getLastName(). This naming pattern is easily recognizable, both to human programmers and to editing tools, such as the NetBeans IDE. In JavaBeans terminology, the Person object is said to contain firstName and lastName properties.

The JavaBeans model also provides support for complex property types, plus an event delivery system. It also contains a number of support classes, all in the `java.beans` package.

UI state management in JavaFX is facilitated by JavaBeans. Learn it well!

Here's an example of JavaBeans in action:

```java
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

class Bill {

    // Define a variable to store the property
    private DoubleProperty amountDue = new SimpleDoubleProperty();

    // Define a getter for the property's value
    public final double getAmountDue(){return amountDue.get();}

    // Define a setter for the property's value
    public final void setAmountDue(double value){amountDue.set(value);}

     // Define a getter for the property itself
    public DoubleProperty amountDueProperty() {return amountDue;}

}
```

Here is an EventListener for the Bill in action:

```java
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;

public class Main {

    public static void main(String[] args) {

      Bill electricBill = new Bill();

        electricBill.amountDueProperty().addListener(new ChangeListener(){
        @Override public void changed(ObservableValue o,Object oldVal,
                 Object newVal){
             System.out.println("Electric bill has changed!");
        }
      });

      electricBill.setAmountDue(100.00);

    }
}
```

Now, how can this technology be applied in FX?

## Property Binding

We will use `javafx.beans.binding.NumberBinding`, a part of the high-level binding API, Fluent API, to bind properties together.

This example illustrates exactly how you can use it:

```java
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.binding.NumberBinding;

public class Main {

    public static void main(String[] args) {
        IntegerProperty num1 = new SimpleIntegerProperty(1);
        IntegerProperty num2 = new SimpleIntegerProperty(2);
        NumberBinding sum = num1.add(num2);
        System.out.println(sum.getValue());
        num1.set(2);
        System.out.println(sum.getValue());
    }
}
```

This will print `4`, despite the fact that we never explicitly updated `sum`.

## Observables

This section will go over `Observable`, `ObservableValue`, `InvalidationListener`, and `ChangeListener`.

The binding API defines a set of interfaces that enable objects to be notified when a value change or invalidation takes place. The Observable and ObservableValue interfaces fire the change notifications, and the InvalidationListener and ChangeListener interfaces receive them. The difference between the two is that ObservableValue wraps a value and fires its changes to any registered ChangeListener, whereas Observable (which does not wrap a value) fires its changes to any registered InvalidationListener.

Using our `Bill` class from earlier examples:

```java
public class Main {

    public static void main(String[] args) {

        Bill bill1 = new Bill();
        Bill bill2 = new Bill();
        Bill bill3 = new Bill();

        NumberBinding total =
        Bindings.add(bill1.amountDueProperty().add(bill2.amountDueProperty()),
              bill3.amountDueProperty());

        total.addListener(new InvalidationListener() {

        @Override public void invalidated(Observable o) {
                System.out.println("The binding is now invalid.");
            }
        });

        // First call makes the binding invalid
        bill1.setAmountDue(200.00);

        // The binding is now invalid
        bill2.setAmountDue(100.00);
        bill3.setAmountDue(75.00);

        // Make the binding valid...
        System.out.println(total.getValue());

        // Make invalid...
        bill3.setAmountDue(150.00);

        // Make valid...
        System.out.println(total.getValue());
    }
}
```

Why is validity important? I don't know, you tell me.
