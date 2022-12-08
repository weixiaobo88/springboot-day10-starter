package com.rest.springbootemployee.exception;

public class NoCompanyFoundException extends RuntimeException {
    public NoCompanyFoundException() {
        super("No company found");
    }
}
