package com.example.controllers;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.entities.UserProfile;
import com.example.repositories.ProfileRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    @Autowired
    private ProfileRepository repository;

    // Create a new profile
    @PostMapping
    public ResponseEntity<UserProfile> createProfile(@RequestBody UserProfile userProfile) {
        UserProfile createdProfile = repository.save(userProfile);
        return new ResponseEntity<>(createdProfile, HttpStatus.CREATED);
    }
    
    @PostMapping("/upload/image/{id}")
    public ResponseEntity<String> uplaodImage(@PathVariable("id") long id,@RequestParam("image") MultipartFile file) {
    	Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        try {
			deflater.setInput(file.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        deflater.finish();

        ByteArrayOutputStream outputStream = null;
		try {
			outputStream = new ByteArrayOutputStream(file.getBytes().length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        byte[] tmp = new byte[4*1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception e) {
        }
        repository.updateImageDataById(id, outputStream.toByteArray());
        
        return ResponseEntity.status(HttpStatus.OK)
                .body("Image uploaded successfully: ");
    }
    
    @PostMapping("/upload/document/{id}")
    public ResponseEntity<String> uplaodDocument(@PathVariable("id") long id,@RequestParam("document") List<MultipartFile> files) {
    	
    	for(MultipartFile file:files)
    	{
    	Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        try {
			deflater.setInput(file.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        deflater.finish();

        ByteArrayOutputStream outputStream = null;
		try {
			outputStream = new ByteArrayOutputStream(file.getBytes().length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        byte[] tmp = new byte[4*1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception e) {
        }
        repository.updateImageDataById(id, outputStream.toByteArray());
    	}
        return ResponseEntity.status(HttpStatus.OK)
                .body("Documents uploaded successfully: ");
    }

    // Retrieve all profiles
    @GetMapping
    public ResponseEntity<List<UserProfile>> getAllProfiles() {
    	 System.out.println("yaha pein toh hain na ");
        List<UserProfile> profiles = repository.findAll();
        System.out.println("tu ithech barobar ahe "+profiles.size());
        for(UserProfile profile:profiles)
        {
        	Inflater inflater = new Inflater();
        	inflater.setInput(profile.getProfilePhoto());
        	ByteArrayOutputStream outputStream = new ByteArrayOutputStream(profile.getProfilePhoto().length);
        	byte[] tmp = new byte[4*1024];
        	try {
        			while (!inflater.finished()) {
        				int count = inflater.inflate(tmp);
        				outputStream.write(tmp, 0, count);
        			}
        		outputStream.close();
        	} catch (Exception exception) {
        										}
        	profile.setProfilePhoto(outputStream.toByteArray());
        }
        for(UserProfile profile:profiles)
        {
        	List<byte[]> temp = new ArrayList<>();
        	for(byte[] doc:profile.getCertificates())
        	{
        	Inflater inflater = new Inflater();
        	inflater.setInput(doc);
        	ByteArrayOutputStream outputStream = new ByteArrayOutputStream(doc.length);
        	byte[] tmp = new byte[4*1024];
        	try {
        			while (!inflater.finished()) {
        				int count = inflater.inflate(tmp);
        				outputStream.write(tmp, 0, count);
        			}
        		outputStream.close();
        	} catch (Exception exception) {
        										}
        		temp.add(outputStream.toByteArray());
        	}
        	profile.setCertificates(temp);
       }
        return new ResponseEntity<>(profiles, HttpStatus.OK);
    }

    // Retrieve a single profile by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> getProfileById(@PathVariable("id") Long id) {
        Optional<UserProfile> profileOptional = repository.findById(id);
        UserProfile profile = profileOptional.get();
        Inflater inflater = new Inflater();
    	inflater.setInput(profile.getProfilePhoto());
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream(profile.getProfilePhoto().length);
    	byte[] tmp = new byte[4*1024];
    	try {
    			while (!inflater.finished()) {
    				int count = inflater.inflate(tmp);
    				outputStream.write(tmp, 0, count);
    			}
    		outputStream.close();
    	} catch (Exception exception) {
    										}
    	
    	profile.setProfilePhoto(outputStream.toByteArray());
    	
    	List<byte[]> temp = new ArrayList<>();
    	for(byte[] doc:profile.getCertificates())
    	{
    	Inflater inflater2 = new Inflater();
    	inflater2.setInput(doc);
    	ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream(doc.length);
    	byte[] tmp3 = new byte[4*1024];
    	try {
    			while (!inflater2.finished()) {
    				int count = inflater2.inflate(tmp3);
    				outputStream2.write(tmp3, 0, count);
    			}
    		outputStream2.close();
    	} catch (Exception exception) {
    										}
    		temp.add(outputStream2.toByteArray());
    	}
    	profile.setCertificates(temp);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    // Update an existing profile
    @PutMapping("/{id}")
    public ResponseEntity<UserProfile> updateProfile(@PathVariable("id") Long id, @RequestBody UserProfile userProfile) {
        Optional<UserProfile> existingProfile = repository.findById(id);
        if (existingProfile.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userProfile.setId(id);
        repository.save(userProfile);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

