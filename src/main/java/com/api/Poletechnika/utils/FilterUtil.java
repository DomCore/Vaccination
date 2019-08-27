package com.api.Poletechnika.utils;

import java.util.Arrays;
import java.util.List;

public class FilterUtil {

    //ПОИСК ИМЕНИ ПО СЛОВУ
    public boolean searchName(String search, String what) {
        if(!search.replaceAll(what,"_").equals(search)) {
            return true;
        }
        return false;
    }


    //ПОИСК ПО ФИЛЬТРАМ В СТРОКЕ: "1,2,35,55"
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
}
