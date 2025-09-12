# FXML Overview

Source: https://docs.oracle.com/javase/8/javafx/api/javafx/fxml/doc-files/introduction_to_fxml.html

## Elements

In FXML, an XML element represents one of the following:

- A class instance
- A property of a class instance
- A "static" property
- A "define" block
- A block of script code (Likely won't discuss this)

### Class Instance Elements

#### Instance Declarations

An element's tag is considered an instance declaration if the tag begins with uppercase letter (and the class is imported) or, as in Java, it denotes a fully-qualified (including the package name) name of a class. When the FXML loader encounters such an element, it creates an instance of that class. Example:

```xml
<?import javafx.scene.control.Label?>
<Label text="Hello, World!"/>
```

Note that the Label’s "text" property in this example is set using an XML attribute. (Read up on the Properties and Bindings doc, has to do with Beans and stuff)

**Maps** - Internally, the FXML loader uses an instance of `com.sun.javafx.fxml.BeanAdapter` to wrap an instantiated object and invoke its setter methods. This (currently) private class implements the `java.util.Map` interface and allows a caller to get and set Bean property values as key/value pairs.

If an element represents a type that already implements Map (such as `java.util.HashMap`), it is not wrapped and its get() and put() methods are invoked directly. Example:

```xml
<HashMap foo="123" bar="456"/>
```

- `fx:value`: The `fx:value` attribute can be used to initialize an instance of a type that does not have a default constructor but provides a static `valueOf(String)` method. For example, `java.lang.Strin`g as well as each of the primitive wrapper types define a `valueOf()` method and can be constructed in FXML as follows:

```xml
<String fx:value="Hello, World!"/>
<Double fx:value="1.0"/>
<Boolean fx:value="false"/>
```

Custom classes that define a static `valueOf(String)` method can also be constructed this way.

- `fx:factory`: The `fx:factory` attribute is another means of creating objects whose classes do not have a default constructor. The value of the attribute is the name of a static, no-arg factory method for producing class instances. For example, the following markup creates an instance of an observable array list, populated with three string values:

```xml
<FXCollections fx:factory="observableArrayList">
    <String fx:value="A"/>
    <String fx:value="B"/>
    <String fx:value="C"/>
</FXCollections>
```

**Builders** - This is another way of creating class instances that do not adhere to Bean conventions.

Builder support in FXML is provided by two interfaces. The `javafx.util`.Builder interface defines a single method named `build()` which is responsible for constructing the actual object:

```java
public interface Builder<T> {
    public T build();
}
```

A javafx.util.BuilderFactory is responsible for producing builders that are capable of instantiating a given type:

```java
public interface BuilderFactory {
    public Builder<?> getBuilder(Class<?> type);
}
```

A default builder factory, `JavaFXBuilderFactory`, is provided in the javafx.fxml package. This factory is capable of creating and configuring most immutable JavaFX types. For example, the following markup uses the default builder to create an instance of the immutable `javafx.scene.paint.Color` class:

<Color red="1.0" green="0.0" blue="0.0"/>

Note that, unlike Bean types, which are constructed when the element's start tag is processed, objects constructed by a builder are not instantiated until the element's closing tag is reached. This is because all of the required arguments may not be available until the element has been fully processed. For example, the Color object in the preceding example could also be written as:

```xml
<Color>
    <red>1.0</red>
    <green>0.0</green>
    <blue>0.0</blue>
</Color>
```

The Color instance cannot be fully constructed until all three of the color components are known.

When processing markup for an object that will be constructed by a builder, the Builder instances are treated like value objects - if a Builder implements the Map interface, the put() method is used to set the builder's attribute values. Otherwise, the builder is wrapped in a BeanAdapter and its properties are assumed to be exposed via standard Bean setters.

#### Tags for Class Instance Elements

**<fx:include>** - This tag creates an object from FXML markup defined in another file. It is used as follows:

```xml
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<VBox xmlns:fx="http://javafx.com/fxml">
    <children>
        <fx:include source="my_button.fxml"/>
    </children>
</VBox>
```

Values that begin with a leading slash character are treated as relative to the classpath. Values with no leading slash are considered relative to the path of the current document.

The resulting scene graph contains a VBox as a root object with a single Button as a child node.

### Property Elements
