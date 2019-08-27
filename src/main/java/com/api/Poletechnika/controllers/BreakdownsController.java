package com.api.Poletechnika.controllers;

import com.api.Poletechnika.exceptions.AccessException;
import com.api.Poletechnika.exceptions.NotFoundException;
import com.api.Poletechnika.exceptions.WrongDataException;
import com.api.Poletechnika.models.*;
import com.api.Poletechnika.repository.*;
import com.api.Poletechnika.utils.Constants;
import com.api.Poletechnika.utils.DateUseUtil;
import com.api.Poletechnika.utils.FilterUtil;
import org.springframework.beans.factory.annotation.Autowired;
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


    //GET all breakdowns
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Iterable<Breakdown> getBreakdowns(@RequestParam(defaultValue = "") String code, @RequestParam(defaultValue = "") String carType,
                                             @RequestParam(defaultValue = "") String carModel, @RequestParam(defaultValue = "") String breakType) {
        List<String> enteredFilters = new ArrayList<>();
        if(carType.trim().length() > 0){
            enteredFilters.add(carType);
        }
        if(carModel.trim().length() > 0){
            enteredFilters.add(carModel);
        }
        if(breakType.trim().length() > 0){
            enteredFilters.add(breakType);
        }

        ArrayList<Breakdown> allBreakdowns = (ArrayList) breakdownRepository.findAll();
        //Check filters
        for(String filter: enteredFilters){
            Iterator<Breakdown> it = allBreakdowns.iterator();
            while (it.hasNext()) {
                Breakdown breakdown = it.next();
                if (!new FilterUtil().searchFilter(breakdown.getFilters(), filter)) {
                    it.remove();
                }
            }
        }
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

        return allBreakdowns;
    }

    //ADD BREAKDOWS
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "", method = RequestMethod.POST)
    public Breakdown addBreakdown(@RequestParam() int person_id, @RequestHeader("Authorization") String token,
                                  @RequestParam(defaultValue = "0") int code, @RequestParam(defaultValue = "0") int unit, @RequestParam(defaultValue = "") String title,
                                  @RequestParam(defaultValue = "") String image, @RequestParam(defaultValue = "0") int car_id,
                                  @RequestParam(defaultValue = "") String description, @RequestParam(defaultValue = "") String manual,
                                  @RequestParam(defaultValue = "") String filters) {

        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){

                Breakdown breakdown = new Breakdown();
                if(code != 0 && unit != 0){
                    breakdown.setCode(code);
                    breakdown.setUnit(unit);
                    breakdown.setTitle(title);
                    breakdown.setImage(image);
                    breakdown.setCar_id(car_id);
                    breakdown.setDescription(description);
                    breakdown.setManual(manual);
                    breakdown.setFilters(filters);
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
                                  @RequestParam(defaultValue = "") String image, @RequestParam(defaultValue = "0") int car_id,
                                  @RequestParam(defaultValue = "") String description, @RequestParam(defaultValue = "") String manual,
                                  @RequestParam(defaultValue = "") String filters) {

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
                    if (filters.trim().length() > 0){
                        breakdownUpdate.setFilters(filters);
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

    //GET BREAKDOWNS CONSULTATION REQUEST
    @RequestMapping(value = "/{id}/consultation", method = RequestMethod.GET)
    public Iterable<BreakdownConsultation> getBreakdownsConsultation(@PathVariable(value = "id") int id) {
        if(id == 0){
            return breakdownConsultationRepository.findAll();
        }else {
            return breakdownConsultationRepository.findAllByBreakdownId(id);
        }

    }

    //ADD BREAKDOWN CONSULTATION
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
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
                        breakdownConsultation.setDate(new DateUseUtil().getCurrentDate());
                        breakdownConsultation.setMessage(message);
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
        return breakdownConsultationRepository.findById(consultationId);
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
