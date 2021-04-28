package com.api.Poletechnika.services;

import com.api.Poletechnika.models.FirebaseNotificationClient;
import com.api.Poletechnika.models.User;
import com.api.Poletechnika.repository.FirebaseNotificationClientRepository;
import com.api.Poletechnika.repository.UserRepository;
import com.api.Poletechnika.utils.Constants;
/*import com.api.Poletechnika.utils.ConvertUtil;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Null;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class LicenseNotifyService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PushNotificationsService pushNotificationsService;

    @Autowired
    FirebaseNotificationClientRepository firebaseNotificationClientRepository;

    private SimpleDateFormat sdf;
    private Calendar c;
    private Date nowDate;

    //Каждый день в 6 утра сервис проверяет всех юзеров, проверяет у кого неделя до окончания лицензии, 3 дня и окончание
    //@Scheduled(fixedDelay = 10000)
    @Scheduled(cron = "0 0 6 * * *")
    public void fixedDelaySchedule() {
        //System.out.println("fixedDelaySchedule every 10 seconds" + new Date());

        sdf = new SimpleDateFormat("yyyy-MM-dd");
        c = Calendar.getInstance();
        nowDate = c.getTime();

        Iterable<User> users = userRepository.findAllByLicense(Constants.LICENSE_STATUS_PAID);

        checkEndLicense(users, 7);
        checkEndLicense(users, 3);
        checkEndLicense(users, 0);

    }



    private void checkEndLicense(Iterable<User> users, int days){
        for(User user : users){

            if(user.getLicense_term() != null){
                try {
                    ////
                    c.setTime(nowDate);

                    String date1 = user.getLicense_term();
                    String date2 = sdf.format(c.getTime()); //now

                    c.setTime(sdf.parse(date1));
                    c.add(Calendar.DATE, days);

                    String date3 = sdf.format(c.getTime());

                    Date dateNow = sdf.parse(date2);
                    Date dateSevenDToEnd = sdf.parse(date3);

                    if(dateNow.compareTo(dateSevenDToEnd) == 0){
                        List<FirebaseNotificationClient> clientIds = (List<FirebaseNotificationClient>) firebaseNotificationClientRepository.findAllByUserId(user.getId());
                        if(clientIds != null && clientIds.size() > 0){
                            String messageBody;
                            switch (days){
                                case 7:
                                    messageBody = Constants.NOTIFICATION_TITLE_LICENSE_END_7;
                                    break;
                                case 3:
                                    messageBody = Constants.NOTIFICATION_TITLE_LICENSE_END_3;
                                    break;
                                case 0:
                                    messageBody = Constants.NOTIFICATION_TITLE_LICENSE_END;
                                    //END LICENSE
                                    user.setLicense(Constants.LICENSE_STATUS_FREE);
                                    user.setLicense_term(null);
                                    userRepository.save(user);
                                    break;
                                default:
                                    messageBody = Constants.NOTIFICATION_TITLE_LICENSE_END;
                                    break;
                            }
/*                            pushNotificationsService.sendPushNotification(new ConvertUtil().convertListToArrayString(clientIds), Constants.NOTIFICATION_TITLE_LICENSE_END_TITLE,
                                    messageBody, Constants.NOTIFICATION_TYPE_PROFILE);*/
                        }
                    }
                }catch (ParseException e) {
                    //e.printStackTrace();
                } /*catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        }
    }
}

// 7   3   and end license
// уведомить пользователя когда у него клайнула платная лицензия (когда он купил)