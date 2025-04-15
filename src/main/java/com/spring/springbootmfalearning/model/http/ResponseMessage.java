package com.spring.springbootmfalearning.model.http;

import java.sql.Timestamp;
import java.util.Date;

public class ResponseMessage {

    public static Timestamp timestamp = new Timestamp(new Date().getTime());
    public static String success = "Operation is success.";
    public static String fail = "Operation is fail.";
}