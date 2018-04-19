package nathan.apes.tasksynth.backend.synthing;

import nathan.apes.roots.definer.Property;
import nathan.apes.roots.grouper.Grouper;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class Assesser {

    private final ArrayList<File> filteredFiles = new ArrayList<>();

    private final static Grouper machineFunctions = new Grouper("MachineFunctions");

    public Assesser(){
        File topDirectory = new File("C:/");
        Path currentPath = topDirectory.toPath();

        File currentFile;

        int fileCount;
        int directoryCount;
        int cycleFileCount = currentPath.toFile().listFiles().length;
        int[] cycleDirectoryCount = new int[30]; cycleDirectoryCount[0] = filterDirectories(convertDirectoryFiles(currentPath.toFile().listFiles())).size();
        int level = 0;

        while(true){
            fileCount = currentPath.toFile().listFiles().length;
            directoryCount = filterDirectories(convertDirectoryFiles(currentPath.toFile().listFiles())).size();

            currentFile = currentPath.toFile().listFiles()[fileCount - cycleFileCount];

            if(currentFile.canExecute())
                filteredFiles.add(currentFile);

            cycleFileCount--;

            if(cycleFileCount < 2) {
                if(directoryCount > 0 && cycleDirectoryCount[level] > 0) {
                    currentPath = filterDirectories(convertDirectoryFiles(currentPath.toFile().listFiles())).get(directoryCount - cycleDirectoryCount[level]).toPath();
                    cycleDirectoryCount[level]--;
                    level++;
                    cycleDirectoryCount[level] = filterDirectories(convertDirectoryFiles(currentPath.toFile().listFiles())).size();
                }
                if(directoryCount < 1 || cycleDirectoryCount[level] < 1){
                    if(cycleDirectoryCount[level - 1] > 0){
                        currentPath = currentPath.getParent();
                        level--;
                    } else {
                        currentPath = currentPath.getParent().getParent();
                        level -= 2;
                        if(cycleDirectoryCount[0] < 1 && level == 0)
                            break;
                    }
                }
                cycleFileCount = currentPath.toFile().listFiles().length;
            }
        }
        filteredFiles.forEach(
            file -> machineFunctions.toGrouperAdder("MachineFunctions").addProperty(new Property(file.getName(), file))
        );
    }

    public static Grouper getMachineFunctions(){ return machineFunctions; }

    private ArrayList<File> convertDirectoryFiles(File[] fileArray) {
        ArrayList<File> files = new ArrayList<>();
        for (int i = 0; i < fileArray.length; i++)
            files.add(fileArray[i]);
        return files;
    }

    private ArrayList<File> filterDirectories(ArrayList<File> inputFiles){
        return new ArrayList(){{
           inputFiles.forEach(
               file -> {
                   if(file.isDirectory())
                       add(file);
               }
           );
        }};
    }

    /*
    public static void cheat(){
        ArrayList<Property> cheating = new ArrayList(){{
            File file2 = new File("C:/Program Files/Adobe/Adobe Photoshop CS6 (64 Bit)/Photoshop.exe");
            add(new Property("Photoshop.exe", file2));
        }};
        machineFunctions.toGrouperAdder("MachineFunctions").addAllProperty(cheating);
    }
    */

}