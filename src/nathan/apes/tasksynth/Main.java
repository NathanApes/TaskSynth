package nathan.apes.tasksynth;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nathan.apes.roots.definer.Property;
import nathan.apes.roots.definer.PropertyExecute;
import nathan.apes.roots.executor.Executor;
import nathan.apes.roots.grouper.Grouper;
import nathan.apes.roots.grouper.GrouperAdder;
import nathan.apes.roots.initiator.Initiator;
import nathan.apes.tasksynth.backend.automation.TaskAutomation;
import nathan.apes.tasksynth.backend.synthing.Assesser;
import nathan.apes.tasksynth.backend.synthing.Classifier;
import nathan.apes.tasksynth.backend.synthing.Dictionary;

import java.io.*;
import java.util.ArrayList;

public class Main extends Application {

    private PrimaryGUI primaryGUI;

    public static Initiator windowUtil;
    public static Initiator tasksaveUtil;
    public static Initiator tasklistUtil;

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent windowLayout = fxmlLoader.load(getClass().getResource("primarygui.fxml"));
        primaryGUI = fxmlLoader.getController();

        primaryStage.setTitle("TaskSynth");
        primaryStage.setScene(new Scene(windowLayout, 800, 500));
        primaryStage.show();

        setUtilFunctions();
        runStartUp();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void setUtilFunctions(){
        windowUtil = new Initiator("WindowOpener", new Grouper("WindowData"), new Grouper("Opener"));
        windowUtil.getQualities().toGrouperAdder(windowUtil.getQualities().getIdentity()).addAllProperty(
            new ArrayList(){{
                add(new Property("WindowFile", null));
                add(new Property("WindowBoundX", null));
                add(new Property("WindowBoundY", null));
                add(new Property("WindowName", null));
                add(new Property("WindowObject", new Stage()));
            }}
        );
        windowUtil.getRuntimeTasks().toGrouperAdder(windowUtil.getRuntimeTasks().getIdentity()).addProperty(
            new Property("Executor", new Executor("OpenProcess", new ArrayList(){{
                    add(new PropertyExecute("Process",
                        () -> {
                            Parent layout = null;
                            try {
                                layout = FXMLLoader.load(getClass().getResource(("ui/" + windowUtil.getQualities().getProperty("WindowFile").getValue())));
                            } catch (IOException e){ e.printStackTrace(); }
                            Stage window = ((Stage)windowUtil.getQualities().getProperty("WindowObject").getValue());
                            window.setScene(new Scene(layout,
                                ((double)windowUtil.getQualities().getProperty("WindowBoundX").getValue()),
                                ((double)windowUtil.getQualities().getProperty("WindowBoundY").getValue())
                            ));
                            window.setTitle(((String)windowUtil.getQualities().getProperty("WindowName").getValue()));
                            if(!window.getModality().equals(Modality.APPLICATION_MODAL))
                                window.initModality(Modality.APPLICATION_MODAL);
                            window.show();
                        }
                    ));
                }}
            ))
        );

        tasksaveUtil = new Initiator("SaveReader", new Grouper("ReaderData"), new Grouper("Reader"));

        tasksaveUtil.getQualities().toGrouperAdder(tasksaveUtil.getQualities().getIdentity()).addProperty(new Property("ContentGrouperIn", new GrouperAdder("ContentGrouper")));
        tasksaveUtil.getQualities().toGrouperAdder(tasksaveUtil.getQualities().getIdentity()).addProperty(new Property("ContentGrouperOut", new GrouperAdder("ContentGrouper")));
        tasksaveUtil.getQualities().toGrouperAdder(tasksaveUtil.getQualities().getIdentity()).addProperty(new Property("IsEdit", false));
        tasksaveUtil.getQualities().toGrouperAdder(tasksaveUtil.getQualities().getIdentity()).addProperty(new Property("IsHiding", false));
        tasksaveUtil.getQualities().toGrouperAdder(tasksaveUtil.getQualities().getIdentity()).addProperty(new Property("HideTasks", new GrouperAdder("Tasks")));

        tasksaveUtil.getRuntimeTasks().toGrouperAdder(tasksaveUtil.getQualities().getIdentity()).addProperty(
            new Property("Executor",
                new Executor("ReadingProcess", new ArrayList(){{
                    add(new PropertyExecute("Process",
                        () -> {
                            try {
                                FileInputStream fileIn = new FileInputStream(("src/nathan/apes/tasksynth/resources/task/tasks.txt"));
                                InputStreamReader reader = new InputStreamReader(fileIn);
                                BufferedReader lineReader = new BufferedReader(reader);
                                String line;
                                int lineNumber = 0;
                                while((line = lineReader.readLine()) != null){
                                    lineNumber++;
                                    ((GrouperAdder) tasksaveUtil.getQualities().getProperty("ContentGrouperIn").getValue()).addProperty(new Property("Line-" + lineNumber, line));
                                }
                            } catch (Exception e){ e.printStackTrace(); }
                        }
                    ));
                }}
            ))
        );

        tasksaveUtil.getRuntimeTasks().toGrouperAdder(tasksaveUtil.getQualities().getIdentity()).addProperty(
            new Property("Executor2", new Executor("WritingProcess",
                new ArrayList(){{
                    add(new PropertyExecute("Process",
                        () -> {
                            ((Executor)tasksaveUtil.getRuntimeTasks().getProperty("Executor").getValue()).executeFunctions();
                            ArrayList<Property> writeContent = new ArrayList(){{
                                addAll(((Grouper)tasksaveUtil.getQualities().getProperty("ContentGrouperIn").getValue()).getProperties());
                                if(!((boolean)tasksaveUtil.getQualities().getProperty("IsEdit").getValue()))
                                    addAll(((Grouper) tasksaveUtil.getQualities().getProperty("ContentGrouperOut").getValue()).getProperties());
                                else if(((boolean)tasksaveUtil.getQualities().getProperty("IsEdit").getValue())){
                                    ArrayList<Property> editContent = new ArrayList(){{
                                        addAll(((Grouper)tasksaveUtil.getQualities().getProperty("ContentGrouperOut").getValue()).getProperties());
                                    }};
                                    for (int i = 1; i < 3; i++)
                                        set(indexOf(editContent.get(0)) + i, editContent.get(i));
                                }

                                if(((boolean)tasksaveUtil.getQualities().getProperty("IsHiding").getValue()))
                                    ((Grouper)tasksaveUtil.getQualities().getProperty("HideTasks").getValue()).getProperties().forEach(
                                        taskIndex -> set((((int)taskIndex.getValue()) * 4) + 3, new Property("Line-" + (((((int)taskIndex.getValue()) * 4) + 3) + 1), "hidden: true"))
                                    );
                            }};

                            try {
                                FileOutputStream fileOut = new FileOutputStream(("src/nathan/apes/tasksynth/resources/task/tasks.txt"));
                                OutputStreamWriter writer = new OutputStreamWriter(fileOut);
                                BufferedWriter lineWriter = new BufferedWriter(writer);

                                writeContent.forEach(line -> {
                                    try {
                                        lineWriter.write((String) line.getValue());
                                        if(writeContent.indexOf(line) != writeContent.size() - 1)
                                            lineWriter.newLine();
                                    } catch (IOException e) { e.printStackTrace(); }
                                });
                                lineWriter.flush();
                                fileOut.close();
                                writer.close();
                            } catch (IOException e) { e.printStackTrace(); }
                        }
                    ));
                }}
            ))
        );
        tasklistUtil = new Initiator("ListUtil", new Grouper("ListData"), new Grouper("Lister"));
        tasklistUtil.getQualities().toGrouperAdder("ListData").addProperty(new Property("ListWindow", null));
        tasklistUtil.getRuntimeTasks().toGrouperAdder("Lister").addProperty(new Property("Executor",
            new Executor("ListingProcess",
                new ArrayList(){{
                    add(new PropertyExecute("Process",
                        () -> {
                            ((Executor) tasksaveUtil.getRuntimeTasks().getProperty("Executor").getValue()).executeFunctions();
                            Grouper list = (Grouper) tasksaveUtil.getQualities().getProperty("ContentGrouperIn").getValue();
                            ArrayList<Property> list2 = new ArrayList(){{ addAll(list.getProperties()); }};
                            Scene layout = ((Stage) tasklistUtil.getQualities().getProperty("ListWindow").getValue()).getScene();
                            try {
                                int count = 0;
                                for (int i = 0; i < list2.size(); i++)
                                    if(!((String)list2.get(i).getValue()).contains(":")) {
                                        ((CheckBox) layout.lookup("#" + count)).setText((String) list2.get(i).getValue());
                                        count++;
                                    }
                            } catch (NullPointerException e){}
                        }
                    ));
                }}
            )
        ));
    }

    private void runStartUp(){
        ArrayList<Property> startupTasks = new ArrayList<>();
        ((Executor)tasksaveUtil.getRuntimeTasks().getProperty("Executor").getValue()).executeFunctions();
        ((Grouper)tasksaveUtil.getQualities().getProperty("ContentGrouperIn").getValue()).getProperties().forEach(
            property -> {
                String line = (String) property.getValue();
                if(line.contains("StartUp"))
                    if(line.substring(line.indexOf(":"), line.length()).equalsIgnoreCase("true"))
                        startupTasks.add(property);
            }
        );
        tasksaveUtil.getQualities().setProperty("ContentGrouperIn", new Property("ContentGrouperIn", new GrouperAdder("ContentGrouperIn")));

        startupTasks.forEach(
            task -> {
                //Run Task
            }
        );
    }
}
