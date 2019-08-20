package com.api.Poletechnika.controllers;

import com.api.Poletechnika.exceptions.AccessException;
import com.api.Poletechnika.exceptions.NotFoundException;
import com.api.Poletechnika.exceptions.WrongDataException;
import com.api.Poletechnika.models.AuthorizationModel;
import com.api.Poletechnika.models.Policy;
import com.api.Poletechnika.models.User;
import com.api.Poletechnika.repository.PolicyRepository;
import com.api.Poletechnika.repository.UserRepository;
import com.api.Poletechnika.utils.AesEncriptionUtil;
import com.api.Poletechnika.utils.Constants;
import com.api.Poletechnika.utils.DateUseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping(value = "/profile")
public class RegistrationController {

    @Autowired
    PolicyRepository policyRepository;

    @Autowired
    UserRepository userRepository;

    //GET REGISTRATION POLICY
    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public Policy getStartReclame() {
        return policyRepository.findById(1);
    }

    //SEND REGISTRATION REQUEST
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public void registrationRequest(@RequestParam String name, @RequestParam String phone, @RequestParam String mail,
                                   @RequestParam String website, @RequestParam String registry, @RequestParam String company,
                                      @RequestParam String policy) {

        if(policy.equals("true")){
            //CHECK IF NUMBER USED ALREADY
            if(userRepository.findUserByPhone(phone) == null || userRepository.findUserByPhone(phone) != null &&
                    userRepository.findUserByPhone(phone).getRegistration_status().equals(Constants.REGISTRATION_STATUS_WAIT)){
                User user;

                if(userRepository.findUserByPhone(phone) == null){
                    user = new User();
                }else {
                    user = userRepository.findUserByPhone(phone);
                }

                user.setName(name);
                user.setPhone(phone);
                user.setMail(mail);
                user.setWebsite(website);
                user.setRegistry(registry);
                user.setCompany(company);
                user.setLicense(Constants.LICENSE_STATUS_FREE);
                //set registration status
                user.setRegistration_status(Constants.REGISTRATION_STATUS_WAIT);
                //set regitration date
                user.setRegistration_date(new DateUseUtil().getCurrentDate());

//ОТПРАВКА ПАРОЛЯ НА СМС
                //Password
                String passwordForUser = generatePass();
                String passwordEncrypt = AesEncriptionUtil.encrypt(passwordForUser);
                user.setPass(passwordEncrypt);

                userRepository.save(user);
            }else {
                throw new WrongDataException(Constants.ERROR_REGISTRATION_NUMBER_USE);
            }
        }else {
            throw new WrongDataException(Constants.ERROR_REGISTRATION_POLICY);
        }
    }

    //SIGN IN
    @RequestMapping(value = "/signIn", method = RequestMethod.POST)
    public AuthorizationModel signInRequest(@RequestParam String phone, @RequestParam String pass) {


       AuthorizationModel userSignInData = new AuthorizationModel();
       User usersByPhone = userRepository.findUserByPhone(phone);

       if(usersByPhone != null){
           if(usersByPhone.getPass().equals(AesEncriptionUtil.encrypt(pass))){
               if(usersByPhone.getRegistration_status().equals(Constants.REGISTRATION_STATUS_WAIT)){
                    usersByPhone.setRegistration_status(Constants.REGISTRATION_STATUS_COMPLETE);
               }

               userSignInData.setUser_id(String.valueOf(usersByPhone.getId()));

               String token = generateToken();
               while(userRepository.findUserByToken(token) != null){
                   token = generateToken();
               }
               userSignInData.setAuth_token(token);
               //SAVE TOKEN IN DB
               usersByPhone.setToken(token);
               usersByPhone.setToken_date(new DateUseUtil().getCurrentDate());
               userRepository.save(usersByPhone);
           }else {
               throw new AccessException(Constants.ERROR_WRONG_PASS);
           }
       }else {
           throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
       }
        return userSignInData;
    }

    //SIGN OUT
    @RequestMapping(value = "/signOut", method = RequestMethod.POST)
    public void signOutRequest(@RequestHeader("Authorization") String token, @RequestParam int user_id) {

        User userById = userRepository.findById(user_id);
        if(userById != null){
            if(userById.getToken() != null && token.trim().length() > 0 && userById.getToken().equals(token)){
                //DELETE TOKEN IN DB
                userById.setToken_date(null);
                userById.setToken(null);
                userRepository.save(userById);
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);

            }
        }else {
            throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
        }
    }


    //RESET PASS
    @RequestMapping(value = "/signIn/resetPass", method = RequestMethod.POST)
    public String resetPassRequest(@RequestParam String phone, @RequestParam String way) {

// WAY HAVE TWO VARIABLES: phone / mail

        User userByPhone = userRepository.findUserByPhone(phone);

        if(userByPhone!= null){
            switch (way){
                case "phone":
//ОТПРАВЛЯЕМ ЗАПРОС С ПАРОЛЕМ НА НОМЕР ТЕЛЕФОНА
                    return "SEND ON PHONE";
                case "mail":
//ОТПРАВЛЯЕМ ЗАПРОС С ПАРОЛЕМ НА ПОЧТУ
                    return "SEND ON MAIL";
                default:
//ОТПРАВЛЯЕМ ЗАПРОС С ПАРОЛЕМ НА НОМЕР ТЕЛЕФОНА
                    return "SEND ON PHONE";
            }
        }else {
            return "USER NOT FOUND";
        }
    }




    private String generatePass(){
        String candidateChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabsdefghjklmnopqrstuvwxyz1234567890";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(candidateChars.charAt(random.nextInt(candidateChars.length())));
        }

        return sb.toString();
    }

    private String generateToken(){
        String candidateChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabsdefghjklmnopqrstuvwxyz1234567890";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            sb.append(candidateChars.charAt(random.nextInt(candidateChars.length())));
        }

        return sb.toString();
    }

}
