package com.example.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.JwtResponse;
import com.example.demo.JwtUtil;
import com.example.entities.UserInfo;
import com.example.repositories.UserInfoRepository;
import com.example.services.CustomUserDetailsService;
import com.example.services.UserInfoManagerImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserInfoController {

    @Autowired
	private JwtUtil jwtutil;

    @Autowired
    UserInfoManagerImpl userservice;
    
    @Autowired
    private UserInfoRepository repository;
    
    @Autowired
    private CustomUserDetailsService customuserdetailsservice;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody UserInfo myuser) {
    		System.out.println("patkan kar"+myuser.getPassword());
    	try
		{
    		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
    		Optional<UserInfo> user=repository.findByUsername(myuser.getUsername());
    		if(!bcrypt.matches(myuser.getPassword(), user.get().getPassword()))
    		{
    			throw new UsernameNotFoundException("credentials don't match");
    		}
    		UserDetails userdetails=customuserdetailsservice.loadUserByUsername(myuser.getUsername());
    		String token=jwtutil.generateToken(userdetails);
    		System.out.println("JWT "+token);
    		return ResponseEntity.ok(new JwtResponse(token));
    	}
    	catch(Exception ee)
    	{
    		ee.printStackTrace();
    		return null;
    	}

        
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserInfo signupRequest) {
        	if(repository.existsByUsername(signupRequest.getUsername()))
        		return ResponseEntity.ok("User name already taken ");
            String success = userservice.signup(signupRequest);
            return ResponseEntity.ok(success);
        
    }

    @PutMapping("/{userId}")
    public ResponseEntity<String> updateProfile(@PathVariable Long userId, @RequestBody UserInfo userInfo) {
        	userInfo.setId(userId);
            userservice.updateUserProfile(userInfo);
            return ResponseEntity.ok("Profile updated successfully");
    }
    
    @GetMapping("/getusers")
    public List<UserInfo> getUserDetails()
    {
    	return repository.findAll();
    }
}
