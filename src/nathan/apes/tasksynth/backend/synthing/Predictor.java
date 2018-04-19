package nathan.apes.tasksynth.backend.synthing;

import nathan.apes.roots.definer.Property;
import nathan.apes.roots.definer.PropertyExecute;
import nathan.apes.roots.executor.Executor;
import nathan.apes.roots.grouper.Grouper;
import nathan.apes.roots.initiator.Initiator;

import java.util.ArrayList;

public class Predictor {

    public Initiator rater;

    public Predictor(String input, String type){

        rater = new Initiator("Rater", new Grouper("Rating"), new Grouper("Executor"));

        rater.getQualities().toGrouperAdder("Rating").addProperty(new Property("Input", input));
        rater.getQualities().toGrouperAdder("Rating").addProperty(new Property("RatingType", type));
        rater.getQualities().toGrouperAdder("Rating").addProperty(new Property("IsAccurate", false));

        rater.getRuntimeTasks().toGrouperAdder("Executor").addProperty(new Property("ExecutorProcess",
            new Executor("RatingProcess",
                new ArrayList(){{
                    add(new PropertyExecute("Process",
                        () -> {
                            String input = (String) rater.getQualities().getProperty("Input").getValue();
                            String ratingType = (String) rater.getQualities().getProperty("RatingType").getValue();
                        }
                    ));
                }}
            )
        ));

        ((Executor)rater.getRuntimeTasks().getProperty("ExecutorProcess").getValue()).executeFunctions();
    }
}
