package com.megthink.gateway.model;

public class NumReturnResponse {

    private NumberRange NumberRange;
    private String Route;
    private String Comments;

    public NumberRange getNumberRange() {
        return NumberRange;
    }

    public void setNumberRange(NumberRange numberRange) {
        NumberRange = numberRange;
    }

    public String getRoute() {
        return Route;
    }

    public void setRoute(String route) {
        Route = route;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }
}

