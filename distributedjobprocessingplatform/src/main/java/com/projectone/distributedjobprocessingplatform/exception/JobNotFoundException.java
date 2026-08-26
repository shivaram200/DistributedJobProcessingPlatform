package com.projectone.distributedjobprocessingplatform.exception;

public class JobNotFoundException extends RuntimeException{

    public JobNotFoundException(String message){
        super(message);
    }
}
