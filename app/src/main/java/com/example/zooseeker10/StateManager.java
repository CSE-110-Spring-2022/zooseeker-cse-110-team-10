package com.example.zooseeker10;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Map;

/**
 * Manages persistence of application state
 */
public class StateManager {
    private final File directory;
    private final File stateFile;
    private final File selectionFile;
    private final File planFile;
    private final File directionsFile;

    private static StateManager stateManager;

    public static StateManager getSingleton() {
        return stateManager;
    }

    public static StateManager getSingleton(Context context) {
        if (stateManager == null) {
            stateManager = new StateManager(context);
        }
        return stateManager;
    }

    private StateManager(Context context) {
        directory = context.getDir(Globals.State.DIRECTORY_PATH, Context.MODE_PRIVATE);

        stateFile = new File(directory, Globals.State.ACTIVE_STATE_FILENAME);
        selectionFile = new File(directory, Globals.State.SELECTION_FILENAME);
        planFile = new File(directory, Globals.State.PLAN_FILENAME);
        directionsFile = new File(directory, Globals.State.DIRECTIONS_FILENAME);
    }

    public boolean isCleanStart() {
        boolean isCleanStart = !stateFile.exists();

        if (isCleanStart) {
            if (!directory.exists()) {
                directory.mkdir();
            }

            try {
                stateFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return isCleanStart;
    }

    /* store<xActivity>State() should be used in the onStop() methods of <xActivity> */
    public void storeSelectionState(SelectedExhibits exhibits) {
        storeMapToFile(stateFile,
                                   Map.of(Globals.MapKeys.STATE, Globals.State.ActiveState.Selection));
        storeMapToFile(selectionFile,
                                   Map.of(Globals.MapKeys.SELECTED_EXHIBIT_IDS, exhibits.getExhibitIds()));

        Log.d("StateManager", "Stored to MainActivity file: "
                + Arrays.toString(exhibits.getExhibitIds().toArray()));
    }

    public void storePlanState(ZooPlan plan) {
        storeMapToFile(stateFile,
                                   Map.of(Globals.MapKeys.STATE, Globals.State.ActiveState.Plan));
        storeMapToFile(planFile,
                                   Map.of(Globals.MapKeys.ZOOPLAN, plan));
    }

    public void storeDirectionsState(ZooPlan plan, int walkerIndex) {
        storeMapToFile(stateFile,
                                   Map.of(Globals.MapKeys.STATE, Globals.State.ActiveState.Directions));
        storeMapToFile(directionsFile,
                                   Map.of(Globals.MapKeys.ZOOPLAN, plan,
                                          Globals.MapKeys.WALKER_INDEX, walkerIndex));
    }

    /**
     * Loads the corresponding map from disk
     *
     * @param state the activity map to load
     * @return a map of the activity's respective state variables
     */
    public Map<String, Object> getActivityMap(Globals.State.ActiveState state) {
        File file = getFile(state);

        if (!file.exists()) {
            throw new IllegalStateException(String.format("File at path %s not found", file.getAbsolutePath()));
        }

        return loadMapFromFile(file);
    }

    /**
     * Gets the file corresponding to the passed in state
     *
     * @param state current activity
     * @return file corresponding to the passed in state
     */
    private File getFile(Globals.State.ActiveState state) {
        switch (state) {
            case Trampoline:
                return stateFile;
            case Selection:
                return selectionFile;
            case Plan:
                return planFile;
            case Directions:
                return directionsFile;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

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