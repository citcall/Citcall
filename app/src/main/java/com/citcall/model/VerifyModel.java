package com.citcall.model;

/**
 * Created by Agustya on 10/7/16.
 */

public class VerifyModel extends BaseModel {
    private boolean error;
    private String valid;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }
}
