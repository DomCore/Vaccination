package com.api.Poletechnika.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class WrongDataException extends RuntimeException {

    public WrongDataException(String message) {
        super(message);
    }
}
