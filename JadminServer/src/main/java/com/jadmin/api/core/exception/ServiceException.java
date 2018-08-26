package com.jadmin.api.core.exception;

/**
 * Service异常
 *
 * @author Jadmin
 * @date 2018/06/09
 */
public class ServiceException extends RuntimeException {
    public ServiceException(final String message) {
        super(message);
    }

    public ServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
