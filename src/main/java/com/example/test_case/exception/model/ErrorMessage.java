package com.example.test_case.exception.model;

import java.time.ZonedDateTime;

public record ErrorMessage(int status, String error, ZonedDateTime dateTime) {}
