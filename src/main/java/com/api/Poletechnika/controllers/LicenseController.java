package com.api.Poletechnika.controllers;


import com.api.Poletechnika.exceptions.AccessException;
import com.api.Poletechnika.exceptions.NotFoundException;
import com.api.Poletechnika.exceptions.WrongDataException;
import com.api.Poletechnika.models.License;
import com.api.Poletechnika.models.UserPermission;
import com.api.Poletechnika.repository.LicenseRepository;
import com.api.Poletechnika.repository.UserPermissionRepository;
import com.api.Poletechnika.repository.UserRepository;
import com.api.Poletechnika.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/license")
public class LicenseController {

    @Autowired
    LicenseRepository licenseRepository;

    @Autowired
    UserPermissionRepository userPermissionRepository;
    @Autowired
    UserRepository userRepository;

    //GET LICENSE
    @RequestMapping(value = "", method = RequestMethod.GET)
    public License getAppLicense() {
        List<License> licenses = (List) licenseRepository.findAll();
        return licenses.get(0);
    }

    //UPDATE LICENSE
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "", method = RequestMethod.POST)
    public License changeAppLicense(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                    @RequestParam(defaultValue = "0") int term, @RequestParam(defaultValue = "") String description,
                                    @RequestParam(defaultValue = "0") int price, @RequestParam(defaultValue = "") String invoice,
                                    @RequestParam(defaultValue = "") String type, @RequestParam(defaultValue = "") String typeTitle) {
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                List<License> licenses = (List) licenseRepository.findAll();
                License updateLicense = licenses.get(0);
                if(updateLicense != null){
                    if(term != 0){
                        updateLicense.setTerm(term);
                    }
                    if(description.trim().length() > 0){
                        updateLicense.setDescription(description);
                    }
                    if(price != 0){
                        updateLicense.setPrice(price);
                    }
                    if(invoice.trim().length() > 0){
                        updateLicense.setInvoice(invoice);
                    }
                    if(type.trim().length() > 0){
                        updateLicense.setType(type);
                    }
                    if(typeTitle.trim().length() > 0){
                        updateLicense.setTypeTitle(typeTitle);
                    }
                    licenseRepository.save(updateLicense);
                    return getAppLicense();
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
