package nathan.apes.tasksynth.backend.synthing;

import nathan.apes.roots.definer.Property;
import nathan.apes.roots.definer.PropertyExecute;
import nathan.apes.roots.executor.Executor;
import nathan.apes.roots.grouper.Grouper;
import nathan.apes.roots.grouper.GrouperAdder;
import nathan.apes.roots.initiator.Initiator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Dictionary {

    public final Initiator wordMeaning;

    public Dictionary(String input, boolean isName) {
        wordMeaning = new Initiator("WordDescriptor", new Grouper("Words"), new Grouper("Executor"));

        wordMeaning.getQualities().toGrouperAdder("Words").addProperty(new Property("InputWord", input));
        wordMeaning.getQualities().toGrouperAdder("Words").addProperty(new Property("OutputDefinition", ""));
        wordMeaning.getQualities().toGrouperAdder("Words").addProperty(new Property("OutputType", ""));
        wordMeaning.getQualities().toGrouperAdder("Words").addProperty(new Property("OutputIdenticals", new GrouperAdder("Identicals")));
        wordMeaning.getQualities().toGrouperAdder("Words").addProperty(new Property("DidSucceed", true));
        wordMeaning.getQualities().toGrouperAdder("Words").addProperty(new Property("IsName", isName));

        wordMeaning.getRuntimeTasks().toGrouperAdder("Executor").addProperty(new Property("WordDefiner",
            new Executor("DefiningProcess",
                new ArrayList(){{
                    add(new PropertyExecute("Process",
                        () -> {
                            String definition = "";
                            String wordType = "";
                            GrouperAdder wordIdenticals = new GrouperAdder("Identicals");

                            String input = (String) wordMeaning.getQualities().getProperty("InputWord").getValue();

                            String url = "https://www.google.com/search?q=" + input;
                            String htmlClass;

                            boolean name = (boolean) wordMeaning.getQualities().getProperty("IsName").getValue();

                            if(!name) {
                                url = url + "+definition";
                                htmlClass = "_Jig";
                            } else {
                                url = url + "+software+description";
                                htmlClass = "_Tgc";
                            }

                            try {
                                Document googleDictionary = Jsoup.connect(url).get();
                                String googleDefinitionEntry = googleDictionary.getElementsByClass(htmlClass).html();
                                if(!name) {
                                    definition = googleDefinitionEntry.substring(googleDefinitionEntry.indexOf("<span>") + 6, googleDefinitionEntry.indexOf("</"));

                                    String googleTypeEntry = googleDictionary.getElementsByClass("lr_dct_sf_h").first().html();
                                    wordType = googleTypeEntry.substring(googleTypeEntry.indexOf("<span>") + 6, googleTypeEntry.indexOf("</"));

                                    ArrayList<String> identicals = new ArrayList<>();
                                    googleDictionary.getElementsByClass("vk_tbl vk_gy").forEach(
                                        section -> {
                                            String googleSynonEntry = section.getElementsByTag("td").get(1).html();
                                            String[] identicalEntries = googleSynonEntry.split("<span>");
                                            for (int i = 0; i < identicalEntries.length; i++)
                                                if (identicalEntries[i].contains("<")) {
                                                    String withoutClass = identicalEntries[i].substring(0, identicalEntries[i].indexOf("<"));
                                                    if(withoutClass.contains(","))
                                                        identicals.add(withoutClass.replaceAll(",", ""));
                                                }
                                        }
                                    );
                                    identicals.forEach(identical -> wordIdenticals.addProperty(new Property("Identical-" + identicals.indexOf(identical), identical)));
                                } else
                                    definition = googleDefinitionEntry.replaceAll("<b>", "").replaceAll("</b>", "");
                            } catch(IOException e){
                                e.printStackTrace();
                                definition = null;
                                wordType = null;
                                wordMeaning.getQualities().setProperty("DidSucceed", new Property("DidSucceed", false));
                            } catch (NullPointerException e1){
                                definition = null;
                                wordType = null;
                                wordMeaning.getQualities().setProperty("DidSucceed", new Property("DidSucceed", false));
                            } catch (IndexOutOfBoundsException e2){
                                definition = null;
                                wordType = null;
                                wordMeaning.getQualities().setProperty("DidSucceed", new Property("DidSucceed", false));
                            }

                            wordMeaning.getQualities().setProperty("OutputDefinition", new Property("OutputDefinition", definition));
                            wordMeaning.getQualities().setProperty("OutputType", new Property("OutputType", wordType));
                            wordMeaning.getQualities().setProperty("OutputIdenticals", new Property("OutputIdenticals", wordIdenticals));
                        }
                    ));
                }}
            ))
        );

        ((Executor)wordMeaning.getRuntimeTasks().getProperty("WordDefiner").getValue()).executeFunctions();
    }
}
