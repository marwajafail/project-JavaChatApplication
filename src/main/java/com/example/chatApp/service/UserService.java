package com.example.chatApp.service;

import com.example.chatApp.exception.InformationExistException;
import com.example.chatApp.model.LoginRequest;
import com.example.chatApp.model.LoginResponse;
import com.example.chatApp.model.User;
import com.example.chatApp.repository.UserRepository;
import com.example.chatApp.security.JWTUtils;
import com.example.chatApp.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private MyUserDetails myUserDetails;

    @Autowired
    public UserService(UserRepository userRepository,
                       @Lazy PasswordEncoder passwordEncoder,
                       JWTUtils jwtUtils,
                       @Lazy AuthenticationManager authenticationManager,
                       @Lazy MyUserDetails myUserDetails
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.myUserDetails = myUserDetails;
    }
     // Creates a new user.
     // If the email is unique, it saves the user with an encoded password.
     //If the email already exists, it throws an exception.
    public User createUser(User userObject) {

        if (!userRepository.existsByEmailAddress(userObject.getEmailAddress())) {

            userObject.setPassword(passwordEncoder.encode(userObject.getPassword()));
            return userRepository.save(userObject);

        } else {
            throw new InformationExistException("User with email address " + userObject.getEmailAddress() + " already exists");
        }
    }

    public User findUserWithEmail(String email) {
        return userRepository.findUserByEmailAddress(email);
    }
   //Logs in a user.
   //Authenticates the user and generates a JWT if successful.
   //If not, returns an error message.
    public ResponseEntity<?> loginUser(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            myUserDetails = (MyUserDetails) authentication.getPrincipal();
            final String JWT = jwtUtils.generateJwtToken(myUserDetails);
            return ResponseEntity.ok(new LoginResponse(JWT));

        } catch (Exception e) {
            return ResponseEntity.ok(new LoginResponse("Error! username or password is incorrect"));
        }


    }
}

