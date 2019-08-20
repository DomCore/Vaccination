package com.api.Poletechnika.controllers;

import com.api.Poletechnika.exceptions.AccessException;
import com.api.Poletechnika.exceptions.NotFoundException;
import com.api.Poletechnika.exceptions.WrongDataException;
import com.api.Poletechnika.models.ConfigModel;
import com.api.Poletechnika.models.Reclam;
import com.api.Poletechnika.models.UserPermission;
import com.api.Poletechnika.repository.ConfigRepository;
import com.api.Poletechnika.repository.ReclameRepository;
import com.api.Poletechnika.repository.UserPermissionRepository;
import com.api.Poletechnika.repository.UserRepository;
import com.api.Poletechnika.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/config")
public class ConfigController {

    @Autowired
    ConfigRepository configRepository;

    @Autowired
    UserPermissionRepository userPermissionRepository;
    @Autowired
    UserRepository userRepository;

    //READ DATA
    @RequestMapping(value = "/appStatus", method = RequestMethod.GET)
    public ConfigModel getStartReclame() {
        return configRepository.findByName("app_status");
    }

    //UPDATE
    @RequestMapping(value = "/appStatus", method = RequestMethod.POST)
    public ConfigModel  updateStartReclame(@RequestHeader(value = "Authorization", defaultValue="") String token, @RequestParam(defaultValue = "0") int person_id,
                            @RequestParam(value = "value", defaultValue =  "") String value) {

        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                if(value.trim().length() > 0){
                    ConfigModel appStatus = getStartReclame();
                    switch (value){
                        case "on":
                            appStatus.setValue("on");
                            break;
                        case "off":
                            appStatus.setValue("off");
                            break;
                    }
                    configRepository.save(appStatus);
                }
                return getStartReclame();
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }
}
