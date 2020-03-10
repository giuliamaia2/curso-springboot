package com.giulia.exception;

public class RegraNegocioException extends RuntimeException { // em tempo de execução, não precisa tratar ela.

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RegraNegocioException(String msg) {
		super(msg);
	}

}
