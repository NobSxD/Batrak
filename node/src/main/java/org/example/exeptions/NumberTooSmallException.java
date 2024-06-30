package org.example.exeptions;

public class NumberTooSmallException extends RuntimeException{
	public NumberTooSmallException() {
		super("Number is too small, it should be at least 11");
	}
}
