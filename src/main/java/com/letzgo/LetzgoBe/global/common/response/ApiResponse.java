package com.letzgo.LetzgoBe.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private ReturnCode returnCode;
    private String returnMessage;
    private T data;
    private Page<T> page;

    public static <T> ApiResponse of(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.returnCode = ReturnCode.SUCCESS;
        response.returnMessage = ReturnCode.SUCCESS.getMessage();
        response.data = data;

        return response;
    }

    public static <T> ApiResponse<T> of(Page<T> page) {
        ApiResponse<T> response = new ApiResponse<>();
        response.returnCode = ReturnCode.SUCCESS;
        response.returnMessage = ReturnCode.SUCCESS.getMessage();
        response.page = page;

        return response;
    }

    public static <T> ApiResponse of(ReturnCode returnCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.returnCode = ReturnCode.SUCCESS;
        response.returnMessage = ReturnCode.SUCCESS.getMessage();

        return response;
    }
}
