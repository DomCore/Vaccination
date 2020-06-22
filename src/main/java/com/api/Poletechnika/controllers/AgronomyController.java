package com.api.Poletechnika.controllers;


import com.api.Poletechnika.exceptions.AccessException;
import com.api.Poletechnika.exceptions.NotFoundException;
import com.api.Poletechnika.exceptions.ServerException;
import com.api.Poletechnika.exceptions.WrongDataException;
import com.api.Poletechnika.models.*;
import com.api.Poletechnika.repository.*;
import com.api.Poletechnika.utils.CalculatorDataSetInfoUtil;
import com.api.Poletechnika.utils.Constants;
import com.api.Poletechnika.utils.CustomListPagination;
import com.api.Poletechnika.utils.FilterUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@RestController
@RequestMapping(value = "/agronomy")
public class AgronomyController {

    @Autowired
    UserPermissionRepository userPermissionRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    AgronomyConditionRepository agronomyConditionRepository;
    @Autowired
    AgronomyMachineryRepository agronomyMachineryRepository;
    @Autowired
    AgronomyMachineryApplyingRepository agronomyMachineryApplyingRepository;
    @Autowired
    AgronomyMachinerySubstancesRepository agronomyMachinerySubstancesRepository;
    @Autowired
    AgronomyEquipmentRepository agronomyEquipmentRepository;
    @Autowired
    AgronomyEarthRepository agronomyEarthRepository;
    @Autowired
    EarthRegionFeatureRepository earthRegionFeatureRepository;

    @Autowired
    AgronomyEarthTypeCharacteristicsRepository agronomyEarthTypeCharacteristicsRepository;

    @Autowired
    AgronomyWeatherRepository agronomyWeatherRepository;
    @Autowired
    AgronomyMachineryModelRepository agronomyMachineryModelRepository;

    @Autowired
    AgronomyMachineryModelShortRepository agronomyMachineryModelShortRepository;

    @Autowired
    MachineryCharacteristicsGeneralRepository machineryCharacteristicsGeneralRepository;
    @Autowired
    MachineryCharacteristicsSimpleRepository machineryCharacteristicsSimpleRepository;
    @Autowired
    MachineryCharacteristicsPatternsRepository machineryCharacteristicsPatternsRepository;


    @Autowired
    EquiomentCharacteristicsGeneralRepository equiomentCharacteristicsGeneralRepository;
    @Autowired
    EquipmentCharacteristicsSimpleRepository equipmentCharacteristicsSimpleRepository;
    @Autowired
    EquipmentCharacteristicsPatternsRepository equipmentCharacteristicsPatternsRepository;


    @Autowired
    AgronomyEarthTypesRepository agronomyEarthTypesRepository;
    @Autowired
    AgronomyEarthRegionsRepository agronomyEarthRegionsRepository;
    @Autowired
    AgronomyEarthCategoriesRepository agronomyEarthCategoriesRepository;


    @Autowired
    UserCalculationRepository userCalculationRepository;


    @Autowired
    UserDataController userDataController;


    //START NEW CALCULATION
    @RequestMapping(value = "", method = RequestMethod.POST)
    public UserCalculationAllData createCalculation (@RequestHeader("Authorization") String token, @RequestParam(value = "user_id") int user_id,
            @RequestParam(defaultValue = "") String map_area,
            @RequestParam String work_area, @RequestParam int earth_feature_id, @RequestParam String weather_id,
            @RequestParam int car_id,  @RequestParam(defaultValue = "1") int car_count,
            @RequestParam(defaultValue = "0") int equipment_id, @RequestParam(defaultValue = "0") int substance_id) {


        //String cry = AesEncriptionUtil.encrypt(code);
        //return "encr - " + cry + "    decr - " + AesEncriptionUtil.decrypt(cry);

        User user = userRepository.findById(user_id);
        if(user != null){
            if(user.getToken() != null && token.trim().length() > 0 &&  user.getToken().equals(token)){
                if(user.getLicense().equals(Constants.LICENSE_STATUS_PAID) || user.getLicense().equals(Constants.LICENSE_STATUS_STAFF)){
                    //all good
                }else {
                    if(userCalculationRepository.findAllByUserId(user_id).size() > Constants.SETTING_FRE_CALSULATION_COUNT){
                        throw new WrongDataException(Constants.ERROR_CALCULATION_END_FREE_CALCULATION);
                    }
                }

                //Get all data
                AgronomyCalculatorRequest agronomyCalculatorRequest = new AgronomyCalculatorRequest();
                agronomyCalculatorRequest.setWork_area(work_area);
                agronomyCalculatorRequest.setWeather(weather_id);
                //Количество техники
                agronomyCalculatorRequest.setMachinery_qty(String.valueOf(car_count));
                //Earth
                EarthRegionFeature earthRegionFeature = earthRegionFeatureRepository.findById(earth_feature_id);
                agronomyCalculatorRequest.setPriming_type(agronomyEarthCategoriesRepository.findById(earthRegionFeature.getCategoryId()).getName());
                //Car
                String equipmentModelValue = null; //FOR SAVE INPUT DATA
                AgronomyMachineryModel machineryModel = agronomyMachineryModelRepository.findById(car_id);
                if(machineryModel == null){
                    throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
                }
                //get car characteristecs
                List<MachineryCharacteristicGeneral> carCharacteristics = machineryCharacteristicsGeneralRepository.findAllByIdMachinery(car_id);
                for(MachineryCharacteristicGeneral itemCharct : carCharacteristics){
                    switch (itemCharct.getName()){
                        case Constants.CALCULATION_INPUT_PARAM_FUEL_VOLUME_KEY:
                            agronomyCalculatorRequest.setFuel_tank_volume(itemCharct.getValue());
                            break;
                        case Constants.CALCULATION_INPUT_PARAM_FUEL_HOUR_KEY:
                            agronomyCalculatorRequest.setFuel_per_hour(itemCharct.getValue());
                            break;
                    }
                }
                //Set work widh and applyiment type
                if(machineryModel.isIndependence()){   //get data from car
                    AgronomyMachineryApplying machineryApplying = agronomyMachineryApplyingRepository.findById(machineryModel.getApplyingId());
                    agronomyCalculatorRequest.setApplying(machineryApplying.getName());
                    for(MachineryCharacteristicGeneral itemCharct : carCharacteristics){
                        if(itemCharct.getName().equals(Constants.CALCULATION_INPUT_PARAM_WORK_WITH_KEY)){
                            agronomyCalculatorRequest.setWork_width(itemCharct.getValue());
                        }
                    }
                }else {   //get data from equipment
                    //Get data about equipment
                    AgronomyEquipment equipmentModel = agronomyEquipmentRepository.findById(equipment_id);
                    equipmentModelValue = String.valueOf(equipmentModel.getId());
                    if(equipmentModel == null){
                        throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
                    }
                    List<EquipmentCharacteristicGeneral> equipmentCharacteristics = equiomentCharacteristicsGeneralRepository.findAllByIdEquipment(equipment_id);
                    for(EquipmentCharacteristicGeneral itemCharct : equipmentCharacteristics){
                        if(itemCharct.getName().equals(Constants.CALCULATION_INPUT_PARAM_WORK_WITH_KEY)){
                            agronomyCalculatorRequest.setWork_width(itemCharct.getValue());
                        }
                    }
                    //Set data
                    AgronomyMachineryApplying machineryApplying = agronomyMachineryApplyingRepository.findById(equipmentModel.getApplyingId());
                    agronomyCalculatorRequest.setApplying(machineryApplying.getName());
                }
        //CHECK IF CAR - SPRAYER AND NEED SUBSTANCE CHOISE
                if(agronomyCalculatorRequest.getApplying().equals("sprayer")){
                    AgronomyMachinerySubstance machinerySubstance = agronomyMachinerySubstancesRepository.findById(substance_id);
                    //Set new applying with substance
                    AgronomyMachineryApplying machineryApplying = agronomyMachineryApplyingRepository.findById(machinerySubstance.getApplyingId());
                    agronomyCalculatorRequest.setApplying(machineryApplying.getName());
                }


                // CREATE REQUEST FOR CALCULATOR FROM OBJECT DATA
                String jsonInputData = null;
                //From all object fields to list
                List<UserCalculationData> calculatorRequestItems = new ArrayList<>();
                Field[] fields = agronomyCalculatorRequest.getClass().getDeclaredFields();
                for(Field f : fields){
                    UserCalculationData requestItem = new UserCalculationData();
                    requestItem.setName(f.getName());
                    try {
                        requestItem.setValue((String) f.get(agronomyCalculatorRequest));
                        //set info title and metrics
                        new CalculatorDataSetInfoUtil().addInputDataInfo(f.getName(), requestItem);
                    } catch (IllegalAccessException e) {
                        throw new ServerException(Constants.ERROR_SERVER_ERROR);
                    }
                    calculatorRequestItems.add(requestItem);
                }
                //From list to json array
                ObjectMapper mapperToJson = new ObjectMapper();
                try {
                    jsonInputData = mapperToJson.writeValueAsString(calculatorRequestItems);
                } catch (JsonProcessingException e) {
                    throw new ServerException(Constants.ERROR_SERVER_ERROR);
                }

                //Create request to calculator
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
                map.add("args", jsonInputData);

                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

                ResponseEntity<String> resp = restTemplate.postForEntity(
                        Constants.URL_ARGONOMY_CALCULATOR,
                        request,
                        String.class);

                ObjectMapper mapper = new ObjectMapper();
                AgronomyCalculatorResponse calculatorResponse = null;
                try {
                    calculatorResponse = mapper.readValue(resp.getBody(), AgronomyCalculatorResponse.class);
                } catch (IOException e) {
                    throw new ServerException(Constants.ERROR_SERVER_ERROR);
                }

                //Check errors
                if(calculatorResponse.getSuccess()){
                    if(calculatorResponse.getExist()){
                        //Save data to DB
                        UserCalculationAllData userCalculationAllDataForSave = new UserCalculationAllData();
                        userCalculationAllDataForSave.setArea(map_area);
                        //Save input data
                        List<UserCalculationData> allInputData = new ArrayList<>();
                        //Add global input data
                        //Car type
                        UserCalculationData calculationInputCarType = new UserCalculationData();
                        calculationInputCarType.setName(Constants.CALCULATION_INPUT_PARAM_CAR_TYPE_KEY);
                        calculationInputCarType.setTitle(Constants.CALCULATION_INPUT_PARAM_CAR_TYPE_TITLE);
                        calculationInputCarType.setValue(machineryModel.getTypeId());
                        calculationInputCarType.setMetrics("");
                        allInputData.add(calculationInputCarType);
                        //Car model
                        UserCalculationData calculationInputCarModel = new UserCalculationData();
                        calculationInputCarModel.setName(Constants.CALCULATION_INPUT_PARAM_CAR_MODEL_KEY);
                        calculationInputCarModel.setTitle(Constants.CALCULATION_INPUT_PARAM_CAR_MODEL_TITLE);
                        calculationInputCarModel.setValue(String.valueOf(machineryModel.getId()));
                        calculationInputCarModel.setMetrics("");
                        allInputData.add(calculationInputCarModel);
                        //Equipment model
                        UserCalculationData calculationInputEquiomentModel = new UserCalculationData();
                        calculationInputEquiomentModel.setName(Constants.CALCULATION_INPUT_PARAM_EQUIPMENT_MODEL_KEY);
                        calculationInputEquiomentModel.setTitle(Constants.CALCULATION_INPUT_PARAM_EQUIPMENT_MODEL_TITLE);
                        calculationInputEquiomentModel.setMetrics("");
                        if(equipmentModelValue != null){
                            calculationInputEquiomentModel.setValue(equipmentModelValue);
                        }else {
                            calculationInputEquiomentModel.setValue("");
                        }
                        allInputData.add(calculationInputEquiomentModel);
                        //Add other input data
                        allInputData.addAll(calculatorRequestItems);
                        //SAVE
                        userCalculationAllDataForSave.setInput_data(allInputData);
                        //set output data title and metrics uk lang
                        for(int i = 0; i < calculatorResponse.getData().size(); i++){
                            new CalculatorDataSetInfoUtil().addOutputDataInfo(calculatorResponse.getData().get(i).getName(), calculatorResponse.getData().get(i));
                            if(calculatorResponse.getData().get(i).getName().equals(Constants.CALCULATION_OUTPUT_TIME_REQUIRED_KEY)){
                                new CalculatorDataSetInfoUtil().setCurrenOutputTime(calculatorResponse.getData().get(i));
                            }
                        }
                        userCalculationAllDataForSave.setOutput_data(calculatorResponse.getData());
                        //Create title
                        userCalculationAllDataForSave.setTitle(agronomyEarthRegionsRepository.findById(earthRegionFeature.getRegionId()).getTitle());

                        return userDataController.addUserCalculations(token, user_id, userCalculationAllDataForSave);
                    }else {
                        throw new WrongDataException(Constants.ERROR_CALCULATION_WRONG_CONDITIONS);
                    }
                }else {
 ///////////////Здесь можно обработать массив ошибок и указать на место ошибки
                    throw new WrongDataException(Constants.ERROR_CALCULATION_WRONG_IPUT_PARAMETERS);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);

        }

       /* RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("test", "first.last@example.com");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<String> resp = restTemplate.postForEntity(
                "http://localhost:8081/agronomy/gg",
                request,
                String.class);
        return resp.getBody();*/
    }


    //GET TYPES
    @RequestMapping(value = "/conditions", method =  RequestMethod.GET)
    public Iterable<AgronomyCondition> getConditions(){
        return  agronomyConditionRepository.findAll();
    }

    //GET TYPES MACHINERY
    @RequestMapping(value = "/conditions/machinery/types", method =  RequestMethod.GET)
    public Iterable<AgronomyMachinery> getConditionMachinery(){
        return agronomyMachineryRepository.findAll();
    }

    //GET TYPE EARTH
    @RequestMapping(value = "/conditions/earth", method =  RequestMethod.GET)
    public Iterable<AgronomyEarth> getConditionEarth(){
        return agronomyEarthRepository.findAll();
    }

    //GET TYPE WEATHER
    @RequestMapping(value = "/conditions/weather", method =  RequestMethod.GET)
    public Iterable<AgronomyWeather> getConditionWeather(@RequestParam(defaultValue = "") String title){

        Iterable<AgronomyWeather> weatherAll = agronomyWeatherRepository.findAll();
        if(title.trim().length() > 0){
            List<AgronomyWeather> findedWeather = new ArrayList<>();
            for(AgronomyWeather weatherItem : weatherAll){
                if(new FilterUtil().searchName(weatherItem.getTitle(), title)){
                    findedWeather.add(weatherItem);
                }
            }
            return findedWeather;
        }else {
            return weatherAll;
        }
    }


//Machinery

    //GET Machinery applying
    @RequestMapping(value = "/conditions/machinery/applying", method =  RequestMethod.GET)
    public Iterable<AgronomyMachineryApplying> getMachineryApplying(@RequestParam(defaultValue = "") String title){
        List<AgronomyMachineryApplying> allData = (List<AgronomyMachineryApplying>) agronomyMachineryApplyingRepository.findAll();
        if(title.trim().length() > 0){
            List<AgronomyMachineryApplying> findedData = new ArrayList<>();
            for(AgronomyMachineryApplying item : allData){
                if(new FilterUtil().searchName(item.getTitle(), title)){
                    findedData.add(item);
                }
            }
            return findedData;
        }else {
            return allData;
        }
    }

    //GET Machinery applying item
    @RequestMapping(value = "/conditions/machinery/applying/{id}", method =  RequestMethod.GET)
    public AgronomyMachineryApplying getMachineryApplyingItem(@PathVariable(value = "id") int id){
       return agronomyMachineryApplyingRepository.findById(id);
    }


    //items

    //GET CHARACTERISTICS GENERAL PATTERN
    @RequestMapping(value = "/conditions/machinery/types/characteristicsGeneral", method =  RequestMethod.GET)
    public Iterable<MachineryCharacteristicPattern> getMachineryCharacteristicsPatterns(@RequestParam(defaultValue = "") String title){
        List<MachineryCharacteristicPattern> allModels = (List<MachineryCharacteristicPattern>) machineryCharacteristicsPatternsRepository.findAll();
        if(title.trim().length() > 0){
            List<MachineryCharacteristicPattern> findedModels = new ArrayList<>();
            for(MachineryCharacteristicPattern modelItem : allModels){
                if(new FilterUtil().searchName(modelItem.getTitle(), title)){
                    findedModels.add(modelItem);
                }
            }
            return findedModels;
        }else {
            return allModels;
        }
    }

    //GET HARACTERISTICS GENERAL PATTERN ITEM
    //FOR CHECK ALL VARIABLES
    @RequestMapping(value = "/conditions/machinery/types/characteristicsGeneral/{id}", method =  RequestMethod.GET)
    public MachineryCharacteristicPattern getMachineryCharacteristicsPatternsItem(@PathVariable(value = "id") int id){
        return  machineryCharacteristicsPatternsRepository.findById(id);
    }

    //GET CHARACTERISTICS SIMPLE
    //FOR CHECK ALL VARIABLES
    @RequestMapping(value = "/conditions/machinery/types/characteristicsSimple", method =  RequestMethod.GET)
    public Iterable<MachineryCharacteristicSimple> getMachineryCharacteristicsSimple(@RequestParam(defaultValue = "") String title,
                                                                                     @RequestParam(defaultValue = "") String limit,
                                                                                     @RequestParam(defaultValue = "") String offset){
        List<MachineryCharacteristicSimple> allModels = (List<MachineryCharacteristicSimple>) machineryCharacteristicsSimpleRepository.findAll();

        int offsetValue;
        int limitValue;



        if(title.trim().length() > 0){
            List<MachineryCharacteristicSimple> findedModels = new ArrayList<>();
            for(MachineryCharacteristicSimple modelItem : allModels){
                if(new FilterUtil().searchName(modelItem.getTitle(), title)){
                    findedModels.add(modelItem);
                }
            }

            if(findedModels != null && findedModels.size() > 0){
                List<MachineryCharacteristicSimple> uniqueModels = new ArrayList<>();
                Iterator iterator = findedModels.iterator();
                while (iterator.hasNext()) {
                    MachineryCharacteristicSimple o = (MachineryCharacteristicSimple) iterator.next();

                    boolean contains = false;
                    for(MachineryCharacteristicSimple item : uniqueModels){
                        if(item.getTitle().equals(o.getTitle())){
                            contains = true;
                        }
                    }

                    if(!contains) uniqueModels.add(o);
                }
                offsetValue = new CustomListPagination().getOffset(offset, uniqueModels.size());
                limitValue = new CustomListPagination().getLimit(limit, offsetValue, uniqueModels.size());
                return uniqueModels.subList(offsetValue, limitValue);
            }else {
                offsetValue = new CustomListPagination().getOffset(offset, findedModels.size());
                limitValue = new CustomListPagination().getLimit(limit, offsetValue, findedModels.size());
                return findedModels.subList(offsetValue, limitValue);
            }

        }else {
            if(allModels != null && allModels.size() > 0){
                List<MachineryCharacteristicSimple> uniqueModels = new ArrayList<>();
                Iterator iterator = allModels.iterator();
                while (iterator.hasNext()) {
                    MachineryCharacteristicSimple o = (MachineryCharacteristicSimple) iterator.next();

                    boolean contains = false;
                    for(MachineryCharacteristicSimple item : uniqueModels){
                        if(item.getTitle().equals(o.getTitle())){
                            contains = true;
                        }
                    }

                    if(!contains) uniqueModels.add(o);
                }
                offsetValue = new CustomListPagination().getOffset(offset, uniqueModels.size());
                limitValue = new CustomListPagination().getLimit(limit, offsetValue, uniqueModels.size());
                return uniqueModels.subList(offsetValue, limitValue);
            }else {
                return allModels;
            }
        }
    }

    //GET CHARACTERISTICS SIMPLE ITEM
    //FOR CHECK ALL VARIABLES
    @RequestMapping(value = "/conditions/machinery/types/characteristicsSimple/{id}", method =  RequestMethod.GET)
    public MachineryCharacteristicSimple getMachineryCharacteristicsSimpleItem(@PathVariable(value = "id") int id){
        return  machineryCharacteristicsSimpleRepository.findById(id);
    }

    //GET MACHINERY ITEMS
    @RequestMapping(value = "/conditions/machinery/types/{type-id}", method =  RequestMethod.GET)
    public Iterable<AgronomyMachineryModelShort> getMachineryModels(@PathVariable(value = "type-id") String typeId,
                                                                    @RequestParam(defaultValue = "") String title){

        Iterable<AgronomyMachineryModelShort> modelsAll = agronomyMachineryModelShortRepository.findAllByTypeId(typeId);
        if(title.trim().length() > 0){
            List<AgronomyMachineryModelShort> findedModels = new ArrayList<>();
            for(AgronomyMachineryModelShort modelItem : modelsAll){
                if(new FilterUtil().searchName(modelItem.getTitle(), title)){   //userItem.getName().equals(name)
                    findedModels.add(modelItem);
                }
            }
            return findedModels;
        }else {
            return modelsAll;
        }
    }

    //ADD MACHINERY ITEM
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/machinery/types/{type-id}", method =  RequestMethod.POST)
    public AgronomyMachineryModelForJSON addMachineryModel(@RequestHeader("Authorization") String token, @PathVariable(value = "type-id") String typeId,
                                    @RequestParam int person_id, @RequestParam String title, @RequestParam String description,
                                    @RequestParam(defaultValue = "1") boolean independence, @RequestParam(defaultValue = "999999") int applying_id,
                                    @RequestParam String characteristics_general, @RequestParam(defaultValue = "") String characteristics_simple){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                //ADD NEW
                AgronomyMachineryModel machineryModel = new AgronomyMachineryModel();
                if(title.trim().length() > 0 && description.trim().length() > 0 && characteristics_general.trim().length() > 0){
                    machineryModel.setTitle(title);
                    machineryModel.setDescription(description);
                    machineryModel.setIndependence(independence);
                    //Set applying
                    if(independence && applying_id == 999999 || independence && applying_id == 0){  //Если устройство независимо, оно должно иметь тип применения
                        throw new WrongDataException("Required int parameter 'applying_id' is not present");
                    }else if(applying_id != 0 && applying_id != 999999 ) {
                        //Check applying_id
                        AgronomyMachineryApplying machineryApplying = agronomyMachineryApplyingRepository.findById(applying_id);
                        if(machineryApplying != null){
                            machineryModel.setApplyingId(applying_id);
                        }else {
                            throw new WrongDataException("Required int parameter 'applying_id' is wrong");
                        }
                    }else {
                        machineryModel.setApplyingId(0);
                    }
                    //SET TYPE
                    AgronomyMachinery machineryType = agronomyMachineryRepository.findById(typeId);
                    if(machineryType != null) {
                        machineryModel.setTypeId(typeId);
                        machineryModel.setTypeTitle(machineryType.getTitle());
                    }else {
                        throw new NotFoundException(Constants.ERROR_TYPE_NOT_FOUND);
                    }
                    //SET GENERAL CHARACTERISTICS
                    JSONArray jsonArray = null;
                    ObjectMapper mapper = new ObjectMapper();
                    MachineryCharacteristicGeneral characteristic = null;
                    try {
                        jsonArray = new JSONArray(characteristics_general);
                        try {
                            characteristic = mapper.readValue(jsonArray.getString(0), MachineryCharacteristicGeneral.class); //test for check if all ok
                            //ADD MODEL TO DB
                            agronomyMachineryModelRepository.save(machineryModel);

                            //CHECK IF GENERAL LIKE PATTERN
                            List<MachineryCharacteristicPattern> patternsArray = (List) getMachineryCharacteristicsPatterns("");
                            if(patternsArray != null && patternsArray.size() > 0){
                                if(jsonArray.length() == patternsArray.size()){
                                    for (MachineryCharacteristicPattern patternItem : patternsArray){
//CHECK IF ARRAY LIKE PATTERN
                                    }
                                }else {
                                    throw new WrongDataException(Constants.ERROR_WRONG_DATA+": "+Constants.ERROR_CHARACTERISTICS_NOT_LIKE_PATTERNS);
                                }
                            }
                            //ADD CHARACT. TO DB
                            for (int i = 0; i < jsonArray.length(); i++) {
                                characteristic = mapper.readValue(jsonArray.getString(i), MachineryCharacteristicGeneral.class);
                                characteristic.setIdMachinery(machineryModel.getId());
                                machineryCharacteristicsGeneralRepository.save(characteristic);
                            }
                        } catch (IOException e) {
                            throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                        }
                    } catch (JSONException e) {
                        throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                    }

                    //ADD SIMPLE CHARACTERISTICS
                    if(characteristics_simple.trim().length() > 0){
                        JSONArray jsonArray2 = null;
                        ObjectMapper mapper2 = new ObjectMapper();
                        MachineryCharacteristicSimple characteristicSimple = null;
                        try {
                            jsonArray2 = new JSONArray(characteristics_simple);
                            try {
                                characteristicSimple = mapper2.readValue(jsonArray2.getString(0), MachineryCharacteristicSimple.class); //test for check if all ok
                                //ADD MODEL TO DB
                                agronomyMachineryModelRepository.save(machineryModel);
                                //ADD CHARACT. TO DB
                                for (int i = 0; i < jsonArray2.length(); i++) {
                                    characteristicSimple = mapper2.readValue(jsonArray2.getString(i), MachineryCharacteristicSimple.class);
                                    characteristicSimple.setIdMachinery(machineryModel.getId());
                                    machineryCharacteristicsSimpleRepository.save(characteristicSimple);
                                }
                            } catch (IOException e) {
                                throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                            }
                        } catch (JSONException e) {
                            throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                        }
                    }
                    return getMachineryModelData(typeId, machineryModel.getId());
                }else {
                    throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }

    //GET MACHINERY ITEM DATA
    @RequestMapping(value = "/conditions/machinery/types/{type-id}/{item-id}", method =  RequestMethod.GET)
    public AgronomyMachineryModelForJSON getMachineryModelData(@PathVariable(value = "type-id") String typeId,
                                                        @PathVariable(value = "item-id") int itemId){

        AgronomyMachineryModel machineryModel = agronomyMachineryModelRepository.findByTypeIdAndId(typeId, itemId);
        List<MachineryCharacteristicGeneral> modelCharacteristicsGeneral = machineryCharacteristicsGeneralRepository.findAllByIdMachinery(itemId);
        List<MachineryCharacteristicSimple> modelCharacteristicsSimple = machineryCharacteristicsSimpleRepository.findAllByIdMachinery(itemId);

        AgronomyMachineryModelForJSON machineryModelForJSON = null;
        if(machineryModel!= null){
            machineryModelForJSON = new AgronomyMachineryModelForJSON();
            machineryModelForJSON.setId(machineryModel.getId());
            machineryModelForJSON.setTitle(machineryModel.getTitle());
            machineryModelForJSON.setType_id(machineryModel.getTypeId());
            machineryModelForJSON.setType_title(machineryModel.getTypeTitle());
            machineryModelForJSON.setDescription(machineryModel.getDescription());
            machineryModelForJSON.setIndependence(machineryModel.isIndependence());
            machineryModelForJSON.setApplying_id(machineryModel.getApplyingId());
            AgronomyMachineryApplying agronomyMachineryApplying = agronomyMachineryApplyingRepository.findById(machineryModel.getApplyingId());
            machineryModelForJSON.setApplying_name(agronomyMachineryApplying.getName());
            machineryModelForJSON.setApplying_title(agronomyMachineryApplying.getTitle());
            if(modelCharacteristicsGeneral != null && modelCharacteristicsGeneral.size() > 0){
                machineryModelForJSON.setCharacteristicsGeneral(modelCharacteristicsGeneral);
            }
            if(modelCharacteristicsSimple != null && modelCharacteristicsSimple.size() > 0){
                machineryModelForJSON.setCharacteristicsSimple(modelCharacteristicsSimple);
            }
        }

        return machineryModelForJSON;
    }

    //UPDATE MACHINERY ITEM DATA
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/machinery/types/{type-id}/{item-id}", method =  RequestMethod.POST)
    public AgronomyMachineryModelForJSON updateMachineryModelData(@PathVariable(value = "type-id") String typeId, @PathVariable(value = "item-id") int itemId,
                                           @RequestHeader("Authorization") String token, @RequestParam int person_id,
                                           @RequestParam(defaultValue = "") String title, @RequestParam(defaultValue = "") String description,
                                           @RequestParam(defaultValue = "") String new_type, @RequestParam(defaultValue = "") String independence, @RequestParam(defaultValue = "999999") int applying_id,
                                           @RequestParam(defaultValue = "") String characteristics_general, @RequestParam(defaultValue = "") String characteristics_simple){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                AgronomyMachineryModel machineryModel = agronomyMachineryModelRepository.findByTypeIdAndId(typeId, itemId);
                //CHECK IF HERE
                if(machineryModel!= null){
                    if(title.trim().length() > 0){
                        machineryModel.setTitle(title);
                    }
                    if(description.trim().length() > 0){
                        machineryModel.setDescription(description);
                    }
                    if(independence.length() > 0 ){
                        if(independence.length() > 1){
                            if(independence.equals("false")){
                                machineryModel.setIndependence(false);
                            }else {
                                machineryModel.setIndependence(true);
                            }
                        }else {
                            int value = 1;
                            try {
                                value = Integer.parseInt(independence);
                            }catch (NumberFormatException e){
                                value = 1;
                            }
                            if(value == 0){
                                machineryModel.setIndependence(false);
                            }else {
                                machineryModel.setIndependence(true);
                            }
                        }
                    }
                    if(applying_id != 999999){
                        if(machineryModel.isIndependence() && applying_id == 0){  //Если устройство независимо, оно должно иметь тип применения
                            throw new WrongDataException("Required int parameter 'applying_id' is wrong");
                        }else if(applying_id != 0) {
                            //Check applying_id
                            AgronomyMachineryApplying machineryApplying = agronomyMachineryApplyingRepository.findById(applying_id);
                            if(machineryApplying != null){
                                machineryModel.setApplyingId(applying_id);
                            }else {
                                throw new WrongDataException("Required int parameter 'applying_id' is wrong");
                            }
                        }else {
                            machineryModel.setApplyingId(0);
                        }
                    }
                    if(machineryModel.isIndependence() && machineryModel.getApplyingId() == 0){
                        throw new WrongDataException("Required int parameter 'applying_id' is not present");
                    }
                    if(new_type.trim().length() > 0){
                        //SET TYPE
                        AgronomyMachinery machineryType = agronomyMachineryRepository.findById(new_type);
                        if(machineryType != null) {
                            machineryModel.setTypeId(new_type);
                            machineryModel.setTypeTitle(machineryType.getTitle());
                        }else {
                            throw new NotFoundException(Constants.ERROR_TYPE_NOT_FOUND);
                        }
                    }
                    if(characteristics_general.trim().length() > 0){
                        //UPDATE CHARACTERISTICS
                        JSONArray jsonArray = null;
                        ObjectMapper mapper = new ObjectMapper();
                        MachineryCharacteristicGeneral characteristic = null;
                        try {
                            jsonArray = new JSONArray(characteristics_general);
                            try {
                                //test for check if all ok
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    characteristic = mapper.readValue(jsonArray.getString(i), MachineryCharacteristicGeneral.class);
                                }
                                //characteristic = mapper.readValue(jsonArray.getString(0), MachineryCharacteristicGeneral.class);
                                if(characteristic != null){
                                    //DELETE OLD DATA
                                    machineryCharacteristicsGeneralRepository.deleteAllByIdMachinery(itemId);
                                    //ADD CHARACT. TO DB
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        characteristic = mapper.readValue(jsonArray.getString(i), MachineryCharacteristicGeneral.class);
                                        characteristic.setIdMachinery(itemId);
                                        machineryCharacteristicsGeneralRepository.save(characteristic);
                                    }
                                }
                            } catch (IOException e) {
                                throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                            }
                        } catch (JSONException e) {
                            throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                        }
                    }
                    //change simple
                    if(characteristics_simple.trim().length() > 0){
                        //UPDATE CHARACTERISTICS
                        JSONArray jsonArray2 = null;
                        ObjectMapper mapper2 = new ObjectMapper();
                        MachineryCharacteristicSimple characteristicSimple = null;
                        try {
                            jsonArray2 = new JSONArray(characteristics_simple);
                            try {
                                //test for check if all ok
                                for (int i = 0; i < jsonArray2.length(); i++) {
                                    characteristicSimple = mapper2.readValue(jsonArray2.getString(i), MachineryCharacteristicSimple.class);
                                }
                                //characteristic = mapper.readValue(jsonArray.getString(0), MachineryCharacteristicGeneral.class);
                                if(characteristicSimple != null){
                                    //DELETE OLD DATA
                                    machineryCharacteristicsSimpleRepository.deleteAllByIdMachinery(itemId);
                                    //ADD CHARACT. TO DB
                                    for (int i = 0; i < jsonArray2.length(); i++) {
                                        characteristicSimple = mapper2.readValue(jsonArray2.getString(i), MachineryCharacteristicSimple.class);
                                        characteristicSimple.setIdMachinery(itemId);
                                        machineryCharacteristicsSimpleRepository.save(characteristicSimple);
                                    }
                                }
                            } catch (IOException e) {
                                throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                            }
                        } catch (JSONException e) {
                            throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                        }
                    }
                    //SAVE DATA
                    agronomyMachineryModelRepository.save(machineryModel);
                    return getMachineryModelData(machineryModel.getTypeId(), itemId);
                }else {
                    throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }

    //DELETE MACHINERY ITEM DATA
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/machinery/types/{type-id}/{item-id}", method =  RequestMethod.DELETE)
    public void deleteMachineryModelData(@PathVariable(value = "type-id") String typeId, @PathVariable(value = "item-id") int itemId,
                                                                  @RequestHeader("Authorization") String token, @RequestParam int person_id){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                //CHECK IF PRODUCT HERE
                if(agronomyMachineryModelRepository.findByTypeIdAndId(typeId, itemId) != null){
                    agronomyMachineryModelRepository.deleteAgronomyMachineryModelByTypeIdAndId(typeId, itemId);
                    machineryCharacteristicsGeneralRepository.deleteAllByIdMachinery(itemId);
                    machineryCharacteristicsSimpleRepository.deleteAllByIdMachinery(itemId);
                }else {
                    throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }

//SUBSTANCES
    //GET MACHINERY SUBSTANCES FOR WORK
    @RequestMapping(value = "/conditions/machinery/substances", method =  RequestMethod.GET)
    public Iterable<AgronomyMachinerySubstance> getMachinerySubstances(){
        return agronomyMachinerySubstancesRepository.findAll();
    }
    //GET MACHINERY SUBSTANCE ITEM
    @RequestMapping(value = "/conditions/machinery/substances/{id}", method =  RequestMethod.GET)
    public AgronomyMachinerySubstance getMachinerySubstances(@PathVariable(value = "id") int id){
        return agronomyMachinerySubstancesRepository.findById(id);
    }


//EQUIPMENT

    //CHARACTERISTICS

    //GET CHARACTERISTICS GENERAL PATTERN
    @RequestMapping(value = "/conditions/machinery/equipment/characteristicsGeneral", method =  RequestMethod.GET)
    public Iterable<EquipmentCharacteristicPattern> getEquipnemtCharacteristicsPatterns(@RequestParam(defaultValue = "") String title){
        List<EquipmentCharacteristicPattern> allModels = (List<EquipmentCharacteristicPattern>) equipmentCharacteristicsPatternsRepository.findAll();
        if(title.trim().length() > 0){
            List<EquipmentCharacteristicPattern> findedModels = new ArrayList<>();
            for(EquipmentCharacteristicPattern modelItem : allModels){
                if(new FilterUtil().searchName(modelItem.getTitle(), title)){
                    findedModels.add(modelItem);
                }
            }
            return findedModels;
        }else {
            return allModels;
        }
    }
    //GET EQUIPMENT CHARACTERISTICS GENERAL PATTERN ITEM
    //FOR CHECK ALL VARIABLES
    @RequestMapping(value = "/conditions/machinery/equipment/characteristicsGeneral/{id}", method =  RequestMethod.GET)
    public EquipmentCharacteristicPattern getEquipnemtCharacteristicsPatternsItem(@PathVariable(value = "id") int id){
        return  equipmentCharacteristicsPatternsRepository.findById(id);
    }


    //GET EQUIPMENT CHARACTERISTICS SIMPLE
    //FOR CHECK ALL VARIABLES
    @RequestMapping(value = "/conditions/machinery/equipment/characteristicsSimple", method =  RequestMethod.GET)
    public Iterable<EquipmentCharacteristicSimple> getEquipnemtCharacteristicsSimple(@RequestParam(defaultValue = "") String title,
                                                                                     @RequestParam(defaultValue = "") String limit,
                                                                                     @RequestParam(defaultValue = "") String offset){
        List<EquipmentCharacteristicSimple> allModels = (List<EquipmentCharacteristicSimple>) equipmentCharacteristicsSimpleRepository.findAll();

        int offsetValue;
        int limitValue;



        if(title.trim().length() > 0){
            List<EquipmentCharacteristicSimple> findedModels = new ArrayList<>();
            for(EquipmentCharacteristicSimple modelItem : allModels){
                if(new FilterUtil().searchName(modelItem.getTitle(), title)){
                    findedModels.add(modelItem);
                }
            }

            if(findedModels != null && findedModels.size() > 0){
                List<EquipmentCharacteristicSimple> uniqueModels = new ArrayList<>();
                Iterator iterator = findedModels.iterator();
                while (iterator.hasNext()) {
                    EquipmentCharacteristicSimple o = (EquipmentCharacteristicSimple) iterator.next();

                    boolean contains = false;
                    for(EquipmentCharacteristicSimple item : uniqueModels){
                        if(item.getTitle().equals(o.getTitle())){
                            contains = true;
                        }
                    }

                    if(!contains) uniqueModels.add(o);
                }
                offsetValue = new CustomListPagination().getOffset(offset, uniqueModels.size());
                limitValue = new CustomListPagination().getLimit(limit, offsetValue, uniqueModels.size());
                return uniqueModels.subList(offsetValue, limitValue);
            }else {
                offsetValue = new CustomListPagination().getOffset(offset, findedModels.size());
                limitValue = new CustomListPagination().getLimit(limit, offsetValue, findedModels.size());
                return findedModels.subList(offsetValue, limitValue);
            }

        }else {
            if(allModels != null && allModels.size() > 0){
                List<EquipmentCharacteristicSimple> uniqueModels = new ArrayList<>();
                Iterator iterator = allModels.iterator();
                while (iterator.hasNext()) {
                    EquipmentCharacteristicSimple o = (EquipmentCharacteristicSimple) iterator.next();

                    boolean contains = false;
                    for(EquipmentCharacteristicSimple item : uniqueModels){
                        if(item.getTitle().equals(o.getTitle())){
                            contains = true;
                        }
                    }

                    if(!contains) uniqueModels.add(o);
                }
                offsetValue = new CustomListPagination().getOffset(offset, uniqueModels.size());
                limitValue = new CustomListPagination().getLimit(limit, offsetValue, uniqueModels.size());
                return uniqueModels.subList(offsetValue, limitValue);
            }else {
                return allModels;
            }
        }
    }
    //GET EQUIPMENT CHARACTERISTICS SIMPLE ITEM
    //FOR CHECK ALL VARIABLES
    @RequestMapping(value = "/conditions/machinery/equipment/characteristicsSimple/{id}", method =  RequestMethod.GET)
    public EquipmentCharacteristicSimple getEquipmentCharacteristicsSimpleItem(@PathVariable(value = "id") int id){
        return  equipmentCharacteristicsSimpleRepository.findById(id);
    }


    //ITEMS EQUIPMENTS

    //GET EQUIPMENTS ITEMS
    @RequestMapping(value = "/conditions/machinery/equipment", method =  RequestMethod.GET)
    public Iterable<AgronomyEquipment> getEquipments(@RequestParam(defaultValue = "") String title, @RequestParam(defaultValue = "") String parent){

        Iterable<AgronomyEquipment> modelsAll = null;
        if(parent.trim().length() > 0){
            modelsAll = agronomyEquipmentRepository.findByParentId(parent);
        }else {
            modelsAll = agronomyEquipmentRepository.findAll();
        }

        if(title.trim().length() > 0){
            List<AgronomyEquipment> findedModels = new ArrayList<>();
            for(AgronomyEquipment modelItem : modelsAll){
                if(new FilterUtil().searchName(modelItem.getTitle(), title)){
                    findedModels.add(modelItem);
                }
            }
            return findedModels;
        }else {
            return modelsAll;
        }
    }

    //ADD EQUIPMENT ITEM
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/machinery/equipment", method =  RequestMethod.POST)
    public AgronomyEquipmentModelForJSON addEquipmentItem(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                                          @RequestParam String title , @RequestParam String description, @RequestParam String parent_id,
                                                          @RequestParam int applying_id,
                                                          @RequestParam String characteristics_general, @RequestParam(defaultValue = "") String characteristics_simple){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                //ADD NEW
                AgronomyEquipment equipmentModel = new AgronomyEquipment();
                if(title.trim().length() > 0 && description.trim().length() > 0 && characteristics_general.trim().length() > 0 && parent_id.trim().length() > 0){
                    equipmentModel.setTitle(title);
                    equipmentModel.setDescription(description);
                    equipmentModel.setParentId(parent_id);
                    //SET applying id
                    AgronomyMachineryApplying machineryApplying = agronomyMachineryApplyingRepository.findById(applying_id);
                    if(machineryApplying != null){
                        equipmentModel.setApplyingId(applying_id);
                    }else {
                        throw new WrongDataException("Required int parameter 'applying_id' is wrong");
                    }
                    //SET TYPE TITLE
                    AgronomyMachinery machineryType = agronomyMachineryRepository.findById(parent_id);
                    if(machineryType != null) {
                        equipmentModel.setParentTitle(machineryType.getTitle());
                    }else {
                        throw new NotFoundException(Constants.ERROR_TYPE_NOT_FOUND);
                    }
                    //SET GENERAL CHARACTERISTICS
                    JSONArray jsonArray = null;
                    ObjectMapper mapper = new ObjectMapper();
                    EquipmentCharacteristicGeneral characteristic = null;
                    try {
                        jsonArray = new JSONArray(characteristics_general);
                        try {
                            characteristic = mapper.readValue(jsonArray.getString(0), EquipmentCharacteristicGeneral.class); //test for check if all ok
                            //ADD MODEL TO DB
                            agronomyEquipmentRepository.save(equipmentModel);

                            //CHECK IF GENERAL LIKE PATTERN
                            List<EquipmentCharacteristicPattern> patternsArray = (List) getEquipnemtCharacteristicsPatterns("");
                            if(patternsArray != null && patternsArray.size() > 0){
                                if(jsonArray.length() == patternsArray.size()){
                                    for (EquipmentCharacteristicPattern patternItem : patternsArray){
//CHECK IF ARRAY LIKE PATTERN
                                    }
                                }else {
                                    throw new WrongDataException(Constants.ERROR_WRONG_DATA+": "+Constants.ERROR_CHARACTERISTICS_NOT_LIKE_PATTERNS);
                                }
                            }
                            //ADD CHARACT. TO DB
                            for (int i = 0; i < jsonArray.length(); i++) {
                                characteristic = mapper.readValue(jsonArray.getString(i), EquipmentCharacteristicGeneral.class);
                                characteristic.setIdEquipment(equipmentModel.getId());
                                equiomentCharacteristicsGeneralRepository.save(characteristic);
                            }
                        } catch (IOException e) {
                            throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                        }
                    } catch (JSONException e) {
                        throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                    }

                    //ADD SIMPLE CHARACTERISTICS
                    if(characteristics_simple.trim().length() > 0){
                        JSONArray jsonArray2 = null;
                        ObjectMapper mapper2 = new ObjectMapper();
                        EquipmentCharacteristicSimple characteristicSimple = null;
                        try {
                            jsonArray2 = new JSONArray(characteristics_simple);
                            try {
                                characteristicSimple = mapper2.readValue(jsonArray2.getString(0), EquipmentCharacteristicSimple.class); //test for check if all ok
                                //ADD MODEL TO DB
                                agronomyEquipmentRepository.save(equipmentModel);
                                //ADD CHARACT. TO DB
                                for (int i = 0; i < jsonArray2.length(); i++) {
                                    characteristicSimple = mapper2.readValue(jsonArray2.getString(i), EquipmentCharacteristicSimple.class);
                                    characteristicSimple.setIdEquipment(equipmentModel.getId());
                                    equipmentCharacteristicsSimpleRepository.save(characteristicSimple);
                                }
                            } catch (IOException e) {
                                throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                            }
                        } catch (JSONException e) {
                            throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                        }
                    }
                    return getEquipmentItemData(equipmentModel.getId());
                }else {
                    throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }

    //GET EQUIPMENT ITEM DATA
    @RequestMapping(value = "/conditions/machinery/equipment/{item-id}", method =  RequestMethod.GET)
    public AgronomyEquipmentModelForJSON getEquipmentItemData(@PathVariable(value = "item-id") int itemId){

        AgronomyEquipment agronomyEquipment = agronomyEquipmentRepository.findById(itemId);
        List<EquipmentCharacteristicGeneral> equipmentCharacteristicGenerals = equiomentCharacteristicsGeneralRepository.findAllByIdEquipment(itemId);
        List<EquipmentCharacteristicSimple> equipmentCharacteristicSimples = equipmentCharacteristicsSimpleRepository.findAllByIdEquipment(itemId);

        AgronomyEquipmentModelForJSON equipmentModelForJSON = null;
        if(agronomyEquipment!= null){
            equipmentModelForJSON = new AgronomyEquipmentModelForJSON();
            equipmentModelForJSON.setId(agronomyEquipment.getId());
            equipmentModelForJSON.setTitle(agronomyEquipment.getTitle());
            equipmentModelForJSON.setParentId(agronomyEquipment.getParentId());
            equipmentModelForJSON.setParentTitle(agronomyEquipment.getParentTitle());
            equipmentModelForJSON.setDescription(agronomyEquipment.getDescription());
            equipmentModelForJSON.setApplying_id(agronomyEquipment.getApplyingId());
            AgronomyMachineryApplying agronomyMachineryApplying = agronomyMachineryApplyingRepository.findById(agronomyEquipment.getApplyingId());
            equipmentModelForJSON.setApplying_name(agronomyMachineryApplying.getName());
            equipmentModelForJSON.setApplying_title(agronomyMachineryApplying.getTitle());
            if(equipmentCharacteristicGenerals != null && equipmentCharacteristicGenerals.size() > 0){
                equipmentModelForJSON.setCharacteristicsGeneral(equipmentCharacteristicGenerals);
            }
            if(equipmentCharacteristicSimples != null && equipmentCharacteristicSimples.size() > 0){
                equipmentModelForJSON.setCharacteristicsSimple(equipmentCharacteristicSimples);
            }
        }
        return equipmentModelForJSON;
    }

    //UPDATE EQUIPMENT ITEM DATA
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/machinery/equipment/{item-id}", method =  RequestMethod.POST)
    public AgronomyEquipmentModelForJSON updateEquipmentItemData(@PathVariable(value = "item-id") int itemId,
                                                                 @RequestHeader("Authorization") String token, @RequestParam int person_id,
                                                                 @RequestParam String title , @RequestParam String description, @RequestParam String parent_id, @RequestParam(defaultValue = "999999") int applying_id,
                                                                 @RequestParam(defaultValue = "") String characteristics_general, @RequestParam(defaultValue = "") String characteristics_simple){

        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                AgronomyEquipment equipmentItem = agronomyEquipmentRepository.findById(itemId);
                //CHECK IF HERE
                if(equipmentItem!= null){
                    if(title.trim().length() > 0){
                        equipmentItem.setTitle(title);
                    }
                    if(description.trim().length() > 0){
                        equipmentItem.setDescription(description);
                    }
                    if(applying_id != 999999){
                        AgronomyMachineryApplying machineryApplying = agronomyMachineryApplyingRepository.findById(applying_id);
                        if(machineryApplying != null){
                            equipmentItem.setApplyingId(applying_id);
                        }else {
                            throw new WrongDataException("Required int parameter 'applying_id' is wrong");
                        }
                    }
                    if(parent_id.trim().length() > 0){
                        //SET PARENT
                        AgronomyMachinery machineryType = agronomyMachineryRepository.findById(parent_id);
                        if(machineryType != null) {
                            equipmentItem.setParentId(parent_id);
                            equipmentItem.setParentTitle(machineryType.getTitle());
                        }else {
                            throw new NotFoundException(Constants.ERROR_TYPE_NOT_FOUND);
                        }
                    }
                    //change general
                    if(characteristics_general.trim().length() > 0){
                        //UPDATE CHARACTERISTICS
                        JSONArray jsonArray = null;
                        ObjectMapper mapper = new ObjectMapper();
                        EquipmentCharacteristicGeneral characteristic = null;
                        try {
                            jsonArray = new JSONArray(characteristics_general);
                            try {
                                //test for check if all ok
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    characteristic = mapper.readValue(jsonArray.getString(i), EquipmentCharacteristicGeneral.class);
                                }
                                //characteristic = mapper.readValue(jsonArray.getString(0), MachineryCharacteristicGeneral.class);
                                if(characteristic != null){
                                    //DELETE OLD DATA
                                    equiomentCharacteristicsGeneralRepository.deleteAllByIdEquipment(itemId);
                                    //ADD CHARACT. TO DB
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        characteristic = mapper.readValue(jsonArray.getString(i), EquipmentCharacteristicGeneral.class);
                                        characteristic.setIdEquipment(itemId);
                                        equiomentCharacteristicsGeneralRepository.save(characteristic);
                                    }
                                }
                            } catch (IOException e) {
                                throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                            }
                        } catch (JSONException e) {
                            throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                        }
                    }
                    //change simple
                    if(characteristics_simple.trim().length() > 0){
                        //UPDATE CHARACTERISTICS
                        JSONArray jsonArray2 = null;
                        ObjectMapper mapper2 = new ObjectMapper();
                        EquipmentCharacteristicSimple characteristicSimple = null;
                        try {
                            jsonArray2 = new JSONArray(characteristics_simple);
                            try {
                                //test for check if all ok
                                for (int i = 0; i < jsonArray2.length(); i++) {
                                    characteristicSimple = mapper2.readValue(jsonArray2.getString(i), EquipmentCharacteristicSimple.class);
                                }
                                //characteristic = mapper.readValue(jsonArray.getString(0), MachineryCharacteristicGeneral.class);
                                if(characteristicSimple != null){
                                    //DELETE OLD DATA
                                    equipmentCharacteristicsSimpleRepository.deleteAllByIdEquipment(itemId);
                                    //ADD CHARACT. TO DB
                                    for (int i = 0; i < jsonArray2.length(); i++) {
                                        characteristicSimple = mapper2.readValue(jsonArray2.getString(i), EquipmentCharacteristicSimple.class);
                                        characteristicSimple.setIdEquipment(itemId);
                                        equipmentCharacteristicsSimpleRepository.save(characteristicSimple);
                                    }
                                }
                            } catch (IOException e) {
                                throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                            }
                        } catch (JSONException e) {
                            throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                        }
                    }
                    //SAVE DATA
                    agronomyEquipmentRepository.save(equipmentItem);
                    return getEquipmentItemData(itemId);
                }else {
                    throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }

    //DELETE EQUIPMENT ITEM DATA
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/machinery/equipment/{item-id}", method =  RequestMethod.DELETE)
    public void deleteEquipmentItemData(@PathVariable(value = "item-id") int itemId,
                                         @RequestHeader("Authorization") String token, @RequestParam int person_id){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                //CHECK IF PRODUCT HERE
                if(agronomyEquipmentRepository.findById(itemId) != null){
                    agronomyEquipmentRepository.deleteById(itemId);
                    equiomentCharacteristicsGeneralRepository.deleteAllByIdEquipment(itemId);
                    equipmentCharacteristicsSimpleRepository.deleteAllByIdEquipment(itemId);
                }else {
                    throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }



//EARTH

    //GET EARTH TYPES
    @RequestMapping(value = "/conditions/earth/types", method =  RequestMethod.GET)
    public MappingJacksonValue getEarthTypes(@RequestParam(defaultValue = "") String title) {

        SimpleBeanPropertyFilter filterJson = SimpleBeanPropertyFilter.filterOutAllExcept("id", "title");
        FilterProvider filtersForJson = new SimpleFilterProvider().addFilter("SomeBeanFilterEarth", filterJson);
        MappingJacksonValue mapping;

        Iterable<AgronomyEarthType> earthAll = agronomyEarthTypesRepository.findAll();

        /*if(region.trim().length() >0){
            Iterator<AgronomyEarthType> it = earthAll.iterator();
            while (it.hasNext()) {
                AgronomyEarthType agronomyEarthType = it.next();
                if (!new FilterUtil().searchFilter(agronomyEarthType.getRegionId(), region)) {
                    it.remove();
                }
            }
        }*/

        if(title.trim().length() > 0){
            List<AgronomyEarthType> findedEarth = new ArrayList<>();
            for(AgronomyEarthType earthItem : earthAll){
                if(new FilterUtil().searchName(earthItem.getTitle(), title)){
                    findedEarth.add(earthItem);
                }
            }
            mapping = new MappingJacksonValue(findedEarth);

        }else {
            mapping = new MappingJacksonValue(earthAll);
        }
        mapping.setFilters(filtersForJson);
        return mapping;
    }

    //ADD EARTH TYPE
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/earth/types", method =  RequestMethod.POST)
    public MappingJacksonValue addEarthType(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                                              @RequestParam String title, @RequestParam String characteristics){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                if(title.trim().length() > 0){
                    //ADD NEW EART TYPE
                    AgronomyEarthType agronomyEarthType = new AgronomyEarthType();
                    agronomyEarthType.setTitle(title);

                    //SET CHARACTERISTICS
                    JSONArray jsonArray = null;
                    ObjectMapper mapper = new ObjectMapper();
                    AgronomyEarthTypeCharacteristic characteristic = null;
                    try {
                        jsonArray = new JSONArray(characteristics);
                        try {
                            //test for check if all ok
                            for (int i = 0; i < jsonArray.length(); i++) {
                                characteristic = mapper.readValue(jsonArray.getString(i), AgronomyEarthTypeCharacteristic.class);
                            }
                            //ADD MODEL TO DB
                            agronomyEarthTypesRepository.save(agronomyEarthType);
                            //ADD CHARACT. TO DB
                            for (int i = 0; i < jsonArray.length(); i++) {
                                characteristic = mapper.readValue(jsonArray.getString(i), AgronomyEarthTypeCharacteristic.class);
                                characteristic.setEarthTypeId(agronomyEarthType.getId());
                                agronomyEarthTypeCharacteristicsRepository.save(characteristic);
                            }
                        } catch (IOException e) {
                            throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                        }
                    } catch (JSONException e) {
                        throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                    }

                    return getEarthTypeItem(agronomyEarthType.getId());
                }
                else {
                    throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }

    //GET EARTH TYPE ITEM
    @RequestMapping(value = "/conditions/earth/types/{id}", method =  RequestMethod.GET)
    public MappingJacksonValue getEarthTypeItem(@PathVariable(value = "id") int id) {

        AgronomyEarthType earthType = agronomyEarthTypesRepository.findById(id);

        SimpleBeanPropertyFilter filterJson = SimpleBeanPropertyFilter.filterOutAllExcept("id", "title", "characteristics");
        FilterProvider filtersForJson = new SimpleFilterProvider().addFilter("SomeBeanFilterEarth", filterJson);
        MappingJacksonValue mapping;

        if(earthType != null){
            List<AgronomyEarthTypeCharacteristic> modelCharacteristics = agronomyEarthTypeCharacteristicsRepository.findAllByEarthTypeId(id);
            if(modelCharacteristics != null){
                earthType.setCharacteristics(modelCharacteristics);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
        }

        mapping = new MappingJacksonValue(agronomyEarthTypesRepository.findById(id));
        mapping.setFilters(filtersForJson);
        return mapping;

    }

    //UPDATE EARTH TYPE ITEM
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/earth/types/{id}", method =  RequestMethod.POST)
    public MappingJacksonValue updateEarthTypeItem(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                              @PathVariable(value = "id") int id, @RequestParam(defaultValue = "") String title,
                                              @RequestParam(defaultValue = "") String characteristics) {
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                //UPDATE EART TYPE
                AgronomyEarthType agronomyEarthType = agronomyEarthTypesRepository.findById(id);
                if(agronomyEarthType != null){
                    if(title.trim().length() > 0){
                        agronomyEarthType.setTitle(title);
                    }
                    if(characteristics.trim().length() > 0){
                        //UPDATE CHARACTERISTICS
                        JSONArray jsonArray = null;
                        ObjectMapper mapper = new ObjectMapper();
                        AgronomyEarthTypeCharacteristic characteristic = null;
                        try {
                            jsonArray = new JSONArray(characteristics);
                            try {
                                //test for check if all ok
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    characteristic = mapper.readValue(jsonArray.getString(i), AgronomyEarthTypeCharacteristic.class);
                                }
                                if(characteristic != null){
                                    //DELETE OLD DATA
                                    agronomyEarthTypeCharacteristicsRepository.deleteAllByEarthTypeId(id);
                                    //ADD CHARACT. TO DB
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        characteristic = mapper.readValue(jsonArray.getString(i), AgronomyEarthTypeCharacteristic.class);
                                        characteristic.setEarthTypeId(id);
                                        agronomyEarthTypeCharacteristicsRepository.save(characteristic);
                                    }
                                }
                            } catch (IOException e) {
                                throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                            }
                        } catch (JSONException e) {
                            throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                        }
                    }
                    agronomyEarthTypesRepository.save(agronomyEarthType);
                    return getEarthTypeItem(agronomyEarthType.getId());
                }else {
                    throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
                }

            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
        }
    }

    //DELETE EARTH TYPE ITEM
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/earth/types/{id}", method =  RequestMethod.DELETE)
    public void deleteEarthTypeItem(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                         @PathVariable(value = "id") int id){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                if(agronomyEarthTypesRepository.findById(id) != null){
                    agronomyEarthTypesRepository.deleteById(id);
                    agronomyEarthTypeCharacteristicsRepository.deleteAllByEarthTypeId(id);
                }else {
                    throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }

    //EARTH TYPES CHARACTERISTICS

    //GET CHARACTERISTICS SIMPLE
    //FOR CHECK ALL VARIABLES
    @RequestMapping(value = "/conditions/earth/types/characteristicsSimple", method =  RequestMethod.GET)
    public Iterable<AgronomyEarthTypeCharacteristic> getEarthCharacteristicsSimple(@RequestParam(defaultValue = "") String title,
                                                                                     @RequestParam(defaultValue = "") String limit,
                                                                                     @RequestParam(defaultValue = "") String offset){
        List<AgronomyEarthTypeCharacteristic> allModels = (List<AgronomyEarthTypeCharacteristic>) agronomyEarthTypeCharacteristicsRepository.findAll();

        int offsetValue;
        int limitValue;



        if(title.trim().length() > 0){
            List<AgronomyEarthTypeCharacteristic> findedModels = new ArrayList<>();
            for(AgronomyEarthTypeCharacteristic modelItem : allModels){
                if(new FilterUtil().searchName(modelItem.getTitle(), title)){
                    findedModels.add(modelItem);
                }
            }

            if(findedModels != null && findedModels.size() > 0){
                List<AgronomyEarthTypeCharacteristic> uniqueModels = new ArrayList<>();
                Iterator iterator = findedModels.iterator();
                while (iterator.hasNext()) {
                    AgronomyEarthTypeCharacteristic o = (AgronomyEarthTypeCharacteristic) iterator.next();

                    boolean contains = false;
                    for(AgronomyEarthTypeCharacteristic item : uniqueModels){
                        if(item.getTitle().equals(o.getTitle())){
                            contains = true;
                        }
                    }

                    if(!contains) uniqueModels.add(o);
                }
                offsetValue = new CustomListPagination().getOffset(offset, uniqueModels.size());
                limitValue = new CustomListPagination().getLimit(limit, offsetValue, uniqueModels.size());
                return uniqueModels.subList(offsetValue, limitValue);
            }else {
                offsetValue = new CustomListPagination().getOffset(offset, findedModels.size());
                limitValue = new CustomListPagination().getLimit(limit, offsetValue, findedModels.size());
                return findedModels.subList(offsetValue, limitValue);
            }

        }else {
            if(allModels != null && allModels.size() > 0){
                List<AgronomyEarthTypeCharacteristic> uniqueModels = new ArrayList<>();
                Iterator iterator = allModels.iterator();
                while (iterator.hasNext()) {
                    AgronomyEarthTypeCharacteristic o = (AgronomyEarthTypeCharacteristic) iterator.next();

                    boolean contains = false;
                    for(AgronomyEarthTypeCharacteristic item : uniqueModels){
                        if(item.getTitle().equals(o.getTitle())){
                            contains = true;
                        }
                    }

                    if(!contains) uniqueModels.add(o);
                }
                offsetValue = new CustomListPagination().getOffset(offset, uniqueModels.size());
                limitValue = new CustomListPagination().getLimit(limit, offsetValue, uniqueModels.size());
                return uniqueModels.subList(offsetValue, limitValue);
            }else {
                return allModels;
            }
        }
    }
    //GET CHARACTERISTICS SIMPLE ITEM
    //FOR CHECK ALL VARIABLES
    @RequestMapping(value = "/conditions/earth/types/characteristicsSimple/{id}", method =  RequestMethod.GET)
    public AgronomyEarthTypeCharacteristic getEarthCharacteristicsSimpleItem(@PathVariable(value = "id") int id){
        return  agronomyEarthTypeCharacteristicsRepository.findById(id);
    }



    //EARTH REGIONS

    //GET EARTH REGIONS
    @RequestMapping(value = "/conditions/earth/regions", method =  RequestMethod.GET)
    public Iterable<AgronomyEarthRegion> getEarthRegions(@RequestParam(defaultValue = "") String title) {
        Iterable<AgronomyEarthRegion> regionsAll = agronomyEarthRegionsRepository.findAll();
        if(title.trim().length() > 0){
            List<AgronomyEarthRegion> findedRegions = new ArrayList<>();
            for(AgronomyEarthRegion regionItem : regionsAll){
                if(new FilterUtil().searchName(regionItem.getTitle(), title)){
                    findedRegions.add(regionItem);
                }
            }
            return findedRegions;
        }else {
            return regionsAll;
        }
    }
    //ADD EARTH REGION
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/earth/regions", method =  RequestMethod.POST)
    public AgronomyEarthRegion addEarthRegion(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                                    @RequestParam(defaultValue = "") String title){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                if(title.trim().length() > 0 ){
                    //ADD NEW EART TYPE
                    AgronomyEarthRegion earthRegion = new AgronomyEarthRegion();
                    earthRegion.setTitle(title);
                    agronomyEarthRegionsRepository.save(earthRegion);
                    return getEarthRegionItem(earthRegion.getId());
                }else {
                    throw new NotFoundException(Constants.ERROR_WRONG_DATA);
                }

            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }
    //GET REGION TYPE ITEM
    @RequestMapping(value = "/conditions/earth/regions/{id}", method =  RequestMethod.GET)
    public AgronomyEarthRegion getEarthRegionItem(@PathVariable(value = "id") int id) {
        return agronomyEarthRegionsRepository.findById(id);
    }
    //UPDATE EARTH REGION ITEM
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/earth/regions/{id}", method =  RequestMethod.POST)
    public AgronomyEarthRegion updateEarthRegionItem(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                              @PathVariable(value = "id") int id, @RequestParam(defaultValue = "") String title) {
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                //CHECK IF NOT NULL
                AgronomyEarthRegion agronomyEarthRegion = agronomyEarthRegionsRepository.findById(id);
                if(agronomyEarthRegion != null){
                    //ADD NEW EART TYPE
                    if(title.trim().length() > 0){
                        agronomyEarthRegion.setTitle(title);
                    }
                    agronomyEarthRegionsRepository.save(agronomyEarthRegion);
                    return agronomyEarthRegion;
                }else {
                    throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }
    //DELETE EARTH REGION ITEM
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/earth/regions/{id}", method =  RequestMethod.DELETE)
    public void deleteEarthRegionItem(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                    @PathVariable(value = "id") int id){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                if(agronomyEarthRegionsRepository.findById(id) != null){
                    agronomyEarthRegionsRepository.deleteById(id);
                }else {
                    throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }


    //EARTH CATEGORIES

    //GET EARTH CATEGORIES
    @RequestMapping(value = "/conditions/earth/categories", method =  RequestMethod.GET)
    public Iterable<AgronomyEarthCategory> getEarthCategories() {
        return agronomyEarthCategoriesRepository.findAll();
    }
    //GET EARTH CATEGORY DATA
    @RequestMapping(value = "/conditions/earth/categories/{id}", method =  RequestMethod.GET)
    public AgronomyEarthCategory getEarthCategoryData(@PathVariable(value = "id") int id) {
        return agronomyEarthCategoriesRepository.findById(id);
    }


    //REGION FEATURES

    //GET EARTH REGION FEATURES
    @RequestMapping(value = "/conditions/earth/regions/{id}/features", method =  RequestMethod.GET)
    public List<EarthRegionFeatureForJSON> getRegionFeatures(@PathVariable(value = "id") int id, @RequestParam(defaultValue = "") String title) {

        List<EarthRegionFeature> itemsAll = earthRegionFeatureRepository.findAllByRegionId(id);

        List<EarthRegionFeatureForJSON> earthRegionFeatureForJSON = null;

        if(itemsAll != null && itemsAll.size() > 0){
            earthRegionFeatureForJSON = new ArrayList<>();
            for(EarthRegionFeature oneItem : itemsAll){
                EarthRegionFeatureForJSON itemJSON = new EarthRegionFeatureForJSON();
                itemJSON.setId(oneItem.getId());
                itemJSON.setDensityTitle(oneItem.getDensityTitle());
                itemJSON.setDensityValue(oneItem.getDensityValue());
                itemJSON.setDensityMetrics(oneItem.getDensityMetrics());
                itemJSON.setEarthTypeId(oneItem.getEarthTypeId());
                itemJSON.setCategoryId(oneItem.getCategoryId());
                //set earth type title
                AgronomyEarthType earthType = agronomyEarthTypesRepository.findById(oneItem.getEarthTypeId());
                itemJSON.setEarthTypeTitle(earthType.getTitle());
                //set category title
                AgronomyEarthCategory earthCategory =  agronomyEarthCategoriesRepository.findById(oneItem.getCategoryId());
                itemJSON.setCategoryTitle(earthCategory.getTitle());
                itemJSON.setCategoryName(earthCategory.getName());
                earthRegionFeatureForJSON.add(itemJSON);
            }

            if(title.trim().length() > 0){
                List<EarthRegionFeatureForJSON> finded = new ArrayList<>();
                for(EarthRegionFeatureForJSON feature : earthRegionFeatureForJSON){
                    if(new FilterUtil().searchName(feature.getEarthTypeTitle(), title)){
                        finded.add(feature);
                    }
                }
                return finded;
            }else {
                return earthRegionFeatureForJSON;
            }
        }else {
            throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
        }
    }
    //ADD EARTH REGION FEATURE
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/earth/regions/{id}/features", method =  RequestMethod.POST)
    public EarthRegionFeatureForJSON addRegionFeature(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                                @PathVariable(value = "id") int id, @RequestParam int earth_type_id,
                                                @RequestParam int category_id,
                                                @RequestParam(defaultValue = "") String density_value){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                if(density_value.trim().length() > 0){
                    if(agronomyEarthRegionsRepository.findById(id) != null && agronomyEarthTypesRepository.findById(earth_type_id) != null){
                        //ADD NEW EART TYPE
                        EarthRegionFeature regionFeature = new EarthRegionFeature();
                        regionFeature.setRegionId(id);
                        regionFeature.setEarthTypeId(earth_type_id);
                        regionFeature.setDensityValue(density_value);
                        regionFeature.setCategoryId(category_id);
                        regionFeature.setDensityTitle(Constants.REGION_FEATURE_DENCITY_TITLE);
                        regionFeature.setDensityMetrics(Constants.REGION_FEATURE_DENCITY_METRIC);
                        earthRegionFeatureRepository.save(regionFeature);
                        return getRegionFeatureItem(id, regionFeature.getId());
                    }else {
                        throw new NotFoundException(Constants.ERROR_WRONG_DATA);
                    }
                }else {
                    throw new NotFoundException(Constants.ERROR_WRONG_DATA);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }

    //GET EARTH REGION FEATURE ITEM
    @RequestMapping(value = "/conditions/earth/regions/{id}/features/{feature-id}", method =  RequestMethod.GET)
    public EarthRegionFeatureForJSON getRegionFeatureItem(@PathVariable(value = "id") int id, @PathVariable(value = "feature-id") int feature_id) {
        EarthRegionFeature regionFeature = earthRegionFeatureRepository.findByIdAndRegionId(feature_id, id);
        if(regionFeature != null){
            EarthRegionFeatureForJSON earthRegionFeatureForJSON = new EarthRegionFeatureForJSON();
            earthRegionFeatureForJSON.setId(regionFeature.getId());
            earthRegionFeatureForJSON.setDensityTitle(regionFeature.getDensityTitle());
            earthRegionFeatureForJSON.setDensityValue(regionFeature.getDensityValue());
            earthRegionFeatureForJSON.setDensityMetrics(regionFeature.getDensityMetrics());
            earthRegionFeatureForJSON.setEarthTypeId(regionFeature.getEarthTypeId());
            earthRegionFeatureForJSON.setCategoryId(regionFeature.getCategoryId());
            //set earth type title
            AgronomyEarthType earthType = agronomyEarthTypesRepository.findById(regionFeature.getEarthTypeId());
            earthRegionFeatureForJSON.setEarthTypeTitle(earthType.getTitle());
            //set category title
            AgronomyEarthCategory earthCategory =  agronomyEarthCategoriesRepository.findById(regionFeature.getCategoryId());
            earthRegionFeatureForJSON.setCategoryTitle(earthCategory.getTitle());
            earthRegionFeatureForJSON.setCategoryName(earthCategory.getName());
            return earthRegionFeatureForJSON;
        }else {
            throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
        }
    }

    //UPDATE EARTH REGION FEATURE ITEM
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/earth/regions/{id}/features/{feature-id}", method =  RequestMethod.POST)
    public EarthRegionFeatureForJSON updateRegionFeatureItem(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                                       @PathVariable(value = "id") int id, @PathVariable(value = "feature-id") int feature_id,
                                                       @RequestParam(defaultValue = "") String density_value, @RequestParam(defaultValue = "") String earth_type_id,
                                                       @RequestParam(defaultValue = "999999") int category_id) {
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                //CHECK IF NOT NULL
                EarthRegionFeature regionFeature = earthRegionFeatureRepository.findByIdAndRegionId(feature_id, id);
                if(agronomyEarthRegionsRepository.findById(id) != null && regionFeature != null){
                    //ADD NEW EART TYPE
                    if(density_value.trim().length() > 0){
                        regionFeature.setDensityValue(density_value);
                    }
                    if(earth_type_id.trim().length() > 0 && agronomyEarthTypesRepository.findById(Integer.parseInt(earth_type_id)) != null){
                        regionFeature.setEarthTypeId(Integer.parseInt(earth_type_id));
                    }
                    if(category_id != 999999){
                        regionFeature.setCategoryId(category_id);
                    }
                    earthRegionFeatureRepository.save(regionFeature);
                    return getRegionFeatureItem(id, regionFeature.getId());
                }else {
                    throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }
    //DELETE REGION FEATURE ITEM
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/earth/regions/{id}/features/{feature-id}", method =  RequestMethod.DELETE)
    public void deleteRegionFeatureItem(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                      @PathVariable(value = "id") int id, @PathVariable(value = "feature-id") int feature_id){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                if(earthRegionFeatureRepository.findById(feature_id) != null){
                    earthRegionFeatureRepository.deleteById(feature_id);
                }else {
                    throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }

 //WEATHER

    //ADD WEATHER
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
/*    @RequestMapping(value = "/conditions/weather", method =  RequestMethod.POST)
    public AgronomyWeather addWeatherItem(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                                        @RequestParam String title, @RequestParam String id){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                if(title.trim().length() > 0 && id.trim().length() > 0){
                    //ADD WEATHER ITEM
                    AgronomyWeather weather = new AgronomyWeather();
                    weather.setTitle(title);
                    weather.setId(id);
                    agronomyWeatherRepository.save(weather);
                    return getWeatherItem(weather.getId());
                }else {
                    throw new NotFoundException(Constants.ERROR_WRONG_DATA);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }
*/
    //GET WEATHER ITEM
    @RequestMapping(value = "/conditions/weather/{id}", method =  RequestMethod.GET)
    public AgronomyWeather getWeatherItem(@PathVariable(value = "id") String id){
        return agronomyWeatherRepository.findById(id);
    }

 /*   //UPDATE WEATHER ITEM
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/weather/{item-id}", method =  RequestMethod.POST)
    public AgronomyWeather updateWeatherItem(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                                     @PathVariable(value = "item-id") String itemId, @RequestParam(defaultValue = "") String title,
                                             @RequestParam(defaultValue = "") String id) {
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                //CHECK IF NOT NULL
                AgronomyWeather weather = agronomyWeatherRepository.findById(itemId);
                if(weather != null){
                    //ADD NEW EART TYPE
                    if(title.trim().length() > 0){
                        weather.setTitle(title);
                    }
                    if(id.trim().length() > 0){
                        weather.setId(id);
                    }
                    agronomyWeatherRepository.save(weather);
                    return getWeatherItem(weather.getId());
                }else {
                    throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }

    //DELETE WEATHER ITEM
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/weather/{id}", method =  RequestMethod.DELETE)
    public void deleteEarthRegionItem(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                      @PathVariable(value = "id") String id){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                if(agronomyWeatherRepository.findById(id) != null){
                    agronomyWeatherRepository.deleteById(id);
                }else {
                    throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }*/
}

