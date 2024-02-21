package com.jobder.app.authentication.controllers;

import com.jobder.app.authentication.dto.userdtos.ClientDTO;
import com.jobder.app.authentication.dto.userdtos.WorkerDTO;
import com.jobder.app.authentication.exceptions.InvalidClientException;
import com.jobder.app.authentication.exceptions.InvalidWorkerException;
import com.jobder.app.authentication.models.users.User;
import com.jobder.app.authentication.services.TokenService;
import com.jobder.app.authentication.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping(path = "/profile")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal User user){
        if(user.getRole().name().equals("CLIENT"))
            return new ResponseEntity<>(user.toClient(),HttpStatus.OK);
        else return new ResponseEntity<>(user.toWorker(),HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user){
        tokenService.cleanUserToken(user);
        return new ResponseEntity<>("Succesfully logged out!", HttpStatus.OK);
    }

    @GetMapping("/userSearchParameters")
    public ResponseEntity<?> getUserSearchParameters(@AuthenticationPrincipal User user){
        if(user.getRole().name().equals("CLIENT"))
            return new ResponseEntity<>(user.toClient().getSearchParameters(),HttpStatus.OK);
        else return new ResponseEntity<>("You are not a client!",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/resetUserSearchParameters")
    public ResponseEntity<?> resetUserSearchParameters(@AuthenticationPrincipal User user){
        ResponseEntity<?> response;
        try{
            userService.resetUserSearchParameters(user);
            response = new ResponseEntity<>("Success", HttpStatus.OK);
        }catch (InvalidClientException e){
            response = new ResponseEntity<>("You are not a client!", HttpStatus.BAD_REQUEST);
        }

         return response;
    }

    @PostMapping("/update/CLIENT")
    public ResponseEntity<?> updateClient(@AuthenticationPrincipal User user, @RequestBody ClientDTO clientDTO){
        ResponseEntity<?> response;
        try{
            userService.updateClient(user.getId(), clientDTO);
            response = new ResponseEntity<>("Success", HttpStatus.OK);
        }catch (InvalidClientException e){
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return response;
    }

    @PostMapping("/update/WORKER")
    public ResponseEntity<?> updateWorker(@AuthenticationPrincipal User user, @RequestBody WorkerDTO workerDTO){
        ResponseEntity<?> response;
        try{
            userService.updateWorker(user.getId(), workerDTO);
            response = new ResponseEntity<>("Success", HttpStatus.OK);
        }catch (InvalidWorkerException e){
            response = new ResponseEntity<>("You are not a client!", HttpStatus.BAD_REQUEST);
        }

        return response;
    }

}
