package com.example.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import com.example.entities.UserInfo;
import com.example.repositories.UserInfoRepository;

@Service
public class UserInfoManagerImpl {

    @Autowired
    private UserInfoRepository userRepository;

    public String signup(UserInfo userinfo)
    {
    	BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
    	String encryptedPass = bcrypt.encode(userinfo.getPassword());
    	userinfo.setPassword(encryptedPass);
    	userRepository.save(userinfo);
    	return "User Added Successfully";
    }
    
    public void updateUserProfile(UserInfo userprofile)
    {
    	userRepository.save(userprofile);
    }
}
