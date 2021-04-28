package com.api.Poletechnika.controllers;

import com.api.Poletechnika.exceptions.WrongDataException;
import com.api.Poletechnika.models.FirebaseNotificationClient;
import com.api.Poletechnika.repository.FirebaseNotificationClientRepository;
import com.api.Poletechnika.services.PushNotificationsService;
import com.api.Poletechnika.utils.Constants;
/*import com.api.Poletechnika.utils.ConvertUtil;*/
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/notification")
public class NotificationController {

    @Autowired
    FirebaseNotificationClientRepository firebaseNotificationClientRepository;


    @Autowired
    PushNotificationsService pushNotificationsService;

    //private final String TOPIC = "Ezbitex Exchange";


    //REGISTRATION
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public void registrationNotificationUser(@RequestParam String token, @RequestParam(defaultValue =  "0") int user_id) {

        if(user_id != 0){
            FirebaseNotificationClient firebaseNotificationClient;
            if(firebaseNotificationClientRepository.findByToken(token) == null){
                firebaseNotificationClient = new FirebaseNotificationClient();
                firebaseNotificationClient.setToken(token);
                firebaseNotificationClient.setUserId(user_id);
                firebaseNotificationClientRepository.save(firebaseNotificationClient);

            }else {
                firebaseNotificationClient = firebaseNotificationClientRepository.findByToken(token);
                if(firebaseNotificationClient.getUserId() == 0){
                    firebaseNotificationClient.setUserId(user_id);
                    firebaseNotificationClientRepository.save(firebaseNotificationClient);
                }
            }
        }else {
            if(firebaseNotificationClientRepository.findByToken(token) == null){
                FirebaseNotificationClient firebaseNotificationClient = new FirebaseNotificationClient();
                firebaseNotificationClient.setToken(token);
                firebaseNotificationClient.setUserId(0);
                firebaseNotificationClientRepository.save(firebaseNotificationClient);
            }
        }
    }

    //DELETE CONSULTATION
    @RequestMapping(value = "/registration", method =  RequestMethod.DELETE)
    public void deleteConsulatation(@RequestParam String token){
        FirebaseNotificationClient firebaseNotificationClient = firebaseNotificationClientRepository.findByToken(token);
        if(firebaseNotificationClient != null){
            firebaseNotificationClientRepository.deleteById(firebaseNotificationClient.getId());
        }
    }

    //REGISTRATION
    @RequestMapping(value = "/message", method = RequestMethod.POST, produces = "application/json")
    public String sendMessage(@RequestParam String title, @RequestParam String body, @RequestParam String type,
                              @RequestParam(defaultValue =  "0") int user_id) throws JSONException {



        /*
        RestTemplate restTemplate = new RestTemplate();

        Map map = new HashMap<String, String>();
        map.put("username", "bob123");
        map.put("password", "myP@ssw0rd");
        ResponseEntity<String> resp = restTemplate.postForEntity(
                "http://poletechnika-api.fairtech-marketing.com/agronomy",
                map,
                String.class);
        return resp.getBody();*/


/*        JSONObject body = new JSONObject();
        body.put("to", "/topics/" + TOPIC);
        body.put("priority", "high");

        JSONObject notification = new JSONObject();
        notification.put("title", "Ezbitex");
        notification.put("body", "Welcome to Ezbitex Exchange!");

        JSONObject data = new JSONObject();
        data.put("Key-1", "JSA Data 1");
        data.put("Key-2", "JSA Data 2");
        data.put("Key-2", "JSA Data 2");

        body.put("notification", notification);
        body.put("data", data);*/
        //HttpEntity<String> request = new HttpEntity<>(body.toString());

        /**
         * { "notification": { "title": "JSA Notification", "body": "Happy Message!" },
         * "data": { "Key-1": "JSA Data 1", "Key-2": "JSA Data 2" }, "to":
         * "/topics/JavaSampleApproach", "priority": "high" }
         */


        //String deviceToken = "fA3c4XobVaM:APA91bGhWRtZzKKcFA4rEHiiak_5s4u3sphn-QDR5jdlgQbVYUayMXXYfDfKWjWWaV0yGBEXwTRuCGusNE2frB4kclL6UILiLNw8fAD_xxqtes1sVTTmkJGFX5Tab-FsfTbOu4aXnxQV";
        //String[] deviceToken = {"fA3c4XobVaM:APA91bGhWRtZzKKcFA4rEHiiak_5s4u3sphn-QDR5jdlgQbVYUayMXXYfDfKWjWWaV0yGBEXwTRuCGusNE2frB4kclL6UILiLNw8fAD_xxqtes1sVTTmkJGFX5Tab-FsfTbOu4aXnxQV",
        //        "f62unz_25gk:APA91bFcXLyeVvA-TERJ3sj50YI3Q5F8YNeOf75papGVr-_2ydYM33biiTT3WBvxAduIGDHPz98h-63hrDmOqR2SxRJpXDu26auBdBqw51Ppahwzyd0KGTS2YCtGlgSkqUn-eGZCrjjE"};



        Iterable<FirebaseNotificationClient> clients;
        if(user_id == 0){
            clients = firebaseNotificationClientRepository.findAll();
        }else {
            clients = firebaseNotificationClientRepository.findAllByUserId(user_id);
        }

/*
        try {
            String pushNotification = pushNotificationsService.sendPushNotification(new ConvertUtil().convertListToArrayString(clients), title, body, type);
            return pushNotification;
        } catch (IOException e) {
            throw new WrongDataException(Constants.ERROR_WRONG_DATA);
        }*/
        return null;
    }
}
