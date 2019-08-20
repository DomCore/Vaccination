package com.api.Poletechnika.controllers;

import com.api.Poletechnika.exceptions.AccessException;
import com.api.Poletechnika.exceptions.NotFoundException;
import com.api.Poletechnika.exceptions.WrongDataException;
import com.api.Poletechnika.models.*;
import com.api.Poletechnika.repository.*;
import com.api.Poletechnika.utils.Constants;
import com.api.Poletechnika.utils.DateUseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(value = "/profile")
public class UserDataController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserGlobalRepository userGlobalRepository;

    @Autowired
    UserPermissionRepository userPermissionRepository;


    @Autowired
    LicenseRequestsRepository licenseRequestsRepository;
    @Autowired
    LicenseRepository licenseRepository;

    @Autowired
    UserCalculationRepository userCalculationRepository;
    @Autowired
    UserCalculationDataRepository userCalculationDataRepository;

    //GET ALL USERS
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/clients", method = RequestMethod.GET)
    public Iterable<User> getUsers(@RequestHeader(value="Authorization", defaultValue="") String token,
                                   @RequestParam(defaultValue = "") String name,
                                   @RequestParam(defaultValue = "0") int user_id) {

        if(token.trim().length() > 0){
            User user = userRepository.findUserByToken(token);
            if(user != null){
                UserPermission userPermission = userPermissionRepository.findByUserId(user.getId());
                if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){

                    if(user_id != 0){
                        List<User> idFiltredUser = new ArrayList<>();
                        User user1 = userRepository.findById(user_id);
                        if(user1 != null){
                            idFiltredUser.add(user1);
                        }
                        return idFiltredUser;
                    }else {
                        if(name.trim().length() > 0){
                            Iterable<User> usersAll = userRepository.findAll();
                            List<User> findedUsers = new ArrayList<>();
                            for(User userItem : usersAll){
                                if(searchName(userItem.getName(), name)){   //userItem.getName().equals(name)
                                    findedUsers.add(userItem);
                                }
                            }
                            return findedUsers;
                        }else {
                            return userRepository.findAll();
                        }
                    }
                }else {
                    throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
        }

    }

    //UPDATE USER IN ADMIN PANEL
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/clients", method =  RequestMethod.POST)
    public User updateLicenseRequest(@RequestHeader(value = "Authorization", defaultValue="") String token, @RequestParam(defaultValue = "0") int person_id,
                                               @RequestParam(defaultValue = "0") int user_id, @RequestParam(defaultValue = "") String name,
                                               @RequestParam(defaultValue = "") String license, @RequestParam(defaultValue = "") String license_term,
                                               @RequestParam(defaultValue = "") String phone, @RequestParam(defaultValue = "") String mail,
                                               @RequestParam(defaultValue = "") String website, @RequestParam(defaultValue = "") String company,
                                               @RequestParam(defaultValue = "") String registry, @RequestParam(defaultValue = "") String pass,
                                               @RequestParam(defaultValue = "") String registration_status, @RequestParam(defaultValue = "") String registration_date
                                     ){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                //UPDATE
                if(user_id != 0){
                    User user = userRepository.findById(user_id);
                    if(user != null){
                        //CHANGE USER DATA
                        if(name.trim().length() > 0){
                            user.setName(name);
                        }
                        if(license.trim().length() > 0){
                            user.setLicense(license);
                        }
                        if(license_term.trim().length() > 0){
                            user.setLicense_term(license_term);
                        }
                        if(phone.trim().length() > 0){
                            user.setPhone(phone);
                        }
                        if(mail.trim().length() > 0){
                            user.setMail(mail);
                        }
                        if(website.trim().length() > 0){
                            user.setWebsite(website);
                        }
                        if(company.trim().length() > 0){
                            user.setCompany(company);
                        }
                        if(registry.trim().length() > 0){
                            user.setRegistry(registry);
                        }
                        if(pass.trim().length() > 0){
                            user.setPass(pass);
                        }
                        if(registration_status.trim().length() > 0){
                            user.setRegistration_status(registration_status);
                        }
                        if(registration_date.trim().length() > 0){
                            user.setRegistration_date(registration_date);
                        }
                        userRepository.save(user);
                        return userRepository.findById(user_id);
                    }else {
                        throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                    }
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


    //DELETE USER
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/clients", method =  RequestMethod.DELETE)
    public void deleteEarthRegionItem(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                      @RequestParam int user_id){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                if(userRepository.findById(user_id) != null){
                    //delete all user calculations
                    List<UserCalculation> userCalculations = userCalculationRepository.findAllByUserId(user_id);
                    if(userCalculations != null && userCalculations.size() > 0){
                        for (UserCalculation userCalculation : userCalculations){
                            userCalculationRepository.deleteUserCalculationByUserIdAndId(user_id, userCalculation.getId());
                            userCalculationDataRepository.deleteAllByIdCalculation(userCalculation.getId());
                        }
                    }
                    //delete user license request
                    licenseRequestsRepository.deleteAllByUserId(user_id);
                    //delete user
                    userRepository.deleteById(user_id);

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


    //GET USER DATA
    @RequestMapping(value = "/clients/{id}", method = RequestMethod.GET)
    public UserGlobalData getUserData(@RequestHeader("Authorization") String token, @PathVariable(value = "id") int user_id) {
        User user = userRepository.findById(user_id);
        if(user != null){
            if(user.getToken() != null &&token.trim().length() > 0 &&  user.getToken().equals(token)){
                return userGlobalRepository.findById(user_id);
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
        }
    }


    //UPDATE USER DATA
    @RequestMapping(value = "/clients/{id}", method = RequestMethod.POST)
    public UserGlobalData changeUserData(@PathVariable(value = "id") int user_id, @RequestHeader("Authorization") String token,
                                         @RequestParam(defaultValue = "") String mail, @RequestParam(defaultValue = "") String phone,
                                         @RequestParam(defaultValue = "0") int code) {
        User user = userRepository.findById(user_id);
        if(user != null){
            if(user.getToken() != null && token.trim().length() > 0 && user.getToken().equals(token)){
                if(mail.trim().length() > 0){
                    user.setMail(mail);
                }
                if(phone.trim().length() > 0){
                    if(userRepository.findUserByPhone(phone) == null){
                        if(code != 0){
                            //ПРОВЕРЯЕМ КОД ЕСЛИ ОК
                            if(user.getChange_code().equals(String.valueOf(code))){
                                user.setPhone(phone);
                            }else {
                                throw new WrongDataException(Constants.ERROR_WRONG_CODE);

                            }
                        }else {
                            String chCode = generateChangeCode();
                            user.setChange_code(chCode);
//ОТПРАВКА ПРОВЕРОЧОГО КОДА НА НОМЕР МОБИЛЬНОГО
                        }
                    }else {
                        throw new WrongDataException(Constants.ERROR_REGISTRATION_NUMBER_USE);
                    }
                }
                userRepository.save(user);
                return userGlobalRepository.findById(user_id);
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
        }
    }

    //CALCULATIONS
    //GET USER CALCULATIONS
    @RequestMapping(value = "/clients/{id}/calculations", method = RequestMethod.GET)
    public Iterable<UserCalculation> getUserCalculations(@RequestHeader("Authorization") String token, @PathVariable(value = "id") int user_id) {
        User user = userRepository.findById(user_id);
        if(user != null){
            if(user.getToken() != null && token.trim().length() > 0 &&  user.getToken().equals(token)){
            return userCalculationRepository.findAllByUserId(user_id);
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
        }
    }

    //ADD USER CALCULATIONS
    @RequestMapping(value = "/clients/{id}/calculations", method = RequestMethod.POST)
    public UserCalculationAllData addUserCalculations(@RequestHeader("Authorization") String token, @PathVariable(value = "id") int user_id,
                                    @RequestBody UserCalculationAllData calculationData) {
        User user = userRepository.findById(user_id);
        if(user != null){
            if(user.getToken() != null && token.trim().length() > 0 &&  user.getToken().equals(token)){

                String description = "";
                if(calculationData.getInput_data() != null && calculationData.getInput_data().size() > 0){
                    int maxI = calculationData.getInput_data().size() >= 3 ? 3 : calculationData.getInput_data().size();
                    for(int i = 0; i < maxI; i++){
                        description += calculationData.getInput_data().get(i).getValue() + " ";
                    }
                }


                UserCalculation calculation = new UserCalculation();
                calculation.setTitle(calculationData.getTitle());
                calculation.setUserId(user_id);
                calculation.setNumber(userCalculationRepository.findAllByUserId(user_id) != null ?
                        userCalculationRepository.findAllByUserId(user_id).size()+1 : 1);
                calculation.setDescription(description.trim());

                calculation.setDate(new DateUseUtil().getCurrentDate());

                if(calculationData.getArea() != null && calculationData.getArea().size() > 0){
                    String arrayToJson = null;
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        arrayToJson = mapper.writeValueAsString(calculationData.getArea());
                        calculation.setArea(arrayToJson);
                    } catch (Exception ex) {
                        calculation.setArea("");
                    }
                }else {
                    calculation.setArea("");
                }
                userCalculationRepository.save(calculation);

                //set input-output data
                UserCalculationData userCalculationData;
                if(calculationData.getInput_data() != null && calculationData.getInput_data().size() > 0){
                    for (int i = 0; i < calculationData.getInput_data().size(); i++){
                        userCalculationData = new UserCalculationData();
                        userCalculationData.setIdCalculation(calculation.getId());
                        userCalculationData.setType("input");
                        userCalculationData.setTitle(calculationData.getInput_data().get(i).getTitle());
                        userCalculationData.setValue(calculationData.getInput_data().get(i).getValue());
                        userCalculationDataRepository.save(userCalculationData);
                    }
                }
                if(calculationData.getOutput_data() != null && calculationData.getOutput_data().size() > 0){
                    for (int i = 0; i < calculationData.getOutput_data().size(); i++){
                        userCalculationData = new UserCalculationData();
                        userCalculationData.setIdCalculation(calculation.getId());
                        userCalculationData.setType("output");
                        userCalculationData.setTitle(calculationData.getOutput_data().get(i).getTitle());
                        userCalculationData.setValue(calculationData.getOutput_data().get(i).getValue());
                        userCalculationDataRepository.save(userCalculationData);
                    }
                }

                return getUserCalculationData(token, user_id, calculation.getId());
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
        }
    }

    //GET CALCULATION DATA
    @RequestMapping(value = "/clients/{id}/calculations/{calculationId}", method = RequestMethod.GET)
    public UserCalculationAllData getUserCalculationData(@RequestHeader("Authorization") String token, @PathVariable(value = "id") int user_id,
                                                            @PathVariable(value = "calculationId") int calculation_id) {
        User user = userRepository.findById(user_id);
        if(user != null){
            if(user.getToken() != null && token.trim().length() > 0 &&  user.getToken().equals(token)){

                UserCalculation calculation = userCalculationRepository.findByUserIdAndId(user_id, calculation_id);

                List<UserCalculationData> inputData = userCalculationDataRepository.findAllByIdCalculationAndType(calculation_id, "input");

                List<UserCalculationData> outputData = userCalculationDataRepository.findAllByIdCalculationAndType(calculation_id, "output");

                if(calculation != null){

                    UserCalculationAllData userCalculationAllData = new UserCalculationAllData();

                    userCalculationAllData.setId(calculation.getId());
                    userCalculationAllData.setDate(calculation.getDate());
                    userCalculationAllData.setDescription(calculation.getDescription());
                    userCalculationAllData.setTitle(calculation.getTitle());
                    userCalculationAllData.setNumber(calculation.getNumber());
                    userCalculationAllData.setUserId(calculation.getUserId());

                    userCalculationAllData.setInput_data(inputData);
                    userCalculationAllData.setOutput_data(outputData);


                    //GET AREA
                    JSONArray jsonArray = null;
                    ObjectMapper mapper = new ObjectMapper();
                    List<CalculationAreaItem> areaItems = new ArrayList<>();
                    CalculationAreaItem areaItem = null;
                    try {
                        jsonArray = new JSONArray(calculation.getArea());
                        try {
                            areaItem = mapper.readValue(jsonArray.getString(0), CalculationAreaItem.class); //test for check if all ok

                            //ADD CHARACT. TO DB
                            for (int i = 0; i < jsonArray.length(); i++) {
                                areaItem = mapper.readValue(jsonArray.getString(i), CalculationAreaItem.class);
                                if(areaItem != null){
                                    areaItems.add(areaItem);
                                }
                            }
                        } catch (IOException e) {
                            throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                        }
                    } catch (JSONException e) {
                        throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                    }

                    userCalculationAllData.setArea(areaItems);
                    return userCalculationAllData;
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

    //DELETE USER CALCULATION
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/clients/{id}/calculations/{calculationId}", method =  RequestMethod.DELETE)
    public void deleteUserCalculation(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                      @PathVariable(value = "id") int user_id, @PathVariable(value = "calculationId") int calculation_id){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                if(userCalculationRepository.findByUserIdAndId(user_id, calculation_id) != null){
                    userCalculationRepository.deleteUserCalculationByUserIdAndId(user_id, calculation_id);
                    userCalculationDataRepository.deleteAllByIdCalculation(calculation_id);

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

    //LICENSE
    //GET USER LICENSE DATA
    @RequestMapping(value = "/clients/{id}/license", method = RequestMethod.GET)
    public UserLicenseData getUserLicenseData(@RequestHeader("Authorization") String token, @PathVariable(value = "id") int user_id) { //UserLicenseData
        User user = userRepository.findById(user_id);
        if(user != null){
            if(user.getToken() != null && token.trim().length() > 0 && user.getToken().equals(token)){

                License license = licenseRepository.findOne(1);
                if(license != null){

                    UserLicenseData userLicenseData = new UserLicenseData();

                    userLicenseData.setType(user.getLicense());
                    userLicenseData.setDate(user.getLicense_term());
                    userLicenseData.setMessage(user.getLicense_message());
                    userLicenseData.setDescription(license.getDescription());
                    userLicenseData.setPrice(license.getPrice());
                    switch (user.getLicense()){
                        case "free":
                            userLicenseData.setTypeTitle("Ознакомительная");
                            break;
                        case "paid":
                            userLicenseData.setTypeTitle("Платная");
                            break;
                        case "staff":
                            userLicenseData.setTypeTitle("Для сотрудников");
                            userLicenseData.setDescription("");
                            userLicenseData.setPrice(0);
                            break;
                    }

                    //ВЫВОД ЗАПРОСОВ НА ЛИЦЕНЗИЮ (МОЖЕТ БЫТЬ НЕСКОЛЬКО ЗАПРОСОВ, ВОЗВРАЩАЕМ ПОСЛЕДНИЙ)
                    List<LicenseRequest> licenseRequestList = licenseRequestsRepository.findAllByUserId(user_id);
                    if(licenseRequestList != null && licenseRequestList.size() > 0){
                        int requestId = 0;
                        for(LicenseRequest request : licenseRequestList){
                            if(request.getId() > requestId){
                                requestId = request.getId();
                            }
                        }

                        LicenseRequest licenseRequest = licenseRequestsRepository.findById(requestId);

                        userLicenseData.setId(licenseRequest.getId());
                        switch (licenseRequest.getPayment()){
                            case "card":
                                userLicenseData.setPay("Карта");
                                break;
                            case "cashless":
                                userLicenseData.setPay("Безналичный расчет");
                                break;
                        }
                    }else {
                        //У ПОЛЬЗОВАТЕЛЯ НЕТУ ЛИЦЕНЗИИ
                        userLicenseData.setId(0);
                        userLicenseData.setPay("");
                    }
                    return userLicenseData;
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



    //GET LICENSE CAN USER BUY CASHLESS
    @RequestMapping(value = "/clients/{id}/license/{type-id}", method = RequestMethod.GET)
    public UserLicenseData getCanUserLicenseCashless(@PathVariable(value = "type-id") String payTypeId, @RequestHeader("Authorization") String token, @PathVariable(value = "id") int user_id) { //UserLicenseData
        User user = userRepository.findById(user_id);
        if(user != null){
            if(user.getToken() != null && token.trim().length() > 0 && user.getToken().equals(token)){

                License license = licenseRepository.findOne(1);
                if(license != null){

                    UserLicenseData userLicenseData = new UserLicenseData();

                    userLicenseData.setId(license.getId());
                    userLicenseData.setType("paid");
                    userLicenseData.setTypeTitle("Платная");
                    userLicenseData.setDescription(license.getDescription());
                    userLicenseData.setPrice(license.getPrice());
                    switch (payTypeId){
                        case "cashless":
                            userLicenseData.setPay("Безналичный расчет");
                            break;
                        case "card":
                            userLicenseData.setPay("Карта");
                            break;
                    }

                    if(user.getLicense().equals("free")){
                        userLicenseData.setDate(new DateUseUtil().changeCurrentDate(null, license.getTerm()));
                    }else if(user.getLicense().equals("paid")){
                        userLicenseData.setDate(new DateUseUtil().changeCurrentDate(user.getLicense_term(), license.getTerm()));
                    }

                    return userLicenseData;
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



    //BUY(ADD) LICENSE FROM CASHLESS
    @RequestMapping(value = "/clients/{id}/license/cashless", method = RequestMethod.POST)
    public void buyLicenseCashless(@RequestHeader("Authorization") String token, @PathVariable(value = "id") int user_id,
                                   @RequestParam String email) { //UserLicenseData
        User user = userRepository.findById(user_id);
        if(user != null){
            if(user.getToken() != null && token.trim().length() > 0 && user.getToken().equals(token)){
                LicenseRequest licenseRequest = new LicenseRequest();
                licenseRequest.setMail(email);
                licenseRequest.setUserId(user_id);
                licenseRequest.setStatus(0);
                licenseRequest.setDate(new DateUseUtil().getCurrentDate());
                licenseRequest.setPayment("cashless");

                License license = licenseRepository.findOne(1);
                if(license == null){
                    throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
                }

                if(user.getLicense().equals("free")){   //Покупка
                    licenseRequest.setLicenseTerm(new DateUseUtil().changeCurrentDate(null, license.getTerm()));
                }else if(user.getLicense().equals("paid")){ //Продление
                    licenseRequest.setLicenseTerm(new DateUseUtil().changeCurrentDate(user.getLicense_term(), license.getTerm()));
                }else {  //default
                    licenseRequest.setLicenseTerm(new DateUseUtil().changeCurrentDate(null, license.getTerm()));
                }
                licenseRequestsRepository.save(licenseRequest);

            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
        }
    }

    //BUY(ADD) LICENSE FROM CARD
    @RequestMapping(value = "/clients/{id}/license/card", method = RequestMethod.POST)
    public void buyLicenseCard(@PathVariable(value = "id") int user_id,
                                   @RequestParam String data, @RequestParam String signature) { //UserLicenseData
//проработать процес взаэмодії з лікпей
    }




    //UTILS

    private String generateChangeCode(){
        String candidateChars = "1234567890";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            sb.append(candidateChars.charAt(random.nextInt(candidateChars.length())));
        }

        return sb.toString();
    }


    //ПОИСК ИМЕНИ ПО СЛОВУ
    public boolean searchName(String search, String what) {
        if(!search.replaceAll(what,"_").equals(search)) {
            return true;
        }
        return false;
    }
}
