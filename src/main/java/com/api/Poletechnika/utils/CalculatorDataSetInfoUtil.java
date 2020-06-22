package com.api.Poletechnika.utils;


import com.api.Poletechnika.models.UserCalculationData;

public class CalculatorDataSetInfoUtil {
    public void addInputDataInfo(String nameParam, UserCalculationData dataItem){
        switch (nameParam){
            case Constants.CALCULATION_INPUT_PARAM_WORK_WITH_KEY:
                dataItem.setTitle(Constants.CALCULATION_INPUT_PARAM_WORK_WITH_TITLE);
                dataItem.setMetrics(Constants.CALCULATION_INPUT_PARAM_WORK_WITH_METRICS);
                break;
            case Constants.CALCULATION_INPUT_PARAM_WORK_AREA_KEY:
                dataItem.setTitle(Constants.CALCULATION_INPUT_PARAM_WORK_AREA_TITLE);
                dataItem.setMetrics(Constants.CALCULATION_INPUT_PARAM_WORK_AREA_METRICS);
                break;
            case Constants.CALCULATION_INPUT_PARAM_FUEL_HOUR_KEY:
                dataItem.setTitle(Constants.CALCULATION_INPUT_PARAM_FUEL_HOUR_TITLE);
                dataItem.setMetrics(Constants.CALCULATION_INPUT_PARAM_FUEL_HOUR_METRICS);
                break;
            case Constants.CALCULATION_INPUT_PARAM_FUEL_VOLUME_KEY:
                dataItem.setTitle(Constants.CALCULATION_INPUT_PARAM_FUEL_VOLUME_TITLE);
                dataItem.setMetrics(Constants.CALCULATION_INPUT_PARAM_FUEL_VOLUME_METRICS);
                break;
            case Constants.CALCULATION_INPUT_CAR_COUNT_KEY:
                dataItem.setTitle(Constants.CALCULATION_INPUT_CAR_COUNT_TITLE);
                dataItem.setMetrics(Constants.CALCULATION_INPUT_CAR_COUNT_METRICS);
                break;
            case Constants.CALCULATION_INPUT_PARAM_EARTH_TYPE_KEY:
                dataItem.setTitle(Constants.CALCULATION_INPUT_PARAM_EARTH_TYPE_TITLE);
                dataItem.setMetrics("");
                break;
            case Constants.CALCULATION_INPUT_PARAM_WEATHER_KEY:
                dataItem.setTitle(Constants.CALCULATION_INPUT_PARAM_WEATHER_TITLE);
                dataItem.setMetrics("");
                break;
            case Constants.CALCULATION_INPUT_PARAM_APPLY_KEY:
                dataItem.setTitle(Constants.CALCULATION_INPUT_PARAM_APPLY_TITLE);
                dataItem.setMetrics("");
                break;
             default:
                 break;
        }
    }

    public void addOutputDataInfo(String nameParam, UserCalculationData dataItem){
        switch (nameParam){
            case Constants.CALCULATION_OUTPUT_PARAM_SPEED_KEY:
                dataItem.setTitle(Constants.CALCULATION_OUTPUT_PARAM_SPEED_TITLE);
                dataItem.setMetrics(Constants.CALCULATION_OUTPUT_PARAM_SPEED_METRICS);
                break;
            case Constants.CALCULATION_OUTPUT_PRODUCTIVITY_KEY:
                dataItem.setTitle(Constants.CALCULATION_OUTPUT_PRODUCTIVITY_TITLE);
                dataItem.setMetrics(Constants.CALCULATION_OUTPUT_PRODUCTIVITY_METRICS);
                break;
            case Constants.CALCULATION_OUTPUT_TIME_REQUIRED_KEY:
                dataItem.setTitle(Constants.CALCULATION_OUTPUT_TIME_REQUIRED_TITLE);
                dataItem.setMetrics(Constants.CALCULATION_OUTPUT_TIME_REQUIRED_METRICS);
                break;
            case Constants.CALCULATION_OUTPUT_FUEL_REQUIRED_KEY:
                dataItem.setTitle(Constants.CALCULATION_OUTPUT_FUEL_REQUIRED_TITLE);
                dataItem.setMetrics(Constants.CALCULATION_OUTPUT_FUEL_REQUIRED_METRICS);
                break;
            case Constants.CALCULATION_OUTPUT_REFURLING_REQ_KEY:
                dataItem.setTitle(Constants.CALCULATION_OUTPUT_REFURLING_REQ_TITLE);
                dataItem.setMetrics(Constants.CALCULATION_OUTPUT_REFURLING_REQ_METRICS);
                break;
            default:
                break;
        }
    }

    public void setCurrenOutputTime(UserCalculationData dataItem){
        final int hourInMin = 60;
        final int dayInMin = 1440;
        final int hoursInDay = 24;

        double valueTime = Double.parseDouble(dataItem.getValue());
        double timeMin = valueTime * hourInMin;
        if (timeMin < hourInMin) {
            //in minutes
            int minutes = (int) timeMin;
            dataItem.setValue(String.valueOf(minutes));
            dataItem.setMetrics(Constants.CALCULATION_OUTPUT_TIME_REQUIRED_METRICS_MIN);
        }else {
            if (timeMin < dayInMin) {
                //in hours
                int timeHours = (int) valueTime;
                int timeMinPart = (int) ((valueTime - timeHours) * hourInMin);  //check if not 0 then add to minutes value

                if(timeMinPart > 0){ //5 год 5 хв
                    dataItem.setValue(timeHours + " " + Constants.CALCULATION_OUTPUT_TIME_REQUIRED_METRICS + " " + timeMinPart);
                    dataItem.setMetrics(Constants.CALCULATION_OUTPUT_TIME_REQUIRED_METRICS_MIN);
                }else {  //5 год
                    dataItem.setValue(String.valueOf(timeHours));
                    dataItem.setMetrics(Constants.CALCULATION_OUTPUT_TIME_REQUIRED_METRICS);
                }
            }else {
                //in days
                double timeDays = timeMin / dayInMin;
                int onlyDay = (int) timeDays;
                int hours = (int)  ((timeDays - onlyDay) * hoursInDay); //check if not 0 then add to minutes value
                if(hours > 0){  //5 дн 10 год
                    dataItem.setValue(onlyDay + " " + Constants.CALCULATION_OUTPUT_TIME_REQUIRED_METRICS_DAYS + " " + hours);
                    dataItem.setMetrics(Constants.CALCULATION_OUTPUT_TIME_REQUIRED_METRICS);
                }else {  //5 дн
                    dataItem.setValue(String.valueOf(onlyDay));
                    dataItem.setMetrics(Constants.CALCULATION_OUTPUT_TIME_REQUIRED_METRICS_DAYS);
                }
            }
        }
    }

}
