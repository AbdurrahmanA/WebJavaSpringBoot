package com.example.demo.listeners;

import com.example.demo.data.InformationEntity;
import com.example.demo.data.UserEntity;
import com.example.demo.services.InformationService;
import com.example.demo.services.UserService;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SessionListener implements HttpSessionListener {

    private static UserService staticUserService;
    private static InformationService staticInformationService;

    @Autowired
    public void setUserService(UserService userService) {
        SessionListener.staticUserService = userService;
    }

    @Autowired
    public void setInformationService(InformationService informationService) {
        SessionListener.staticInformationService = informationService;
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        Object userObj = event.getSession().getAttribute("user");
        if (userObj instanceof UserEntity user) {
            String preference = user.getSortPreference();
            staticUserService.saveUserSortPreference(user, preference);
            System.out.println("Session expired. User sort preference saved for: " + user.getLogin());

            List<InformationEntity> pending = (List<InformationEntity>) event.getSession().getAttribute("pendingCreates");
            if (pending != null) {
                for (InformationEntity info : pending) {
                    System.out.println("Saving: " + info.getTitle() + " | sharedWith: " +
                            (info.getSharedWith() == null ? "everyone" : info.getSharedWith().getLogin()));
                    staticInformationService.createInformation(info);
                }
            }
        }
    }
}
