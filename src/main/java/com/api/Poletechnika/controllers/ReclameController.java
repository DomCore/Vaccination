package com.api.Poletechnika.controllers;

import com.api.Poletechnika.exceptions.AccessException;
import com.api.Poletechnika.exceptions.NotFoundException;
import com.api.Poletechnika.exceptions.WrongDataException;
import com.api.Poletechnika.models.Reclam;
import com.api.Poletechnika.models.UserPermission;
import com.api.Poletechnika.repository.ReclameRepository;
import com.api.Poletechnika.repository.UserPermissionRepository;
import com.api.Poletechnika.repository.UserRepository;
import com.api.Poletechnika.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/reclame")
public class ReclameController {

    @Autowired
    ReclameRepository reclameRepository;

    @Autowired
    UserPermissionRepository userPermissionRepository;
    @Autowired
    UserRepository userRepository;

    //READ DATA
    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public Reclam getStartReclame() {
        return reclameRepository.findReclamByType("start");
    }

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public Reclam getMainReclame() {
        return reclameRepository.findReclamByType("main");
    }

    //UPDATE
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public Reclam  updateStartReclame(@RequestHeader(value = "Authorization", defaultValue="") String token, @RequestParam(defaultValue = "0") int person_id,
                            @RequestParam(value = "title", defaultValue =  "") String title,
                            @RequestParam(value = "image", defaultValue =  "") String image,
                            @RequestParam(value = "video", defaultValue =  "") String video) {

        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                Reclam  reclam = reclameRepository.findReclamByType("start");
                if (reclam != null) {
                    if(!title.trim().isEmpty()){
                        reclam.setTitle(title);
                    }
                    if(!image.trim().isEmpty()){
                        reclam.setImage(image);
                    }
                    if(!video.trim().isEmpty()){
                        reclam.setVideo(video);
                    }
                    reclameRepository.save(reclam);
                    return reclameRepository.findReclamByType("start");
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

    @RequestMapping(value = "/main", method = RequestMethod.POST)
    public Reclam  updateMainReclame(@RequestHeader(value = "Authorization", defaultValue="") String token, @RequestParam(defaultValue = "0") int person_id,
                                    @RequestParam(value = "title", defaultValue =  "") String title,
                                     @RequestParam(value = "image", defaultValue =  "") String image,
                                     @RequestParam(value = "video", defaultValue =  "") String video) {

        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                Reclam  reclam = reclameRepository.findReclamByType("main");
                if (reclam != null) {
                    if(!title.trim().isEmpty()){
                        reclam.setTitle(title);
                    }
                    if(!image.trim().isEmpty()){
                        reclam.setImage(image);
                    }
                    if(!video.trim().isEmpty()){
                        reclam.setVideo(video);
                    }
                    reclameRepository.save(reclam);
                    return reclameRepository.findReclamByType("main");
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
