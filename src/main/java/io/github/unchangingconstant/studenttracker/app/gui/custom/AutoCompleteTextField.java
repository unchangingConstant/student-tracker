package io.github.unchangingconstant.studenttracker.app.gui.custom;

import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.util.Callback;

// There has to be a better way oh my god
/*
 * I'd take the example at this link: https://gist.github.com/floralvikings/10290131
 * But that idiot added no support for binding. Wow! I bet his tree set is sooo fucking efficient. Too bad its USELESS
 */
public class AutoCompleteTextField<T> extends TextField {
    
    private final SimpleListProperty<T> items = new SimpleListProperty<>();
    private final ContextMenu optionsPopup = new ContextMenu();

    private Callback<T, String> itemNameFactory;

    public AutoCompleteTextField()  {
        super();
        textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                if (getText().length() == 0)    {
                    optionsPopup.hide();
                } else  {
                    List<String> matches = getAutoCompleteMatches(getText());
                    updateContextMenu(matches);
                }
            }
        });
    }

    // Map an item in the items list to a string
    public void setItemNameFactory(Callback<T, String> itemNameFactory)    {
        this.itemNameFactory = itemNameFactory;
    }

    public SimpleListProperty<T> itemsProperty()    {
        return items;
    }

    private List<String> getAutoCompleteMatches(String pattern)    {
        LinkedList<String> matches = new LinkedList<>();
        items.forEach(item -> {
            String itemString = itemNameFactory.call(item);
            if (itemString.contains(pattern))  {
                matches.add(itemString);
            }
        });
        return matches;
    }

    private void updateContextMenu(List<String> matches) {
        if (matches.size() == 0)   {
            optionsPopup.hide();
        } else  {
            // TODO update popup!
        }
    }

}
