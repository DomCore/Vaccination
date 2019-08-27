package com.api.Poletechnika.controllers;


import com.api.Poletechnika.exceptions.AccessException;
import com.api.Poletechnika.exceptions.NotFoundException;
import com.api.Poletechnika.exceptions.WrongDataException;
import com.api.Poletechnika.models.*;
import com.api.Poletechnika.repository.*;
import com.api.Poletechnika.utils.AesEncriptionUtil;
import com.api.Poletechnika.utils.Constants;
import com.api.Poletechnika.utils.FilterUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    AgronomyEarthRepository agronomyEarthRepository;
    @Autowired
    AgronomyWeatherRepository agronomyWeatherRepository;
    @Autowired
    AgronomyMachineryModelRepository agronomyMachineryModelRepository;
    @Autowired
    AgronomyMachineryModelShortRepository agronomyMachineryModelShortRepository;
    @Autowired
    AgronomyMachineryModelCharacteristicsRepository agronomyMachineryModelCharacteristicsRepository;

    @Autowired
    AgronomyEarthTypesRepository agronomyEarthTypesRepository;
    @Autowired
    AgronomyEarthRegionsRepository agronomyEarthRegionsRepository;

    //START NEW CALCULATION
    @RequestMapping(value = "", method = RequestMethod.POST)
    public String createCalculation () {
        return "START NEW CALCULATION ";
    }
///START zrobut

    //GET TYPES
    @RequestMapping(value = "/conditions", method =  RequestMethod.GET)
    public Iterable<AgronomyCondition> getConditions(){
        return  agronomyConditionRepository.findAll();
    }



    //GET TYPE MACHINERY
    @RequestMapping(value = "/conditions/machinery", method =  RequestMethod.GET)
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

    //GET MACHINERY ITEMS
    @RequestMapping(value = "/conditions/machinery/{type-id}", method =  RequestMethod.GET)
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
    @RequestMapping(value = "/conditions/machinery/{type-id}", method =  RequestMethod.POST)
    public AgronomyMachineryModelForJSON addMachineryModel(@RequestHeader("Authorization") String token, @PathVariable(value = "type-id") String typeId,
                                    @RequestParam int person_id, @RequestParam String title , @RequestParam String description,
                                    @RequestParam String characteristics){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                //ADD NEW
                AgronomyMachineryModel machineryModel = new AgronomyMachineryModel();
                if(title.trim().length() > 0 && description.trim().length() > 0 && characteristics.trim().length() > 0){
                    machineryModel.setTitle(title);
                    machineryModel.setDescription(description);
                    machineryModel.setTypeId(typeId);
                    //SET TYPE TITLE
                    AgronomyMachinery machineryType = agronomyMachineryRepository.findById(typeId);
                    if(machineryType != null) {
                        machineryModel.setTypeTitle(machineryType.getTitle());
                    }else {
                        throw new NotFoundException(Constants.ERROR_TYPE_NOT_FOUND);
                    }
                    //SET CHARACTERISTICS
                    JSONArray jsonArray = null;
                    ObjectMapper mapper = new ObjectMapper();
                    AgronomyMachineryModelCharacteristic characteristic = null;
                    try {
                        jsonArray = new JSONArray(characteristics);
                        try {
                            characteristic = mapper.readValue(jsonArray.getString(0), AgronomyMachineryModelCharacteristic.class); //test for check if all ok
                            //ADD MODEL TO DB
                            agronomyMachineryModelRepository.save(machineryModel);
                            //ADD CHARACT. TO DB
                            for (int i = 0; i < jsonArray.length(); i++) {
                                characteristic = mapper.readValue(jsonArray.getString(i), AgronomyMachineryModelCharacteristic.class);
                                characteristic.setIdMachinery(machineryModel.getId());
                                agronomyMachineryModelCharacteristicsRepository.save(characteristic);
                            }
                        } catch (IOException e) {
                            throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                        }
                    } catch (JSONException e) {
                        throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                    }
                    //return agronomyMachineryModelShortRepository.findAllByTypeId(typeId);
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
    @RequestMapping(value = "/conditions/machinery/{type-id}/{item-id}", method =  RequestMethod.GET)
    public AgronomyMachineryModelForJSON getMachineryModelData(@PathVariable(value = "type-id") String typeId,
                                                        @PathVariable(value = "item-id") int itemId){

        AgronomyMachineryModel machineryModel = agronomyMachineryModelRepository.findByTypeIdAndId(typeId, itemId);
        List<AgronomyMachineryModelCharacteristic> modelCharacteristics = agronomyMachineryModelCharacteristicsRepository.findAllByIdMachinery(itemId);

        AgronomyMachineryModelForJSON machineryModelForJSON = new AgronomyMachineryModelForJSON();
        if(machineryModel!= null){
            machineryModelForJSON.setId(machineryModel.getId());
            machineryModelForJSON.setTitle(machineryModel.getTitle());
            machineryModelForJSON.setType_id(machineryModel.getTypeId());
            machineryModelForJSON.setType_title(machineryModel.getTypeTitle());
            machineryModelForJSON.setDescription(machineryModel.getDescription());
        }
        if(modelCharacteristics != null && modelCharacteristics.size() > 0){
            machineryModelForJSON.setCharacteristics(modelCharacteristics);
        }
        return machineryModelForJSON;
    }

    //UPDATE MACHINERY ITEM DATA
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/machinery/{type-id}/{item-id}", method =  RequestMethod.POST)
    public AgronomyMachineryModelForJSON updateMachineryModelData(@PathVariable(value = "type-id") String typeId, @PathVariable(value = "item-id") int itemId,
                                           @RequestHeader("Authorization") String token, @RequestParam int person_id,
                                           @RequestParam(defaultValue = "") String title, @RequestParam(defaultValue = "") String description,
                                           @RequestParam(defaultValue = "") String characteristics){

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
                    if(characteristics.trim().length() > 0){
                        //UPDATE CHARACTERISTICS
                        JSONArray jsonArray = null;
                        ObjectMapper mapper = new ObjectMapper();
                        AgronomyMachineryModelCharacteristic characteristic = null;
                        try {
                            jsonArray = new JSONArray(characteristics);
                            try {
                                characteristic = mapper.readValue(jsonArray.getString(0), AgronomyMachineryModelCharacteristic.class); //test for check if all ok
                                if(characteristic != null){
                                    //DELETE OLD DATA
                                    agronomyMachineryModelCharacteristicsRepository.deleteAllByIdMachinery(itemId);
                                    //ADD CHARACT. TO DB
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        characteristic = mapper.readValue(jsonArray.getString(i), AgronomyMachineryModelCharacteristic.class);
                                        characteristic.setIdMachinery(itemId);
                                        agronomyMachineryModelCharacteristicsRepository.save(characteristic);
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
                    return getMachineryModelData(typeId, itemId);
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
    @RequestMapping(value = "/conditions/machinery/{type-id}/{item-id}", method =  RequestMethod.DELETE)
    public void deleteMachineryModelData(@PathVariable(value = "type-id") String typeId, @PathVariable(value = "item-id") int itemId,
                                                                  @RequestHeader("Authorization") String token, @RequestParam int person_id){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                //CHECK IF PRODUCT HERE
                if(agronomyMachineryModelRepository.findByTypeIdAndId(typeId, itemId) != null){
                    agronomyMachineryModelRepository.deleteAgronomyMachineryModelByTypeIdAndId(typeId, itemId);
                    agronomyMachineryModelCharacteristicsRepository.deleteAllByIdMachinery(itemId);
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
    public Iterable<AgronomyEarthType> getEarthTypes(@RequestParam(defaultValue = "") String title) {

        Iterable<AgronomyEarthType> earthAll = agronomyEarthTypesRepository.findAll();
        if(title.trim().length() > 0){
            List<AgronomyEarthType> findedEarth = new ArrayList<>();
            for(AgronomyEarthType earthItem : earthAll){
                if(new FilterUtil().searchName(earthItem.getTitle(), title)){
                    findedEarth.add(earthItem);
                }
            }
            return findedEarth;
        }else {
            return earthAll;
        }
    }

    //ADD EARTH TYPE
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/earth/types", method =  RequestMethod.POST)
    public AgronomyEarthType addEarthType(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                                              @RequestParam String title){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                if(title.trim().length() > 0){
                    //ADD NEW EART TYPE
                    AgronomyEarthType agronomyEarthType = new AgronomyEarthType();
                    agronomyEarthType.setTitle(title);
                    agronomyEarthTypesRepository.save(agronomyEarthType);
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
    public AgronomyEarthType getEarthTypeItem(@PathVariable(value = "id") int id) {
        return agronomyEarthTypesRepository.findById(id);
    }

    //UPDATE EARTH TYPE ITEM
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/earth/types/{id}", method =  RequestMethod.POST)
    public AgronomyEarthType updateEarthTypeItem(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                              @PathVariable(value = "id") int id, @RequestParam(defaultValue = "") String title) {
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                //ADD NEW EART TYPE
                AgronomyEarthType agronomyEarthType = agronomyEarthTypesRepository.findById(id);
                if(agronomyEarthType != null){
                    if(title.trim().length() > 0){
                        agronomyEarthType.setTitle(title);
                        agronomyEarthTypesRepository.save(agronomyEarthType);
                    }else {
                        throw new NotFoundException(Constants.ERROR_WRONG_DATA);
                    }
                }
                return agronomyEarthType;
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
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
                                                    @RequestParam(defaultValue = "") String title, @RequestParam(defaultValue = "") String value){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                if(title.trim().length() > 0 && value.trim().length() > 0){
                    //ADD NEW EART TYPE
                    AgronomyEarthRegion earthRegion = new AgronomyEarthRegion();
                    earthRegion.setTitle(title);
                    earthRegion.setValue(value);
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
                                              @PathVariable(value = "id") int id, @RequestParam(defaultValue = "") String title,
                                              @RequestParam(defaultValue = "") String value) {
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
                    if(value.trim().length() > 0){
                        agronomyEarthRegion.setValue(value);
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

    //WEATHER

    //ADD WEATHER
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/conditions/weather", method =  RequestMethod.POST)
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

    //GET WEATHER ITEM
    @RequestMapping(value = "/conditions/weather/{id}", method =  RequestMethod.GET)
    public AgronomyWeather getWeatherItem(@PathVariable(value = "id") String id){
        return agronomyWeatherRepository.findById(id);
    }

    //UPDATE WEATHER ITEM
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
    }
}

