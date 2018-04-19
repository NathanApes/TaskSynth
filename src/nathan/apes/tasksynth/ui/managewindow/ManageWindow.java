package nathan.apes.tasksynth.ui.managewindow;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nathan.apes.roots.definer.Property;
import nathan.apes.roots.executor.Executor;
import nathan.apes.roots.grouper.Grouper;
import nathan.apes.roots.grouper.GrouperAdder;

import java.util.ArrayList;

import static nathan.apes.tasksynth.Main.tasksaveUtil;
import static nathan.apes.tasksynth.Main.windowUtil;

public class ManageWindow {

    @FXML protected void onAddClick(MouseEvent mouseEvent){
        windowUtil.getQualities().setProperty("WindowName", new Property("WindowName", "Create Task"));
        windowUtil.getQualities().setProperty("WindowFile", new Property("WindowFile", "addwindow/addwindow.fxml"));
        windowUtil.getQualities().setProperty("WindowBoundX", new Property("WindowBoundX", 400.0));
        windowUtil.getQualities().setProperty("WindowBoundY", new Property("WindowBoundY", 200.0));
        ((Executor)windowUtil.getRuntimeTasks().getProperty("Executor").getValue()).executeFunctions();
    }

    @FXML protected void onEditClick(MouseEvent mouseEvent){
        windowUtil.getQualities().setProperty("WindowName", new Property("WindowName", "Edit TaskName"));
        windowUtil.getQualities().setProperty("WindowFile", new Property("WindowFile", "editwindow/editwindow.fxml"));
        windowUtil.getQualities().setProperty("WindowBoundX", new Property("WindowBoundX", 400.0));
        windowUtil.getQualities().setProperty("WindowBoundY", new Property("WindowBoundY", 160.0));

        Scene layout = ((ImageView)mouseEvent.getTarget()).getScene();

        GrouperAdder tasksEdited = new GrouperAdder("EditedTasks");
        int index = 0;
        for(int i = 0; i <= 15; i++)
            if(((CheckBox)layout.lookup("#" + String.valueOf(i))).isSelected() && (!((CheckBox)layout.lookup("#" + String.valueOf(i))).getText().isEmpty())) {
                index = i;
                String text = ((CheckBox) layout.lookup("#" + String.valueOf(i))).getText();
                windowUtil.getQualities().setProperty("WindowName", new Property("WindowName", text));
                tasksEdited.addProperty(new Property(text, text));
            }
        if(tasksEdited.getProperties().size() == 1) {
            ((Executor) windowUtil.getRuntimeTasks().getProperty("Executor").getValue()).executeFunctions();
            ((Executor) tasksaveUtil.getRuntimeTasks().getProperty("Executor").getValue()).executeFunctions();
            Grouper readLines = ((Grouper) tasksaveUtil.getQualities().getProperty("ContentGrouperIn").getValue());
            String trimmed = ((String)readLines.getProperty("Line-" + (((index * 4) + 1) + 1)).getValue()).substring(((String)readLines.getProperty("Line-" + (((index * 4) + 1) + 1)).getValue()).indexOf(":") + 2);
            ((TextField) ((Stage) windowUtil.getQualities().getProperty("WindowObject").getValue()).getScene().lookup("#taskWhen")).setText(trimmed);
            String trimmed2 = ((String) readLines.getProperty("Line-" + ((index * 4) + 3)).getValue()).substring(((String) readLines.getProperty("Line-" + ((index * 4) + 3)).getValue()).indexOf(":") + 2);
            ((CheckBox) ((Stage) windowUtil.getQualities().getProperty("WindowObject").getValue()).getScene().lookup("#startup")).setSelected(Boolean.parseBoolean(trimmed2));
        }
    }
    @FXML protected void onRemoveClick(MouseEvent mouseEvent){
        Scene layout = ((ImageView)mouseEvent.getTarget()).getScene();

        tasksaveUtil.getQualities().getProperty("IsHiding").setValue(true);

        for (int i = 0; i <= 15; i++)
            if(((CheckBox)layout.lookup("#" + String.valueOf(i))).isSelected()){
                ((GrouperAdder)tasksaveUtil.getQualities().getProperty("HideTasks").getValue()).addProperty(new Property("Remove-" + (i + 1), i));
                ((CheckBox) layout.lookup("#" + String.valueOf(i))).setText("");
            }
        ((Executor)tasksaveUtil.getRuntimeTasks().getProperty("Executor2").getValue()).executeFunctions();

        tasksaveUtil.getQualities().getProperty("ContentGrouperIn").setValue(new GrouperAdder("ContentGrouperIn"));
        tasksaveUtil.getQualities().getProperty("ContentGrouperOut").setValue(new GrouperAdder("ContentGrouperOut"));
        tasksaveUtil.getQualities().getProperty("IsHiding").setValue(false);
    }
}
