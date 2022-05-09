package com.example.zooseeker10;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

/**
 * Citation:
 * https://developer.android.com/reference/android/arch/persistence/room/TypeConverter?hl=en
 * https://developer.android.com/reference/android/arch/persistence/room/TypeConverters?hl=en
 * TypeConverter and TypeConverters
 * May 8 2022
 * Used as a reference for how to store the tags in the database (TypeConverters in ZooDataBase).
 * A.S.
 */
public class TagsConverter {

    @TypeConverter
    public static String tagsListToString(List<String> tags) {
        return String.join(",", tags);
    }

    @TypeConverter
    public static List<String> stringToTagsList(String combinedTags) {
        return Arrays.asList(combinedTags.split(","));
    }
}
