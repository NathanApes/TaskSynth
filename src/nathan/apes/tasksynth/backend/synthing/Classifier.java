package nathan.apes.tasksynth.backend.synthing;

import nathan.apes.roots.definer.Property;
import nathan.apes.roots.definer.PropertyExecute;
import nathan.apes.roots.executor.Executor;
import nathan.apes.roots.grouper.Grouper;
import nathan.apes.roots.grouper.GrouperAdder;
import nathan.apes.roots.initiator.Initiator;

import java.io.File;
import java.util.ArrayList;

public class Classifier {

    public static Initiator keyMaker;

    public Classifier(String input){

        keyMaker = new Initiator("KeyMaker", new Grouper("Keys"), new Grouper("Executor"));

        keyMaker.getQualities().toGrouperAdder("Keys").addProperty(new Property("Input", input));
        keyMaker.getQualities().toGrouperAdder("Keys").addProperty(new Property("RefinedFunctions", new GrouperAdder("Functions")));
        keyMaker.getQualities().toGrouperAdder("Keys").addProperty(new Property("RefinedFunctionLocations", new GrouperAdder("Locations")));
        keyMaker.getQualities().toGrouperAdder("Keys").addProperty(new Property("FunctionKeyList", new GrouperAdder("List")));
        keyMaker.getQualities().toGrouperAdder("Keys").addProperty(new Property("CommandResponseKeyList", new GrouperAdder("List")));
        keyMaker.getQualities().toGrouperAdder("Keys").addProperty(new Property("SelectedKey", ""));
        keyMaker.getQualities().toGrouperAdder("Keys").addProperty(new Property("TaskObject", ""));

        ((GrouperAdder)keyMaker.getQualities().getProperty("CommandResponseKeyList").getValue()).addAllProperty(
            new ArrayList(){{
                add(new Property("FileCommands",
                    new GrouperAdder("Commands",
                        new ArrayList(){{
                            add(new Property("Copy", "COPY"));
                            add(new Property("Move", "MOVE"));
                            add(new Property("Rename", "RENAME"));
                        }}
                    )
                ));
                add(new Property("MediaPlaybackCommands",
                    new GrouperAdder("Commands",
                        new ArrayList(){{
                            add(new Property("Play", "PLAY"));
                        }}
                    )
                ));
            }}
        );

        keyMaker.getRuntimeTasks().toGrouperAdder("Executor").addProperty(new Property("ExecutorProcess",
            new Executor("Keyer",
                new ArrayList(){{
                    add(new PropertyExecute("FunctionKeyer",
                        () -> {
                            new Assesser();
                            Assesser.getMachineFunctions().getProperties().forEach(
                                executable -> {
                                    String executableName = ((File)executable.getValue()).getName();
                                    String executableLocation = ((File)executable.getValue()).getAbsolutePath();
                                    String simpleName = executableName.substring(0, executableName.lastIndexOf("."));
                                    Dictionary wordLookup = new Dictionary(simpleName, true);
                                    if(((boolean)wordLookup.wordMeaning.getQualities().getProperty("DidSucceed").getValue())) {
                                        String description = (String) wordLookup.wordMeaning.getQualities().getProperty("OutputDefinition").getValue();
                                        ((GrouperAdder) keyMaker.getQualities().getProperty("RefinedFunctions").getValue()).addProperty(new Property("Function-" + simpleName, description));
                                        ((GrouperAdder) keyMaker.getQualities().getProperty("RefinedFunctionLocations").getValue()).addProperty(new Property("FunctionLocation-" + simpleName, executableLocation));
                                    }
                                }
                            );
                            ((Grouper)keyMaker.getQualities().getProperty("RefinedFunctions").getValue()).getProperties().forEach(
                                function -> {
                                    String description = (String) function.getValue();

                                    ArrayList<Property> functionList = new ArrayList(){{
                                        addAll(((Grouper)keyMaker.getQualities().getProperty("RefinedFunctions").getValue()).getProperties());
                                    }};
                                    ArrayList<Property> locationList = new ArrayList(){{
                                        addAll(((Grouper)keyMaker.getQualities().getProperty("RefinedFunctionLocations").getValue()).getProperties());
                                    }};
                                    String location = ((String)locationList.get(functionList.indexOf(function)).getValue());
                                    String name = location.substring(location.lastIndexOf("\\") + 1, location.length() - 4);

                                    ArrayList<String> baseKeys = new ArrayList(){{
                                        add("game");
                                        add("email");
                                        add("word process");
                                        add("spreadsheet");
                                        add("call");
                                        add("web browser");
                                        add("edit");
                                        add("image");
                                    }};
                                    baseKeys.forEach(
                                        key -> {
                                            if(description.contains(key)){
                                                GrouperAdder list = ((GrouperAdder)keyMaker.getQualities().getProperty("FunctionKeyList").getValue());

                                                list.addProperty(new Property("Multimedia", new GrouperAdder("Multimedia")));
                                                ((GrouperAdder)list.getProperty("Multimedia").getValue()).addProperty(new Property("CreationMultimedia", new GrouperAdder("CreationMultimedia")));

                                                list.addProperty(new Property("Record", new GrouperAdder("Record")));
                                                list.addProperty(new Property("Edit", new GrouperAdder("Edit")));

                                                int index = baseKeys.indexOf(key);

                                                switch (index) {
                                                    case 0:
                                                        list.addProperty(new Property("Games", new GrouperAdder("Games")));
                                                        ((GrouperAdder)list.getProperty("Games").getValue()).addProperty(new Property("Function-" + name, location));
                                                    case 1:
                                                        list.addProperty(new Property("Email", new GrouperAdder("Email")));
                                                        ((GrouperAdder)list.getProperty("Email").getValue()).addProperty(new Property("Function-" + name, location));
                                                    case 2:
                                                        ((GrouperAdder)list.getProperty("Multimedia").getValue()).addProperty(new Property("Writing Multimedia", new GrouperAdder("Writers")));
                                                        ((GrouperAdder) ((Grouper) list.getProperty("Multimedia").getValue()).getProperty("WritingMultimedia").getValue()).addProperty(new Property("Function-" + name, location));
                                                    case 3:
                                                        ((GrouperAdder)list.getProperty("CreationMultimedia").getValue()).addProperty(new Property("SheetMultimedia", new GrouperAdder("SheetMultimedia")));
                                                        ((GrouperAdder) ((Grouper) list.getProperty("Multimedia").getValue()).getProperty("SheetMultimedia").getValue()).addProperty(new Property("Function-" + name, location));
                                                    case 4:
                                                        list.addProperty(new Property("Communication", new GrouperAdder("Communication")));
                                                        ((GrouperAdder)list.getProperty("Communcation").getValue()).addProperty(new Property("Function-" + name, location));
                                                    case 5:
                                                        list.addProperty(new Property("Internet", new GrouperAdder("Internet")));
                                                        ((GrouperAdder)list.getProperty("Internet").getValue()).addProperty(new Property("Function-" + name, location));
                                                }

                                                if(key.equalsIgnoreCase("edit")) {
                                                    if (description.contains("video") || description.contains("movie")) {
                                                        ((GrouperAdder) list.getProperty("Edit").getValue()).addProperty(new Property("Video", new GrouperAdder("Video")));
                                                        ((GrouperAdder) ((Grouper) list.getProperty("Edit").getValue()).getProperty("Video").getValue()).addProperty(new Property("Function-" + name, location));
                                                    }
                                                    if (description.contains("audio") || description.contains("sound")) {
                                                        ((GrouperAdder) list.getProperty("Edit").getValue()).addProperty(new Property("Audio", new GrouperAdder("Audio")));
                                                        ((GrouperAdder) ((Grouper) list.getProperty("Edit").getValue()).getProperty("Audio").getValue()).addProperty(new Property("Function-" + name, location));
                                                    }
                                                }

                                                if(key.equalsIgnoreCase("image"))
                                                    if(description.contains("edit") || description.contains("draw")){
                                                        list.addProperty(new Property("ImageEdit", new GrouperAdder("ImageEdit")));
                                                        ((GrouperAdder)list.getProperty("ImageEdit").getValue()).addProperty(new Property("Function-" + name, location));
                                                    }

                                                ((GrouperAdder)keyMaker.getQualities().getProperty("FunctionKeyList").getValue()).addAllProperty(list.getProperties());
                                            }
                                        }
                                    );
                                }
                            );
                        }
                    ));

                    add(new PropertyExecute("CommandResponseKeyer",
                        () -> {
                            GrouperAdder commandresponseKeyList = (GrouperAdder) keyMaker.getQualities().getProperty("CommandResponseKeyList").getValue();

                            ((Grouper)keyMaker.getQualities().getProperty("FunctionKeyList").getValue()).getProperties().forEach(
                                functionKey -> {
                                    String functionKeyType = ((Grouper)functionKey.getValue()).getIdentity();
                                    GrouperAdder addProperties = new GrouperAdder("Adds",
                                        new ArrayList(){{
                                            add(new Property("Open", "OPEN"));
                                            add(new Property("Update", "UPDATE"));
                                            add(new Property("Set", "SET"));
                                            add(new Property("Delete", "DELETE"));
                                            add(new Property("Print", "PRINT"));
                                            add(new Property("Call", "CALL"));
                                            add(new Property("Message", "MESSAGE"));
                                            add(new Property("AddFriend", "ADDFRIEND"));
                                            add(new Property("Lookup", "LOOKUP"));
                                        }}
                                    );
                                    if(functionKeyType.equalsIgnoreCase("Games") || functionKeyType.equalsIgnoreCase("Edit")){
                                        commandresponseKeyList.addProperty(new Property("GamesCommands", new GrouperAdder("Commands")));
                                        addProperties.getProperties().retainAll(
                                            new ArrayList(){{
                                                add(new Property("Open", "OPEN"));
                                                add(new Property("Update", "UPDATE"));
                                                add(new Property("Configure", "CONFIGURE"));
                                            }}
                                        );
                                    }
                                    if(functionKeyType.equalsIgnoreCase("Email")){
                                        commandresponseKeyList.addProperty(new Property("EmailCommands", new GrouperAdder("Commands")));
                                        addProperties.getProperties().retainAll(
                                            new ArrayList(){{
                                                add(new Property("Open", "OPEN"));
                                                add(new Property("Update", "UPDATE"));
                                                add(new Property("Configure", "CONFIGURE"));
                                                add(new Property("Delete", "DELETE"));
                                            }}
                                        );
                                    }
                                    if(functionKeyType.equalsIgnoreCase("Multimedia") || functionKeyType.equalsIgnoreCase("ImageEdit")){
                                        commandresponseKeyList.addProperty(new Property("MultimediaCommands", new GrouperAdder("Commands")));
                                        addProperties.getProperties().retainAll(
                                            new ArrayList(){{
                                                add(new Property("Open", "OPEN"));
                                                add(new Property("Update", "UPDATE"));
                                                add(new Property("Print", "PRINT"));
                                            }}
                                        );
                                    }
                                    if(functionKeyType.equalsIgnoreCase("Communication")){
                                        commandresponseKeyList.addProperty(new Property("CommunicationCommands", new GrouperAdder("Commands")));
                                        addProperties.getProperties().retainAll(
                                            new ArrayList(){{
                                                add(new Property("Update", "UPDATE"));
                                                add(new Property("Configure", "CONFIGURE"));
                                                add(new Property("Call", "CALL"));
                                                add(new Property("Message", "MESSAGE"));
                                                add(new Property("AddFriend", "ADDFRIEND"));
                                            }}
                                        );
                                    }
                                    if(functionKeyType.equalsIgnoreCase("Internet")){
                                        commandresponseKeyList.addProperty(new Property("InternetCommands", new GrouperAdder("Commands")));
                                        addProperties.getProperties().retainAll(
                                            new ArrayList(){{
                                                add(new Property("Update", "UPDATE"));
                                                add(new Property("Configure", "CONFIGURE"));
                                                add(new Property("Lookup", "LOOKUP"));
                                            }}
                                        );
                                    }
                                    ((GrouperAdder)commandresponseKeyList.getProperty(functionKeyType + "Commands").getValue()).addAllProperty(addProperties.getProperties());
                                }
                            );
                            keyMaker.getQualities().setProperty("CommandResponseKeyList", new Property("CommandResponseKeyList", commandresponseKeyList));
                        }
                    ));
                }}
            )
        ));

        ((Executor)keyMaker.getRuntimeTasks().getProperty("ExecutorProcess").getValue()).executeFunction("FunctionKeyer");
    }

    public Grouper getResponses(){ return ((Grouper)keyMaker.getQualities().getProperty("CommandResponseKeyList").getValue()); }
}
