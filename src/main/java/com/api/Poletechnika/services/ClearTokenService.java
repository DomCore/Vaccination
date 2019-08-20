package com.api.Poletechnika.services;

import com.api.Poletechnika.models.User;
import com.api.Poletechnika.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ClearTokenService {

    @Autowired
    UserRepository userRepository;

    //Каждый день в 6 утра сервис проверяет всех юзеров, не прошло ли 30 дней с момента последнего исспользования токена

    //@Scheduled(fixedDelay = 20000)
    @Scheduled(cron = "0 0 6 * * *")
    public void fixedDelaySchedule() {
        //System.out.println("fixedDelaySchedule every 10 seconds" + new Date());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        Date nowDate = c.getTime();

        Iterable<User> users = userRepository.findAll();
        for(User user : users){
            if(user.getToken_date() != null && user.getToken_date().trim().length() > 0){
                try {
                    c.setTime(nowDate);

                    String date1 = user.getToken_date();
                    String date2 = sdf.format(c.getTime()); //now

                    c.setTime(sdf.parse(date1));
                    c.add(Calendar.DATE, 30);
                    String date3 = sdf.format(c.getTime());

                    Date dateNow = sdf.parse(date2);
                    Date dateToken = sdf.parse(date3);

                    if(dateNow.compareTo(dateToken) >= 0){
                        user.setToken(null);
                        user.setToken_date(null);
                    }

                } catch (ParseException e) {
                    //e.printStackTrace();
                }
            }
        }
        userRepository.save(users);
    }
}
