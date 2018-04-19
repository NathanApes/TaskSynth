package nathan.apes.tasksynth.backend.automation;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class TaskAutomation {

    public TaskAutomation(int type, String taskDetails, String triggerDetails){
        if(type == 0)
            automateTimedTask(taskDetails, triggerDetails);
    }

    private void automateTimedTask(String taskDetails, String timeDetails){
        long time = Long.parseLong(timeDetails.substring(timeDetails.indexOf(":") + 1, timeDetails.length() - 1));
        TimeUnit timeUnit = TimeUnit.HOURS;
        int multiplierUnit = 1;
        boolean repeat = false;

        if(timeDetails.contains("+"))
            time = Long.parseLong(timeDetails.substring(timeDetails.indexOf(":") + 2, timeDetails.length() - 1));

        if(timeDetails.contains("repeat"))
            repeat = true;

        if(timeDetails.contains("m")) {
            timeUnit = TimeUnit.DAYS;
            multiplierUnit = 30;
        }
        else if(timeDetails.contains("w")) {
            timeUnit = TimeUnit.DAYS;
            multiplierUnit = 7;
        }
        else if(timeDetails.contains("d"))
            timeUnit = TimeUnit.DAYS;

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //Run SynthTask
            }
        };
        Timer timer = new Timer();

        if(!repeat)
            timer.schedule(timerTask, TimeUnit.MILLISECONDS.convert(time, timeUnit) * multiplierUnit);
        else
            timer.schedule(timerTask, 0L,TimeUnit.MILLISECONDS.convert(time, timeUnit) * multiplierUnit);
    }
}
