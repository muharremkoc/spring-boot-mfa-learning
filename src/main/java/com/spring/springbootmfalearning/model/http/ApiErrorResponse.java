package com.spring.springbootmfalearning.model.http;

import java.util.List;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ApiErrorResponse<T> extends ApiResponse<T> {

    private String path;
    private List<String> errors;

    public ApiErrorResponse() {
        super();
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setStatusCode(HttpStatus.BAD_REQUEST.value());
        this.setMessage(ResponseMessage.fail);
    }
}