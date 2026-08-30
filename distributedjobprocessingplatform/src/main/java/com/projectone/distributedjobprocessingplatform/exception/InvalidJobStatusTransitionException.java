package com.projectone.distributedjobprocessingplatform.exception;

public class InvalidJobStatusTransitionException extends RuntimeException{

    public InvalidJobStatusTransitionException(String message){

        super(message);

    }
}
