package com.vsynytsyn.userservice.controller;


import com.vsynytsyn.commons.responce.ErrorResponse;
import com.vsynytsyn.userservice.domain.UserEntity;
import com.vsynytsyn.userservice.dto.UserDTO;
import com.vsynytsyn.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    ResponseEntity<Page<UserEntity>> getAll(
            Pageable pageable, @RequestParam(required = false) String username
    ) {
        return ResponseEntity.ok(userService.getAll(pageable, username));
    }


    @GetMapping("/{id}")
    ResponseEntity<UserEntity> getOne(
            @PathVariable(name = "id") Long userId
    ) {
        return ResponseEntity.of(Optional.of(userService.getOne(userId)));
    }


    @PostMapping("/create")
    ResponseEntity<Object> create(
            @RequestBody UserEntity userEntity
    ) {
        try {
            UserEntity e = userService.create(userEntity);
            if (e == null) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(e, HttpStatus.CREATED);
        } catch (ConstraintViolationException exception) {
            StringBuilder message = new StringBuilder();
            for (ConstraintViolation<?> constraintViolation : exception.getConstraintViolations()) {
                message.append(String.format("passed: '%s', error: '%s'\n", constraintViolation.getInvalidValue(),
                        constraintViolation.getMessage()));
            }
            return new ErrorResponse(message.toString(), HttpStatus.BAD_REQUEST).getResponseEntity();
        } catch (RuntimeException exception) {
            return new ErrorResponse(exception.getMessage(), HttpStatus.CONFLICT).getResponseEntity();
        }
    }


    @PostMapping("/{id}")
    public ResponseEntity<Object> update(
            @PathVariable(name = "id") UserEntity userEntity,
            @RequestBody UserDTO userDTO
    ) {
        if (userEntity == null)
            return ResponseEntity.notFound().build();
        UserEntity updated = userService.update(userEntity, userDTO);
        if (updated == null)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(
            @PathVariable("id") UserEntity userEntity
    ) {
        try {
            if (userService.delete(userEntity, null))
                return ResponseEntity.noContent().build();
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT).getResponseEntity();
        }
    }
}
