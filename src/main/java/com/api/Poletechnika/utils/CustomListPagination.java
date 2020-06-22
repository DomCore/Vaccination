package com.api.Poletechnika.utils;

public class CustomListPagination {

    public int getLimit(String limit, int offsetValue, int arraySize){
        if(limit.trim().length() > 0){
            try {
                if(Integer.parseInt(limit) > 0){
                    if((offsetValue + (Integer.parseInt(limit)) > arraySize)){
                        return arraySize;
                    }else {
                        return (offsetValue + Integer.parseInt(limit));
                    }
                }else {
                    return arraySize;
                }
            }catch (NumberFormatException e){
                return arraySize;
            }
        }else {
            return arraySize;
        }
    }

    public int getOffset(String offset, int arraySize){
        if(offset.trim().length() > 0){
            try {
                if(Integer.parseInt(offset) > 0){
                    if((Integer.parseInt(offset)) > arraySize){
                        return (arraySize - 1);
                    }else {
                        return (Integer.parseInt(offset)-1);
                    }
                }else {
                    return 0;
                }
            }catch (NumberFormatException e){
                return 0;
            }
        }else {
            return 0;
        }
    }
}
