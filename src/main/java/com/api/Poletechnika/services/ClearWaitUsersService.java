package com.api.Poletechnika.services;

import com.api.Poletechnika.models.User;
import com.api.Poletechnika.repository.UserRepository;
import com.api.Poletechnika.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class ClearWaitUsersService {

    @Autowired
    UserRepository userRepository;

    //Каждый день в 5 утра сервис проверяет всех юзеров, если зарегистрировался но не вошел в течении 2х дней или недозарегистрировался - юзер удаляется

    //@Scheduled(fixedDelay = 20000)
    @Scheduled(cron = "0 0 5 * * *")
    public void fixedDelaySchedule() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        Date nowDate = c.getTime();

        Iterable<User> users = userRepository.findAll();
        for(User user : users){
            if(user.getRegistrationStatus() != null && user.getRegistrationStatus().equals(Constants.REGISTRATION_STATUS_WAIT)){

                try {
                    c.setTime(nowDate);

                    String date1 = user.getRegistration_date();
                    String date2 = sdf.format(c.getTime()); //now

                    c.setTime(sdf.parse(date1));
                    c.add(Calendar.DATE, 2);
                    String date3 = sdf.format(c.getTime());

                    Date dateNow = sdf.parse(date2);
                    Date dateWaitRegistration = sdf.parse(date3);

                    if(dateNow.compareTo(dateWaitRegistration) >= 0){
                        userRepository.deleteById(user.getId());
                    }

                } catch (ParseException e) {
                    //e.printStackTrace();
                }



            }
        }
    }
}
