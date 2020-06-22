package com.api.Poletechnika.utils;

import java.util.Arrays;
import java.util.List;

public class FilterUtil {

    //ПОИСК ИМЕНИ ПО СЛОВУ
    public boolean searchName(String search, String what) {
        if(!search.replaceAll(what,"_").equals(search)) {
            return true;
        }
        if(!search.replaceAll(upperCaseAllFirst(what),"_").equals(search)) {
            return true;
        }
        if(!search.replaceAll(what.toLowerCase(),"_").equals(search)) {
            return true;
        }
        return false;
    }


    //ПОИСК ПО ФИЛЬТРАМ В СТРОКЕ: "1,2,35,55"  ИССПОЛЬЗУЮ В ПОИСКЕ РЕГИОНОВ В ПОЧВЕ
    public boolean searchFilter(String search, String what) {
        List<String> filtersList = Arrays.asList(search.split(","));
        for (String item : filtersList){
            if(what.trim().equals(item.trim())){
                return true;
            }
        }
        return false;
    }

    //ПОИСК ПО ИМЕНИ И ХЕШТЕГУ
    public boolean searchNameOrHash(String searchData, String name, String hashtags) {
        if(!name.replaceAll(searchData.trim(),"_").equals(name)) {
            return true;
        }else {
            hashtags = hashtags.replaceAll("\\s+","");
            List<String> hashtagsList = Arrays.asList(hashtags.split("#"));
            for (String item : hashtagsList){
                if(searchData.trim().equals(item)){
                    return true;
                }
            }
            return false;
        }
    }
    //ПОИСК ПО ХЕШТЕГУ
    public boolean searchHashtag(String searchData, String hashtags) {
        hashtags = hashtags.replaceAll("\\s+","");
        List<String> hashtagsList = Arrays.asList(hashtags.split("#"));
        for (String item : hashtagsList){
            if(searchData.trim().equals(item)){
                return true;
            }
        }
        return false;
    }




    //private util
    private static String upperCaseAllFirst(String value) {
        char[] array = value.toCharArray();
        // Uppercase first letter.
        array[0] = Character.toUpperCase(array[0]);

        // Uppercase all letters that follow a whitespace character.
        for (int i = 1; i < array.length; i++) {
            if (Character.isWhitespace(array[i - 1])) {
                array[i] = Character.toUpperCase(array[i]);
            }
        }

        // Result.
        return new String(array);
    }
}
