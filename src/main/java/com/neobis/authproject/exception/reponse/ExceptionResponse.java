package com.neobis.authproject.exception.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@AllArgsConstructor
public class ExceptionResponse {
    private HttpStatus httpStatus;
    private String exceptionName;
    private String message;
}
