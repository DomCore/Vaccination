package com.api.Poletechnika.utils;

public class FilterUtil {

    //ПОИСК ИМЕНИ ПО СЛОВУ
    static public boolean searchName(String search, String what) {
        if(!search.replaceAll(what,"_").equals(search)) {
            return true;
        }
        return false;
    }

}
