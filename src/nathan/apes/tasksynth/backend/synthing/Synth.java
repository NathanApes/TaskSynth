package nathan.apes.tasksynth.backend.synthing;

import nathan.apes.roots.definer.Property;
import nathan.apes.roots.definer.PropertyExecute;
import nathan.apes.roots.executor.Executor;
import nathan.apes.roots.grouper.Grouper;
import nathan.apes.roots.grouper.GrouperAdder;
import nathan.apes.roots.initiator.Initiator;

import java.util.ArrayList;

import static nathan.apes.tasksynth.backend.synthing.Classifier.keyMaker;

public class Synth {

    public static Initiator taskSynther;

    public Synth(){
        taskSynther = new Initiator("TaskSynther", new Grouper("TaskProperties"), new Grouper("Executor"));
        taskSynther.getQualities().toGrouperAdder("TaskProperties").addProperty(new Property("Input", ""));
        taskSynther.getRuntimeTasks().toGrouperAdder("Executor").addProperty(new Property("SynthProcess",
            new Executor("Synth",
                new ArrayList(){{
                    add(new PropertyExecute("Process",
                        () -> {
                            //1. Take input
                            //2. Process Verbs/Terms into respecting synonyms
                            //3. Match the "Key Term" to its defined function
                            //4. Carry that Function Out

                            String[] inputTerms = ((String)taskSynther.getQualities().getProperty("Input").getValue()).split(" ");
                            ArrayList<String> terms = new ArrayList<>();

                            for (int i = 0; i < inputTerms.length; i++)
                                terms.add(inputTerms[i]);

                            terms.forEach(
                                term -> {

                                    Dictionary programDefiner = new Dictionary(term, true);
                                    if(!programDefiner.wordMeaning.getQualities().getProperty("OutputDefinition").getValue().equals(""))
                                        keyMaker.getQualities().setProperty("TaskObject", new Property("TaskObject", term));

                                    if(term.equalsIgnoreCase("Print"))
                                        keyMaker.getQualities().setProperty("SelectedKey", new Property("SelectedKey", "PRINT"));
                                    if(term.equalsIgnoreCase("Message"))
                                        keyMaker.getQualities().setProperty("SelectedKey", new Property("SelectedKey", "MESSAGE"));
                                    if(term.equalsIgnoreCase("Add") && ((String)taskSynther.getQualities().getProperty("Input").getValue()).contains("Friend"))
                                        keyMaker.getQualities().setProperty("SelectedKey", new Property("SelectedKey", "ADDFRIEND"));
                                    if(term.equalsIgnoreCase("Lookup"))
                                        keyMaker.getQualities().setProperty("SelectedKey", new Property("SelectedKey", "LOOKUP"));
                                    if(term.equalsIgnoreCase("Play"))
                                        keyMaker.getQualities().setProperty("SelectedKey", new Property("SelectedKey", "PLAY"));
                                    if(term.equalsIgnoreCase("Move"))
                                        keyMaker.getQualities().setProperty("SelectedKey", new Property("SelectedKey", "MOVE"));
                                    if(term.equalsIgnoreCase("Rename"))
                                        keyMaker.getQualities().setProperty("SelectedKey", new Property("SelectedKey", "RENAME"));

                                    Dictionary definer = new Dictionary(term, false);
                                    String type = (String) definer.wordMeaning.getQualities().getProperty("OutputWordType").getValue();
                                    if(type.equals("verb")){
                                        GrouperAdder identicals = (GrouperAdder) definer.wordMeaning.getQualities().getProperty("OutputIdenticals").getValue();
                                        GrouperAdder functionTerms = (GrouperAdder) keyMaker.getQualities().getProperty("CommandResponseKeyList").getValue();
                                        functionTerms.getProperties().forEach(
                                            function ->
                                                identicals.getProperties().forEach(
                                                    identical -> {
                                                        if(identical.getValue().equals(function.getValue()))
                                                            keyMaker.getQualities().setProperty("SelectedKey", new Property("SelectedKey", function.getValue()));
                                                    }
                                                )
                                        );
                                    }
                                }
                            );
                        }
                    ));
                }}
            ))
        );
    }
}
