package nathan.apes.tasksynth.ui.rundialog;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import nathan.apes.roots.definer.Property;
import nathan.apes.roots.grouper.GrouperAdder;

public class RunDialogGUI {

    @FXML protected void onRunSubmit(ActionEvent actionEvent){
        Scene layout = ((Button)actionEvent.getTarget()).getScene();
        GrouperAdder tasksRunned = new GrouperAdder("RunnedTasks");
        for(int i = 0; i <= 15; i++)
            if(((CheckBox)layout.lookup("#" + String.valueOf(i))).isSelected()){
                String text = ((CheckBox)layout.lookup("#" + String.valueOf(i))).getText();
                tasksRunned.addProperty(new Property(text, text));
            }
        //Run tasks by their FunctionNames
    }

    @FXML protected void onCancel(ActionEvent actionEvent){
        ((Button)actionEvent.getTarget()).getParent().getScene().getWindow().hide();
    }
}
