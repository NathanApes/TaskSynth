package nathan.apes.tasksynth.ui.editwindow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import nathan.apes.roots.definer.Property;
import nathan.apes.roots.executor.Executor;
import nathan.apes.roots.grouper.Grouper;
import nathan.apes.roots.grouper.GrouperAdder;

import java.util.ArrayList;

import static nathan.apes.tasksynth.Main.tasklistUtil;
import static nathan.apes.tasksynth.Main.tasksaveUtil;
import static nathan.apes.tasksynth.Main.windowUtil;

public class EditWindow {

    @FXML protected void onSubmit(ActionEvent actionEvent){
        Scene layout = ((Button)actionEvent.getTarget()).getParent().getScene();
        ArrayList<Property> input = new ArrayList(){{
            add(new Property("TaskWhat", windowUtil.getQualities().getProperty("WindowName").getValue()));
            add(new Property("TaskWhen", "Time: " + ((TextField)layout.lookup("#taskWhen")).getText()));
            add(new Property("StartUp", "StartUp: " + ((CheckBox)layout.lookup("#startup")).isSelected()));
        }};
        Grouper editedContent = new Grouper("Content", input);
        tasksaveUtil.getQualities().setProperty("ContentGrouperOut", new Property("ContentGrouperOut", editedContent));
        tasksaveUtil.getQualities().setProperty("IsEdit", new Property("IsEdit", true));
        ((Executor)tasksaveUtil.getRuntimeTasks().getProperty("Executor2").getValue()).executeFunctions();
        tasksaveUtil.getQualities().getProperty("ContentGrouperIn").setValue(new GrouperAdder("ContentGrouperIn"));
        tasksaveUtil.getQualities().getProperty("ContentGrouperOut").setValue(new GrouperAdder("ContentGrouperOut"));
        tasksaveUtil.getQualities().getProperty("IsEdit").setValue(false);

        layout.getWindow().hide();
    }

    @FXML protected void onCancel(ActionEvent actionEvent){
        windowUtil.getQualities().setProperty("WindowName", new Property("WindowName", "Manage Tasks"));
        windowUtil.getQualities().setProperty("WindowFile", new Property("WindowFile", "managewindow/managewindow.fxml"));
        windowUtil.getQualities().setProperty("WindowBoundX", new Property("WindowBoundX", 600.0));
        windowUtil.getQualities().setProperty("WindowBoundY", new Property("WindowBoundY", 290.0));
        ((Executor)windowUtil.getRuntimeTasks().getProperty("Executor").getValue()).executeFunctions();
    }
}
