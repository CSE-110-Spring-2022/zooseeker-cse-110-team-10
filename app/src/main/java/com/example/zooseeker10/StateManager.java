package com.example.zooseeker10;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Manages persistence of application state
 */
public class StateManager {
    private enum ActiveState {
        Main,
        Plan,
        Directions;
    }

    private static final File directory;
    private static final File stateFile;
    private static final File mainFile;
    private static final File planFile;
    private static final File directionsFile;

    static
    {
        ContextWrapper contextWrapper = new ContextWrapper(Globals.State.activity);
        directory = contextWrapper.getDir(Globals.State.DIRECTORY_PATH, Context.MODE_PRIVATE);

        stateFile = new File(directory, Globals.State.ACTIVE_STATE_FILENAME);
        mainFile = new File(directory, Globals.State.MAIN_FILENAME);
        planFile = new File(directory, Globals.State.PLAN_FILENAME);
        directionsFile = new File(directory, Globals.State.DIRECTIONS_FILENAME);
    }

    /**
     * Used to load and store files. Honestly, doesn't even need to be in this subclass but I am
     * clinically insane and have committed manslaughter in multiple countries.
     */
    private static class FileManager {
        /**
         * Writes the given map to the file
         *
         * @param file file to write to
         * @param mapToWrite map to write
         */
        private static void storeMapToFile(File file, Map<String, Object> mapToWrite) {
            try {
                // Saving of object in a file
                FileOutputStream fileOut = new FileOutputStream(file);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

                // Method for serialization of object
                objectOut.writeObject(mapToWrite);

                objectOut.close();
                fileOut.close();

                Log.d("StateManager", String.format("Map successfully stored at %s",
                        file.getAbsolutePath()));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Loads map from file
         *
         * @param file file to load map from
         * @return We may never know
         */
        private static Map<String, Object> loadMapFromFile(File file) {
            try {
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);

                Map<String, Object> loadedList = (Map<String, Object>) objectIn.readObject();

                objectIn.close();
                fileIn.close();

                Log.d("StateManager", String.format("Map successfully loaded from %s",
                        file.getAbsolutePath()));

                return loadedList;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return null; // TODO: idk. maybe just throw unchecked exceptions in catches.
        }
    }

    /**
     * Called by MainActivity's onCreate to load state data and pull up the last-active Activity
     *
     * @param activity the calling MainActivity
     */
    public static void loadLastActiveActivity(MainActivity activity) {
        // Sets state in disk and finishes if clean start
        if (!stateFile.exists()) {
            if (!directory.exists()) {
                directory.mkdir();
            }

            try {
                stateFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            storeMainState(activity.selectedExhibits);
            return;
        }

        // Switches to correct activity and loads relevant files based on last saved state
        ActiveState activeState = (ActiveState) FileManager.loadMapFromFile(stateFile).get(Globals.MapKeys.STATE);
        switch (activeState) {
            case Main:
                if (!mainFile.exists()) {
                    throw new IllegalStateException("MainFile not found");
                }

                List<String> selectedExhibitIDs = (ArrayList<String>) FileManager.loadMapFromFile(mainFile).get(Globals.MapKeys.SELECTED_EXHIBIT_IDS);
                Log.d("StateManager", "Loaded from MainActivity file: "
                        + Arrays.toString(selectedExhibitIDs.toArray()));

                SelectedExhibits selectedExhibits = new SelectedExhibits(Globals.State.activity);
                for (String exhibit : selectedExhibitIDs) {
                    selectedExhibits.addExhibit(exhibit);
                }

                activity.selectedExhibits = selectedExhibits; // TODO: Bruh this is scuffed. Maybe intent to self with extra (ensure completed oncreate in mainactivity AND android:launchMode="singleTop")
                break;
            case Plan:
                if (!planFile.exists()) {
                    throw new IllegalStateException("PlanFile not found");
                }

                ZooPlan pathZooPlan = (ZooPlan) FileManager.loadMapFromFile(planFile).get(Globals.MapKeys.ZOOPLAN);

                Intent pIntent = new Intent(activity, PlanActivity.class);
                pIntent.putExtra(Globals.MapKeys.ZOOPLAN, pathZooPlan);
                activity.startActivity(pIntent);
                break;
            case Directions:
                if (!directionsFile.exists()) {
                    throw new IllegalStateException("DirectionsFile not found"); // TODO: Probably don't need to throw exception. Could just restart in MainActivity.
                }

                Map<String, Object> directionsMap = FileManager.loadMapFromFile(directionsFile);
                ZooPlan directionsZooPlan = (ZooPlan) directionsMap.get(Globals.MapKeys.ZOOPLAN);
                int walkerIndex = (Integer) directionsMap.get(Globals.MapKeys.WALKER_INDEX);

                Intent dIntent = new Intent(activity, DirectionsActivity.class);
                dIntent.putExtra(Globals.MapKeys.ZOOPLAN, directionsZooPlan);
                dIntent.putExtra(Globals.MapKeys.WALKER_INDEX, walkerIndex);
                activity.startActivity(dIntent);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + activeState);
        }
    }

    /* store<xActivity>State() should be used in the onStop() methods of <xActivity> */
    public static void storeMainState(SelectedExhibits exhibits) {
        FileManager.storeMapToFile(stateFile,
                                   Map.of(Globals.MapKeys.STATE, ActiveState.Main));
        FileManager.storeMapToFile(mainFile,
                                   Map.of(Globals.MapKeys.SELECTED_EXHIBIT_IDS, exhibits.getExhibitIds()));

        Log.d("StateManager", "Stored to MainActivity file: "
                + Arrays.toString(exhibits.getExhibitIds().toArray()));
    }

    public static void storePlanState(ZooPlan plan) {
        FileManager.storeMapToFile(stateFile,
                                   Map.of(Globals.MapKeys.STATE, ActiveState.Plan));
        FileManager.storeMapToFile(planFile,
                                   Map.of(Globals.MapKeys.ZOOPLAN, plan));
    }

    public static void storeDirectionsState(ZooPlan plan, int walkerIndex) {
        FileManager.storeMapToFile(stateFile,
                                   Map.of(Globals.MapKeys.STATE, ActiveState.Directions));
        FileManager.storeMapToFile(directionsFile,
                                   Map.of(Globals.MapKeys.ZOOPLAN, plan,
                                          Globals.MapKeys.WALKER_INDEX, walkerIndex));
    }

    /* Helper methods to get file maps */
    private static Map<String, Object> loadMainFile() {
        return FileManager.loadMapFromFile(mainFile);
    }

    private static Map<String, Object> loadPlanFile() {
        return FileManager.loadMapFromFile(planFile);
    }

    private static Map<String, Object> loadDirectionsFile() {
        return FileManager.loadMapFromFile(directionsFile);
    }
}