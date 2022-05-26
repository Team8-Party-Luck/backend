package com.partyluck.party_luck.exception;

import com.partyluck.party_luck.dto.ResponseDto;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@RestController
public class WebErrorController implements ErrorController {
//    @Override
//    public String getErrorPath(){
//        return null;
//    }
    @RequestMapping("/error")
    public ResponseDto handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if(status != null){
            int statusCode = Integer.valueOf(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                return new ResponseDto(false,404,"올바른 요청이 아닙니다.");
            } else {
                return new ResponseDto(false,500,"올바른 요청이 아닙니다.");
            }
        }

        return new ResponseDto(false,500,"올바른 요청이 아닙니다.");
    }
//    @DeleteMapping("/error")
//    public String handleErro1r(HttpServletRequest request) {
//        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//
//        if(status != null){
//            int statusCode = Integer.valueOf(status.toString());
//
//            if(statusCode == HttpStatus.NOT_FOUND.value()) {
//                return "error/404error";
//            } else {
//                return "error/error";
//            }
//        }
//
//        return "error/error";
//    }
//    @PostMapping("/error")
//    public String handleError2(HttpServletRequest request) {
//        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//
//        if(status != null){
//            int statusCode = Integer.valueOf(status.toString());
//
//            if(statusCode == HttpStatus.NOT_FOUND.value()) {
//                return "error/404error";
//            } else {
//                return "error/error";
//            }
//        }
//
//        return "error/error";
//    }
}
