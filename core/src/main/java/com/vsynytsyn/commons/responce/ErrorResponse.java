package com.vsynytsyn.commons.responce;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.NotNull;

@Getter
public class ErrorResponse {

    private final ResponseEntity<Object> responseEntity;


    public ErrorResponse(@NotNull String message, @NotNull HttpStatus httpStatus) throws RuntimeException {
        try {
            String jsonMessage = new ObjectMapper().writeValueAsString(new ErrorMessage(message));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Type", "application/json");
            responseEntity = new ResponseEntity<>(
                    jsonMessage,
                    httpHeaders,
                    httpStatus);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }


    @AllArgsConstructor
    @Getter
    private static class ErrorMessage {
        private final String message;
    }

}
