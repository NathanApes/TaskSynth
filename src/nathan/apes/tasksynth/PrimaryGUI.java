package nathan.apes.tasksynth;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import nathan.apes.roots.definer.Property;
import nathan.apes.roots.executor.Executor;
import nathan.apes.roots.grouper.GrouperAdder;

import static nathan.apes.tasksynth.Main.tasklistUtil;
import static nathan.apes.tasksynth.Main.tasksaveUtil;
import static nathan.apes.tasksynth.Main.windowUtil;

public class PrimaryGUI {

    @FXML protected void onRunClick(ActionEvent actionEvent){
        windowUtil.getQualities().setProperty("WindowName", new Property("WindowName", "Run Task"));
        windowUtil.getQualities().setProperty("WindowFile", new Property("WindowFile", "rundialog/rundialog.fxml"));
        windowUtil.getQualities().setProperty("WindowBoundX", new Property("WindowBoundX", 300.0));
        windowUtil.getQualities().setProperty("WindowBoundY", new Property("WindowBoundY", 300.0));

        tasklistUtil.getQualities().setProperty("ListWindow", new Property("ListWindow", windowUtil.getQualities().getProperty("WindowObject").getValue()));
        ((Executor) tasklistUtil.getRuntimeTasks().getProperty("Executor").getValue()).executeFunctions();
    }

    @FXML protected void onCreateClick(ActionEvent actionEvent){
        windowUtil.getQualities().setProperty("WindowName", new Property("WindowName", "Create Task"));
        windowUtil.getQualities().setProperty("WindowFile", new Property("WindowFile", "addwindow/addwindow.fxml"));
        windowUtil.getQualities().setProperty("WindowBoundX", new Property("WindowBoundX", 400.0));
        windowUtil.getQualities().setProperty("WindowBoundY", new Property("WindowBoundY", 200.0));
        ((Executor)windowUtil.getRuntimeTasks().getProperty("Executor").getValue()).executeFunctions();
    }

    @FXML protected void onManageClick(ActionEvent actionEvent){
        windowUtil.getQualities().setProperty("WindowName", new Property("WindowName", "Manage Tasks"));
        windowUtil.getQualities().setProperty("WindowFile", new Property("WindowFile", "managewindow/managewindow.fxml"));
        windowUtil.getQualities().setProperty("WindowBoundX", new Property("WindowBoundX", 600.0));
        windowUtil.getQualities().setProperty("WindowBoundY", new Property("WindowBoundY", 290.0));
        ((Executor)windowUtil.getRuntimeTasks().getProperty("Executor").getValue()).executeFunctions();

        tasklistUtil.getQualities().setProperty("ListWindow", new Property("ListWindow", windowUtil.getQualities().getProperty("WindowObject").getValue()));
        ((Executor) tasklistUtil.getRuntimeTasks().getProperty("Executor").getValue()).executeFunctions();
        tasksaveUtil.getQualities().getProperty("ContentGrouperIn").setValue(new GrouperAdder("ContentGrouperIn"));
    }

    @FXML protected void onEntered(KeyEvent keyEvent){
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            String taskInputted = ((TextField)keyEvent.getTarget()).getText();
            //Input into backend function
            ((TextField)keyEvent.getTarget()).setText("What's the plan?");
        }
    }
}