package com.jobder.app.authentication.controllers;

import com.jobder.app.authentication.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/profile")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal User user){
        if(user.getRole().name().equals("CLIENT"))
            return new ResponseEntity<>(user.toClient(),HttpStatus.OK);
        else return new ResponseEntity<>(user.toWorker(),HttpStatus.OK);
    }
}
