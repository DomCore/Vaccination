package com.api.Poletechnika.controllers;


import com.api.Poletechnika.exceptions.AccessException;
import com.api.Poletechnika.exceptions.NotFoundException;
import com.api.Poletechnika.exceptions.WrongDataException;
import com.api.Poletechnika.models.FirebaseNotificationClient;
import com.api.Poletechnika.models.LicenseRequest;
import com.api.Poletechnika.models.User;
import com.api.Poletechnika.models.UserPermission;
import com.api.Poletechnika.repository.FirebaseNotificationClientRepository;
import com.api.Poletechnika.repository.LicenseRequestsRepository;
import com.api.Poletechnika.repository.UserPermissionRepository;
import com.api.Poletechnika.repository.UserRepository;
import com.api.Poletechnika.services.PushNotificationsService;
import com.api.Poletechnika.utils.Constants;
/*import com.api.Poletechnika.utils.ConvertUtil;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/licenseRequests")
public class LicenseRequestController {

    @Autowired
    LicenseRequestsRepository licenseRequestsRepository;

    @Autowired
    UserPermissionRepository userPermissionRepository;
    @Autowired
    UserRepository userRepository;


    @Autowired
    PushNotificationsService pushNotificationsService;
    @Autowired
    FirebaseNotificationClientRepository firebaseNotificationClientRepository;


    //GET LICENSE REQUESTS
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Iterable<LicenseRequest> getLicenseRequests(@RequestHeader(value="Authorization", defaultValue="") String token,
                                                       @RequestParam(defaultValue = "99959") int status) {
        //CHECK ADMINISTRATION PERMISSION
        if(token.trim().length() > 0){
            User user = userRepository.findUserByToken(token);
            if(user != null){
                UserPermission userPermission = userPermissionRepository.findByUserId(user.getId());
                if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
                    if(status != 99959){
                        Iterable<LicenseRequest> requestAll = licenseRequestsRepository.findAll();
                        List<LicenseRequest> findedRequests = new ArrayList<>();
                        for(LicenseRequest requestItem : requestAll){
                            if(requestItem.getStatus() == status){
                                findedRequests.add(requestItem);
                            }
                        }
                        return findedRequests;
                    }else {
                        return licenseRequestsRepository.findAll();
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

    //GET REQUEST
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/{id}", method =  RequestMethod.GET)
    public LicenseRequest getLicenseRequest(@RequestHeader(value="Authorization", defaultValue="") String token,
                                           @PathVariable(value = "id") int id){
        if(token.trim().length() > 0){
            User user = userRepository.findUserByToken(token);
            if(user != null){
                UserPermission userPermission = userPermissionRepository.findByUserId(user.getId());
                if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
                    return licenseRequestsRepository.findById(id);
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

    //UPDATE REQUEST
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/{id}", method =  RequestMethod.POST)
    public LicenseRequest updateLicenseRequest(@PathVariable(value = "id") int id, @RequestHeader(value = "Authorization", defaultValue="") String token,
                                               @RequestParam(defaultValue = "0") int person_id,
                                               @RequestParam(defaultValue = "0") int user_id, @RequestParam(defaultValue = "0") int status,
                                               @RequestParam(defaultValue = "") String date, @RequestParam(defaultValue = "") String license_term,
                                               @RequestParam(defaultValue = "") String mail, @RequestParam(defaultValue = "") String payment){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                //UPDATE
                LicenseRequest licenseRequest = licenseRequestsRepository.findById(id);
                if(licenseRequest != null){
                    if(user_id != 0){
                        licenseRequest.setUserId(user_id);
                    }
                    if(status == 0){
                        licenseRequest.setStatus(status);
                    }else if(status == 1){
                        licenseRequest.setStatus(status);
                        //SET PAY LICENSE FOR USER
                        User user = userRepository.findById(licenseRequest.getUserId());
                        if(user != null){
                            user.setLicense(Constants.LICENSE_STATUS_PAID);
                            user.setLicense_term(licenseRequest.getLicenseTerm());
                            userRepository.save(user);
                            // после успешной оплаты запускать запрос уведомления:   (протестировать уведомление)
                            List<FirebaseNotificationClient> clientIds = (List<FirebaseNotificationClient>) firebaseNotificationClientRepository.findAllByUserId(user.getId());
                            if(clientIds != null && clientIds.size() > 0){
  /*                              try {
                                    pushNotificationsService.sendPushNotification(new ConvertUtil().convertListToArrayString(clientIds), Constants.NOTIFICATION_TITLE_LICENSE_END_TITLE,
                                            Constants.NOTIFICATION_TITLE_LICENSE_BUY, Constants.NOTIFICATION_TYPE_PROFILE);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }*/
                            }
                        }else {
                            throw new WrongDataException(Constants.ERROR_WRONG_DATA + " - user id");
                        }
                    }
                    //
                    if(date.trim().length() > 0){
                        licenseRequest.setDate(date);
                    }
                    if(license_term.length() > 0){
                        licenseRequest.setLicenseTerm(license_term);
                    }
                    if(mail.trim().length() > 0){
                        licenseRequest.setMail(mail);
                    }
                    if(payment.trim().length() > 0){
                        licenseRequest.setPayment(payment);
                    }
                    licenseRequestsRepository.save(licenseRequest);
                    return licenseRequestsRepository.findById(id);
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
}
