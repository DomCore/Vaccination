package com.api.Poletechnika.controllers;

import com.api.Poletechnika.exceptions.AccessException;
import com.api.Poletechnika.exceptions.NotFoundException;
import com.api.Poletechnika.exceptions.WrongDataException;
import com.api.Poletechnika.models.*;
import com.api.Poletechnika.repository.*;
import com.api.Poletechnika.utils.Constants;
import com.api.Poletechnika.utils.DateUseUtil;
import com.api.Poletechnika.utils.FilterUtil;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping(value = "/breakdowns")
public class BreakdownsController {

    @Autowired
    BreakdownRepository breakdownRepository;

    @Autowired
    BreakdownConsultationRepository breakdownConsultationRepository;

    @Autowired
    UserPermissionRepository userPermissionRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    BreakdownFiltersRepository breakdownFiltersRepository;

    //GET all breakdowns
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Iterable<Breakdown> getBreakdowns(@RequestParam(defaultValue = "") String code, @RequestParam(defaultValue = "") String hashtags, @RequestParam(defaultValue = "") String carType,
                                             @RequestParam(defaultValue = "") String carModel, @RequestParam(defaultValue = "") String breakType) {
        //OLD FILTERS CODE
        /*List<String> enteredFilters = new ArrayList<>();
        if(carType.trim().length() > 0){
            enteredFilters.add(carType);
        }
        if(carModel.trim().length() > 0){
            enteredFilters.add(carModel);
        }
        if(breakType.trim().length() > 0){
            enteredFilters.add(breakType);
        }*/

        ArrayList<Breakdown> allBreakdowns = (ArrayList) breakdownRepository.findAll();

        if(breakType.trim().length() > 0){
            Iterator<Breakdown> it = allBreakdowns.iterator();
            while (it.hasNext()) {
                Breakdown breakdown = it.next();
                if (!breakdown.getBreakdownType().equals(breakType.trim())) {
                    it.remove();
                }
            }
        }

        if(carType.trim().length() > 0){
            Iterator<Breakdown> it = allBreakdowns.iterator();
            while (it.hasNext()) {
                Breakdown breakdown = it.next();
                if (!breakdown.getCarType().equals(carType.trim())) {
                    it.remove();
                }
            }
        }

        if(carModel.trim().length() > 0){
            Iterator<Breakdown> it = allBreakdowns.iterator();
            while (it.hasNext()) {
                Breakdown breakdown = it.next();
                if (!breakdown.getCarModel().equals(carModel.trim())) {
                    it.remove();
                }
            }
        }


        //OLD FILTERS CODE
        //Check filters
       /* for(String filter: enteredFilters){
            Iterator<Breakdown> it = allBreakdowns.iterator();
            while (it.hasNext()) {
                Breakdown breakdown = it.next();
                if (!new FilterUtil().searchFilter(breakdown.getFilters(), filter)) {
                    it.remove();
                }
            }
        }*/

        //Check like code
        if(code.trim().length() > 0){
            Iterator<Breakdown> it = allBreakdowns.iterator();
            while (it.hasNext()) {
                Breakdown breakdown = it.next();
                if (!new FilterUtil().searchName(String.valueOf(breakdown.getCode()), code)) {
                    it.remove();
                }
            }
        }

        //Check  hashtags
        if(hashtags.trim().length() > 0){
            Iterator<Breakdown> it = allBreakdowns.iterator();
            while (it.hasNext()) {
                Breakdown breakdown = it.next();
                if (!new FilterUtil().searchHashtag(hashtags, breakdown.getHashtags())) {
                    it.remove();
                }
            }
        }

        return allBreakdowns;
    }

    //ADD BREAKDOWS
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "", method = RequestMethod.POST)
    public Breakdown addBreakdown(@RequestParam() int person_id, @RequestHeader("Authorization") String token,
                                  @RequestParam(defaultValue = "0") int code, @RequestParam(defaultValue = "0") int unit, @RequestParam(defaultValue = "") String title,
                                  @RequestParam(defaultValue = "") String hashtags,
                                  @RequestParam(defaultValue = "") String image, @RequestParam(defaultValue = "0") int car_id,
                                  @RequestParam(defaultValue = "") String description, @RequestParam(defaultValue = "") String manual,
                                  @RequestParam String breakdown_type_title,
                                  @RequestParam String car_type_id, @RequestParam String car_model_id) {

        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){

                Breakdown breakdown = new Breakdown();
                if(code != 0 && unit != 0){
                    breakdown.setCode(code);
                    breakdown.setUnit(unit);
                    breakdown.setTitle(title);
                    breakdown.setHashtags(hashtags);
                    breakdown.setImage(image);
                    breakdown.setCar_id(car_id);
                    breakdown.setDescription(description);
                    breakdown.setManual(manual);

                    //set breakdown type filter
                    BreakdownFilter filterInDB = breakdownFiltersRepository.findByTitleAndType(breakdown_type_title.trim(), Constants.FILTER_TYPE_BREAKDONW_TYPE);
                    if(filterInDB != null){
                        breakdown.setBreakdownType(String.valueOf(filterInDB.getId()));
                    }else {
                        BreakdownFilter newFilter = new BreakdownFilter();
                        newFilter.setTitle(breakdown_type_title);
                        newFilter.setType(Constants.FILTER_TYPE_BREAKDONW_TYPE);
                        breakdownFiltersRepository.save(newFilter);
                        breakdown.setBreakdownType(String.valueOf(newFilter.getId()));
                    }

//Возможно здесь нужно будет выносить значения в фильтры для вывода нужного количества моделей а не всех
                    //set car type filter
                    breakdown.setCarType(car_type_id);
                    //set car model filter
                    breakdown.setCarModel(car_model_id);

                    breakdownRepository.save(breakdown);
                    return getBreakdownData(breakdown.getId());
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

    //GET BREAKDOWN
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Breakdown getBreakdownData(@PathVariable(value = "id") int id) {

        Breakdown breakdown = breakdownRepository.findById(id);
        if(breakdown != null){
            List<Video> videos = videoRepository.findAllByBreakdownId(id);
            if(videos != null){
                breakdown.setVideos(videos);
            }
            return breakdown;
        }else {
            throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
        }
    }


    //UPDATE BREAKDOWN
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public Breakdown addBreakdown(@RequestParam() int person_id, @RequestHeader("Authorization") String token, @PathVariable(value = "id") int id,
                                  @RequestParam(defaultValue = "0") int code, @RequestParam(defaultValue = "0") int unit, @RequestParam(defaultValue = "") String title,
                                  @RequestParam(defaultValue = "") String image, @RequestParam(defaultValue = "0") int car_id, @RequestParam(defaultValue = "999") String hashtags,
                                  @RequestParam(defaultValue = "") String description, @RequestParam(defaultValue = "") String manual,
                                  @RequestParam(defaultValue = "") String breakdown_type_title,
                                  @RequestParam(defaultValue = "") String car_type_id, @RequestParam(defaultValue = "") String car_model_id) {

        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){

                Breakdown breakdownUpdate = breakdownRepository.findById(id);
                if(breakdownUpdate != null){
                    if(code != 0){
                        breakdownUpdate.setCode(code);
                    }
                    if(unit != 0){
                        breakdownUpdate.setUnit(unit);
                    }
                    if(title.trim().length() > 0){
                        breakdownUpdate.setTitle(title);
                    }
                    if(!hashtags.equals("999")){
                        breakdownUpdate.setHashtags(hashtags);
                    }
                    if(image.trim().length() > 0){
                        breakdownUpdate.setImage(image);
                    }
                    if(car_id != 0){
                        breakdownUpdate.setCar_id(car_id);
                    }
                    if(description.trim().length() > 0){
                        breakdownUpdate.setDescription(description);
                    }
                    if(manual.trim().length() > 0){
                        breakdownUpdate.setManual(manual);
                    }

                    if(breakdown_type_title.trim().length() > 0){
                        //set breakdown type filter
                        BreakdownFilter filterInDB = breakdownFiltersRepository.findByTitleAndType(breakdown_type_title.trim(), Constants.FILTER_TYPE_BREAKDONW_TYPE);
                        if(filterInDB != null){
                            breakdownUpdate.setBreakdownType(String.valueOf(filterInDB.getId()));
                        }else {
                            BreakdownFilter newFilter = new BreakdownFilter();
                            newFilter.setTitle(breakdown_type_title);
                            newFilter.setType(Constants.FILTER_TYPE_BREAKDONW_TYPE);
                            breakdownFiltersRepository.save(newFilter);
                            breakdownUpdate.setBreakdownType(String.valueOf(newFilter.getId()));
                        }
                    }

                    if(car_type_id.trim().length() > 0){
                        breakdownUpdate.setCarType(car_type_id.trim());
                    }

                    if(car_model_id.trim().length() > 0){
                        breakdownUpdate.setCarModel(car_model_id.trim());
                    }

                    breakdownRepository.save(breakdownUpdate);
                    return breakdownRepository.findById(id);
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

    //DELETE BREAKDOWN
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/{id}", method =  RequestMethod.DELETE)
    public void deleteBreakdown(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                      @PathVariable(value = "id") int id){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                if(breakdownRepository.findById(id) != null){
                    breakdownRepository.deleteById(id);
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


    //FILTERS

    //Get all filters
    @RequestMapping(value = "/filters", method = RequestMethod.GET)
    public MappingJacksonValue getFilters(@RequestParam(defaultValue = "") String title, @RequestParam(defaultValue = "") String type) {
        SimpleBeanPropertyFilter filterJson = SimpleBeanPropertyFilter.filterOutAllExcept("id", "title", "type");
        FilterProvider filtersForJson = new SimpleFilterProvider().addFilter("SomeBeanFilter", filterJson);
        MappingJacksonValue mapping;

        Iterable<BreakdownFilter> filtersAll;
        if(type.trim().length() > 0){
            filtersAll = breakdownFiltersRepository.findAllByType(type);
        }else {
            filtersAll = breakdownFiltersRepository.findAll();
        }

        if(title.trim().length() > 0){
            List<BreakdownFilter> findedFilters = new ArrayList<>();
            for(BreakdownFilter filterItem : filtersAll){
                if(new FilterUtil().searchName(filterItem.getTitle(), title)){
                    findedFilters.add(filterItem);
                }
            }
            mapping = new MappingJacksonValue(findedFilters);
        }else {
            mapping = new MappingJacksonValue(filtersAll);
        }

        mapping.setFilters(filtersForJson);
        return mapping;
    }

    //ADD NEW FILTER
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/filters", method = RequestMethod.POST)
    public MappingJacksonValue addFilter(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                         @RequestParam String title, @RequestParam String type,
                                         @RequestParam(defaultValue = "0") String value_id, @RequestParam(defaultValue = "0") String value_parent) {
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                BreakdownFilter filter = new BreakdownFilter();
                if(title.trim().length() > 0 && type.trim().length() > 0){
                    filter.setTitle(title);
                    filter.setType(type);
                    if(!value_parent.equals("0")){
                        filter.setValue_parent(value_parent);
                    }
                    if(!value_id.equals("0")){
                        filter.setValueId(value_id);
                    }
                    breakdownFiltersRepository.save(filter);
                    return getOneFilter(filter.getId());
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
    //GET ONE FILTER
    @RequestMapping(value = "/filters/{id}", method = RequestMethod.GET)
    public MappingJacksonValue getOneFilter(@PathVariable(value = "id") int id) {
        if(breakdownFiltersRepository.findById(id) != null){
            // Вывод в Json только id and title
            SimpleBeanPropertyFilter filterJson = SimpleBeanPropertyFilter.filterOutAllExcept("id", "title", "type");
            FilterProvider filtersForJson = new SimpleFilterProvider().addFilter("SomeBeanFilter", filterJson);
            MappingJacksonValue mapping = new MappingJacksonValue(breakdownFiltersRepository.findById(id));
            mapping.setFilters(filtersForJson);
            return mapping;
        }else {
            throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
        }
        //return videoFiltersRepository.findById(id);
    }

    //UPDATE FILTER
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/filters/{id}", method = RequestMethod.POST)
    public MappingJacksonValue updateFilter(@RequestHeader("Authorization") String token, @RequestParam int person_id, @PathVariable(value = "id") int id,
                                            @RequestParam(defaultValue = "") String title, @RequestParam(defaultValue = "") String type,
                                            @RequestParam(defaultValue = "999999") int parent) {
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                BreakdownFilter breakdownFilter = breakdownFiltersRepository.findById(id);
                if(breakdownFilter != null){
                    if(title.trim().length() > 0){
                        breakdownFilter.setTitle(title);
                    }
                    if(type.trim().length() > 0){
                        breakdownFilter.setType(type);
                    }
                    if(parent != 999999  && parent != breakdownFilter.getId()){  //ВТОРОЕ УСЛОВИЕ - ЧТО БЫ НЕ СТАВИТЬ РОДИТЕЛЕМ САМОГО СЕБЯ
//                         videoFilter.setParent(parent);
                    }
                    breakdownFiltersRepository.save(breakdownFilter);
                    return getOneFilter(id);
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

    //DELETE FILTER
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/filters/{id}", method =  RequestMethod.DELETE)
    public void deleteFilter(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                             @PathVariable(value = "id") int id){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                if(breakdownFiltersRepository.findById(id) != null){
                    breakdownFiltersRepository.deleteById(id);
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


    //CONSULTATION

    //GET BREAKDOWNS CONSULTATION REQUEST
    @RequestMapping(value = "/{id}/consultation", method = RequestMethod.GET)
    public Iterable<BreakdownConsultation> getBreakdownsConsultation(@PathVariable(value = "id") int id,
                                                                     @RequestParam(defaultValue = "999") int is_new) {
        if(id == 0){
            if(is_new == 1 || is_new == 0){
                return breakdownConsultationRepository.findAllByIsNew(is_new);
            }else {
                return breakdownConsultationRepository.findAll();
            }
        }else {
            if(is_new == 1 || is_new == 0){
                return breakdownConsultationRepository.findAllByBreakdownIdAndIsNew(id, is_new);
            }else {
                return breakdownConsultationRepository.findAllByBreakdownId(id);
            }
        }

    }

    //ADD BREAKDOWN CONSULTATION
    @RequestMapping(value = "/{id}/consultation", method = RequestMethod.POST)
    public BreakdownConsultation addBreakdownConsultation(@RequestParam() int user_id, @RequestHeader("Authorization") String token,
                                              @PathVariable(value = "id") int id,
                                              @RequestParam(defaultValue = "") String message) {

        if(userRepository.findById(user_id) != null){
            if(userRepository.findById(user_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(user_id).getToken().equals(token)){

                if(breakdownRepository.findById(id) != null){
                    if(message.trim().length() > 0){
                        BreakdownConsultation breakdownConsultation = new BreakdownConsultation();
                        breakdownConsultation.setBreakdownId(id);
                        breakdownConsultation.setUserId(user_id);
                        breakdownConsultation.setDate(new DateUseUtil().getCurrentDateTime());
                        breakdownConsultation.setMessage(message);
                        breakdownConsultation.setIsNew(1);
                        breakdownConsultationRepository.save(breakdownConsultation);
                        return breakdownConsultationRepository.findById(breakdownConsultation.getId());
                    }else {
                        throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                    }
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

    //GET BREAKDOWN CONSULTATION
    @RequestMapping(value = "/{id}/consultation/{consultation-id}", method = RequestMethod.GET)
    public BreakdownConsultation getBreakdownConsultationData(@PathVariable(value = "consultation-id") int consultationId) {
        BreakdownConsultation breakdownConsultation = breakdownConsultationRepository.findById(consultationId);
        if(breakdownConsultation != null){
            if(breakdownConsultation.getIsNew() == 1){
                breakdownConsultation.setIsNew(0);
                breakdownConsultationRepository.save(breakdownConsultation);
            }
            return breakdownConsultation;
        }else {
            throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
        }
    }

    //SER ANSWER CONSULTATION FOR USER
    @RequestMapping(value = "/{id}/consultation/{consultation-id}", method = RequestMethod.POST)
    public BreakdownConsultation sendAnswerConsultation(@PathVariable(value = "consultation-id") int consultationId,
                                                        @RequestParam String message) {

        BreakdownConsultation breakdownConsultation = breakdownConsultationRepository.findById(consultationId);
        if(breakdownConsultation != null){
            User user = userRepository.findById(breakdownConsultation.getUserId());
            if(user != null){
                if(message.trim().length() > 0){
                    String userMail = user.getMail();
                    String ask = breakdownConsultation.getMessage();
//НЕОБХОДИМО отправить ответ на почту пользователя с содержимым: вопрос - ответ (вожможно дату) сформировать текст ответа
                }else {
                    throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                }
            }else {
                throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
        }


        return breakdownConsultationRepository.findById(consultationId);
    }

    //DELETE CONSULTATION
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/{id}/consultation/{consultation-id}", method =  RequestMethod.DELETE)
    public void deleteConsulatation(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                      @PathVariable(value = "consultation-id") int consultationId){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                if(breakdownConsultationRepository.findById(consultationId) != null){
                    breakdownConsultationRepository.deleteById(consultationId);
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
