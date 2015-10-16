package com.tobyrich.dev.hangarapp.events;

public class RajawaliSurfaceLoad {
    private final String message;
    private final boolean success;


    public RajawaliSurfaceLoad(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
