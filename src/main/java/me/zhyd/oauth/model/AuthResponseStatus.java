package me.zhyd.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @version 1.0
 * @since 1.8
 */
@Getter
@AllArgsConstructor
public enum AuthResponseStatus {
    SUCCESS(2000, "Success"),
    FAILURE(5000, "Failure"),
    NOT_IMPLEMENTED(5001, "Not Implemented"),
    PARAMETER_INCOMPLETE(5002, "Parameter incomplete"),
    UNSUPPORTED(5003, "Unsupported operation"),
    NO_AUTH_SOURCE(5004, "AuthSource cannot be null"),
    UNIDENTIFIED_PLATFORM(5005, "Unidentified platform"),
    ILLEGAL_REDIRECT_URI(5006, "Illegal redirect uri"),
    ILLEGAL_REQUEST(5007, "Illegal request"),
    ILLEGAL_CODE(5008, "Illegal code"),
    ;

    private int code;
    private String msg;
}

