package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public static Result upload() {
        return ok(upload.render("Your new application is ready."));
    }

    public static Result statistics() {
        return ok(statistics.render("Your new application is ready."));
    }

    public static Result about() {
        return ok(about.render("Your new application is ready."));
    }




}
