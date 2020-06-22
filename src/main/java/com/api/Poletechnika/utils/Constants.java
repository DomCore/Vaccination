package com.api.Poletechnika.utils;

public  class Constants {

    //GLOBAL LINKS
    static public final String URL_ARGONOMY_CALCULATOR = "http://agrocalc.pancode.net";
    static public final int SETTING_FRE_CALSULATION_COUNT = 2;


    //ERROR
    static public final String ERROR_SERVER_ERROR = "Server Error";
    static public final String ERROR_TYPE_NOT_FOUND = "Type not found";
    static public final String ERROR_DATA_NOT_FOUND = "Data not found";
    static public final String ERROR_WRONG_AUTH_TOKEN = "Wrong auth token";
    static public final String ERROR_WRONG_PASS = "Wrong password";
    static public final String ERROR_ACCESS_DENIED = "Access denied";
    static public final String ERROR_WRONG_DATA = "Wrong input data";
    static public final String ERROR_WRONG_CODE = "Wrong code";
    static public final String ERROR_REQUEST_IN_PROCESSING = "Request in processing";
    static public final String ERROR_CHARACTERISTICS_NOT_LIKE_PATTERNS = "general characteristics not like pattern array";
    //REGISTRATION ERRORS
    static public final String ERROR_REGISTRATION_NUMBER_USE = "Number is already use";
    static public final String ERROR_REGISTRATION_POLICY = "Policy not adopted";
    //FOR CALCULATION
    static public final String ERROR_CALCULATION_END_FREE_CALCULATION = "Not have free calculation";
    static public final String ERROR_CALCULATION_WRONG_CONDITIONS = "Impossible in these conditions";
    static public final String ERROR_CALCULATION_WRONG_IPUT_PARAMETERS = "Wrong enter parametrs";


    //PERMISSION KEYS
    static public final String ADMINISTRATION_PERMISSION_KEY = "admin";


    //LICENSE STATUS
    static public final String LICENSE_STATUS_PAID = "paid";
    static public final String LICENSE_STATUS_FREE = "free";
    static public final String LICENSE_STATUS_STAFF = "staff";

    //REGISTRATION STATUS
    static public final String REGISTRATION_STATUS_WAIT = "wait";
    static public final String REGISTRATION_STATUS_COMPLETE = "complete";


    //VIDEO AND BREAKDOWNS FILTERS TYPE
    static public final String FILTER_TYPE_BREAKDONW_TYPE = "breakdown_type";
    static public final String FILTER_TYPE_MACHINERY_TYPE = "car_type";
    static public final String FILTER_TYPE_MACHINERY_MODEL = "car_model";


    //FOR NOTIFICATION
    static public final String NOTIFICATION_TITLE_LICENSE_END_TITLE = "Лицензия приложения";
    static public final String NOTIFICATION_TITLE_LICENSE_BUY = "Вы приобрели платную лицензию";
    static public final String NOTIFICATION_TITLE_LICENSE_END_7 = "Платная лицензия заканчивается через 7 дней!";
    static public final String NOTIFICATION_TITLE_LICENSE_END_3 = "Платная лицензия заканчивается через 3 дня!";
    static public final String NOTIFICATION_TITLE_LICENSE_END = "Срок платной лицензии окончен. Продлите лицензию в настройках профиля.";
    //FOR NOTIFICATION statuses
    static public final String NOTIFICATION_TYPE_PROFILE = "profile";


    //FOR EARTH REGION FEATHER VALUES
    static public final String REGION_FEATURE_DENCITY_TITLE = "Щільність грунту";
    static public final String REGION_FEATURE_DENCITY_METRIC = "г/см3";


    //CALCULATION INPUT DATA
    //general
    static public final String CALCULATION_INPUT_PARAM_CAR_TYPE_KEY = "car_type";
    static public final String CALCULATION_INPUT_PARAM_CAR_TYPE_TITLE = "Категорія пристрою";

    static public final String CALCULATION_INPUT_PARAM_CAR_MODEL_KEY = "car_model";
    static public final String CALCULATION_INPUT_PARAM_CAR_MODEL_TITLE = "Модель пристрою";

    static public final String CALCULATION_INPUT_PARAM_EQUIPMENT_MODEL_KEY = "equipment_model";
    static public final String CALCULATION_INPUT_PARAM_EQUIPMENT_MODEL_TITLE = "Модель причіпного пристрою";
    //other
    static public final String CALCULATION_INPUT_PARAM_WORK_WITH_KEY = "work_width";
    static public final String CALCULATION_INPUT_PARAM_WORK_WITH_TITLE = "Ширина захоплення";
    static public final String CALCULATION_INPUT_PARAM_WORK_WITH_METRICS = "м";

    static public final String CALCULATION_INPUT_PARAM_WORK_AREA_KEY = "work_area";
    static public final String CALCULATION_INPUT_PARAM_WORK_AREA_TITLE = "Площа обробки";
    static public final String CALCULATION_INPUT_PARAM_WORK_AREA_METRICS = "га";

    static public final String CALCULATION_INPUT_PARAM_FUEL_HOUR_KEY = "fuel_per_hour";
    static public final String CALCULATION_INPUT_PARAM_FUEL_HOUR_TITLE = "Витрата палива";
    static public final String CALCULATION_INPUT_PARAM_FUEL_HOUR_METRICS = "л/год";

    static public final String CALCULATION_INPUT_PARAM_FUEL_VOLUME_KEY = "fuel_tank_volume";
    static public final String CALCULATION_INPUT_PARAM_FUEL_VOLUME_TITLE = "Ємність бензобака";
    static public final String CALCULATION_INPUT_PARAM_FUEL_VOLUME_METRICS = "л";

    static public final String CALCULATION_INPUT_PARAM_EARTH_TYPE_KEY = "priming_type";
    static public final String CALCULATION_INPUT_PARAM_EARTH_TYPE_TITLE = "Тип ґрунту";

    static public final String CALCULATION_INPUT_PARAM_WEATHER_KEY = "weather";
    static public final String CALCULATION_INPUT_PARAM_WEATHER_TITLE = "Погодні умови";

    static public final String CALCULATION_INPUT_PARAM_APPLY_KEY = "applying";
    static public final String CALCULATION_INPUT_PARAM_APPLY_TITLE = "Тип застосування";

    static public final String CALCULATION_INPUT_CAR_COUNT_KEY = "machinery_qty";
    static public final String CALCULATION_INPUT_CAR_COUNT_TITLE = "Кількість одиниць техніки";
    static public final String CALCULATION_INPUT_CAR_COUNT_METRICS = "шт";

    //CALCULATION OUTPUT DATA
    static public final String CALCULATION_OUTPUT_PARAM_SPEED_KEY = "speed";
    static public final String CALCULATION_OUTPUT_PARAM_SPEED_TITLE = "Розрахункова швидкість агрегату";
    static public final String CALCULATION_OUTPUT_PARAM_SPEED_METRICS = "км/год";

    static public final String CALCULATION_OUTPUT_PRODUCTIVITY_KEY = "productivity";
    static public final String CALCULATION_OUTPUT_PRODUCTIVITY_TITLE = "Продуктивність, сумарна";
    static public final String CALCULATION_OUTPUT_PRODUCTIVITY_METRICS = "га/год";

    static public final String CALCULATION_OUTPUT_TIME_REQUIRED_KEY = "time_required";
    static public final String CALCULATION_OUTPUT_TIME_REQUIRED_TITLE = "Витрати часу на виконання всього обсягу робіт";
    static public final String CALCULATION_OUTPUT_TIME_REQUIRED_METRICS = "год";
    static public final String CALCULATION_OUTPUT_TIME_REQUIRED_METRICS_MIN = "хв";
    static public final String CALCULATION_OUTPUT_TIME_REQUIRED_METRICS_DAYS = "дн";

    static public final String CALCULATION_OUTPUT_FUEL_REQUIRED_KEY = "fuel_required";
    static public final String CALCULATION_OUTPUT_FUEL_REQUIRED_TITLE = "Загальні витрата палива";
    static public final String CALCULATION_OUTPUT_FUEL_REQUIRED_METRICS = "л";

    static public final String CALCULATION_OUTPUT_REFURLING_REQ_KEY = "refueling_required";
    static public final String CALCULATION_OUTPUT_REFURLING_REQ_TITLE = "Кількість дозаправок палива, сумарна";
    static public final String CALCULATION_OUTPUT_REFURLING_REQ_METRICS = "шт";
}
