package com.example.zooseeker10;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

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
