package com.projectone.distributedjobprocessingplatform.exception;

public class UnexpectedException extends RuntimeException{

    public UnexpectedException(){
        super("Unexpected error occurred please try again in some time.");
    }
}
