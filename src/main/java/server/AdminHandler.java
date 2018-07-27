package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import server.helpers.IResponseManager;
import server.sessions.ISessionManager;
import server.webcontrollers.IAdminController;

import java.io.*;
import java.util.List;
import java.util.Map;

public class AdminHandler implements HttpHandler {

    private final IAdminController controller;
    private final ISessionManager sessionManager;
    private final IResponseManager responseManager;

    public static HttpHandler create(ISessionManager sessionManager,
                                     IAdminController controller,
                                     IResponseManager responseManager) {
        return new AdminHandler(sessionManager, controller, responseManager);
    }

    private AdminHandler(ISessionManager sessionManager,
                         IAdminController controller,
                         IResponseManager responseManager) {
        this.sessionManager = sessionManager;
        this.controller = controller;
        this.responseManager = responseManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        System.out.println(method);

        int loggedUserId = sessionManager.getCurrentUserId(httpExchange);

        if( loggedUserId == -1) {
            responseManager.redirectToLogin(httpExchange);

        } else {
            if (method.equals("GET")) {
                System.out.println(method);
                String uri = httpExchange.getRequestURI().toString();
                System.out.println(uri);
                switch (uri) {
                    case "/admin":
                        displayAdminHomePage(httpExchange);
                        break;
                    case "/admin/create_mentor":
                        displayCreateMentor(httpExchange);
                        break;
                    case "/admin/display_mentor":
                        displayMentor(httpExchange);
                        break;
                    case "/admin/create_level":
                        showExperienceLevelCreation(httpExchange);
                        break;
                    case "/admin/create_class":
                        displayCreateClassPage(httpExchange);
                        break;
                    case "/admin/assign_mentor_to_class":
                        displayAssignMentorToClass(httpExchange);
                        break;
                    case "/admin/edit_mentor":
                        displayEditMentor(httpExchange);
                        break;
                }
            }
            if (method.equals("POST")) {

                String uri = httpExchange.getRequestURI().toString();
                System.out.println("URI: " + uri);
                switch (uri) {
                    case "/admin": displayAdminHomePage(httpExchange);
                        break;
                    case "/admin/edit_mentor":
                        Map mentorData = responseManager.parseEditData(httpExchange);
                        editMentor(httpExchange, mentorData);
                        break;
                    case "/admin/display_mentor":
                        showMentorDetails(httpExchange);
                        break;
                    case "/admin/create_level":
                        handleExperienceLevelCreation(httpExchange);
                        break;
                    case "/admin/create_class":
                        saveClassToDb(httpExchange);
                        break;
                    case "/admin/assign_mentor_to_class":
                        handleAssignMentorToClass(httpExchange);
                        break;
                    case "/admin/create_mentor":
                        createMentor(httpExchange);
                        break;
                }
            }
        }
    }

    private void handleAssignMentorToClass(HttpExchange httpExchange) throws IOException {
        Map<String, String> inputs = responseManager.getInput(httpExchange);
        String mentorData = inputs.get("mentor");
        String classData = inputs.get("class");
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/admin/assign_mentor_to_class.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("mentors", controller.getAllMentorsCollection());
        model.with("classes",  controller.getAllClassesCollection());
        boolean isSuccess= controller.assignMentorToClass(mentorData, classData);
        String result;
        if(isSuccess) {
            result = mentorData + " assigned to class " + classData + "!";
        } else {
            result = "operation failed! student: " + mentorData;
        }
        model.with("result", result);
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void displayAssignMentorToClass(HttpExchange httpExchange) throws IOException {
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/admin/assign_mentor_to_class.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("mentors", controller.getAllMentorsCollection());
        model.with("classes",  controller.getAllClassesCollection());
        model.with("result", "");
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }


    private void saveClassToDb(HttpExchange httpExchange) throws IOException {
        Map<String, String> inputs = responseManager.getInput(httpExchange);
        String className = inputs.get("className");

        String info;
        if(controller.createClass(className)){
            info = "Class added successfully!";
        }else{
            info = "Something went wrong - perhaps You've used already existing name?";
        }
        String response;
        JtwigTemplate template = JtwigTemplate.classpathTemplate(
                "static/admin/create_class.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("classes", controller.getAllClassesCollection());
        model.with("info", info);
        response =template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void displayCreateClassPage(HttpExchange httpExchange) throws IOException {
        String response;
        JtwigTemplate template = JtwigTemplate.classpathTemplate(
                                "static/admin/create_class.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("classes", controller.getAllClassesCollection());
        model.with("info", "");
        response = template.render(model);
        responseManager.executeResponse(httpExchange,response);
    }



    private void editMentor(HttpExchange httpExchange, Map mentor) throws IOException {

        String info;
        boolean isMentorEdited = controller.editMentor(mentor);
        List<String> mentors = controller.getMentorsFullData();
        if(isMentorEdited) {
            info = "Mentor updated!";
        } else {
            info = "Problem occurred";
        }
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/admin/edit_mentor.html");

        JtwigModel model = JtwigModel.newModel();
        model.with("mentors", mentors);
        model.with("info", info);
        String response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void displayEditMentor(HttpExchange httpExchange) throws IOException {
        List<String> mentors = controller.getMentorsFullData();

        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/admin/edit_mentor.html");

        JtwigModel model = JtwigModel.newModel();
        model.with("mentors", mentors);
        String response = template.render(model);
        responseManager.executeResponse(httpExchange, response);

    }

    private void showMentorDetails(HttpExchange httpExchange) throws IOException {
        Map<String, String> inputs = responseManager.getInput(httpExchange);
        String name = inputs.get("mentorName");

        String mentorInfo = controller.seeMentorData(name);
        String uri = httpExchange.getRequestURI().toString();
        System.out.println("URI: " + uri);
        List<String> mentorsFullData = controller.getMentorsFullData();
        List<String> mentorsNames = controller.getMentorsNames();
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/admin/display_mentor.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("mentors", mentorsFullData);
        model.with("MentorsNames", mentorsNames);
        model.with("MentorInfo", mentorInfo);
        String response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void displayMentor(HttpExchange httpExchange) throws IOException {
        List<String> mentorsFullData = controller.getMentorsFullData();
        List<String> mentorsNames = controller.getMentorsNames();
        String info = "choose mentor to display";
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/admin/display_mentor.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("mentors", mentorsFullData);
        model.with("MentorsNames", mentorsNames);
        model.with("MentorInfo", info);
        String response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void displayCreateMentor(HttpExchange httpExchange) throws IOException {
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/admin/create_mentor.html");
        JtwigModel model = JtwigModel.newModel();
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void displayAdminHomePage(HttpExchange httpExchange) throws IOException {
        String response;
        int MAIN_INFO_INDEX = 0;
        int DETAILS_INDEX = 1;
        String[] adminData = controller.getAdminData(sessionManager
                .getCurrentUserId(httpExchange));
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/admin/profile.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("mainInfo", adminData[MAIN_INFO_INDEX]);
        model.with("details", adminData[DETAILS_INDEX]);
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void createMentor(HttpExchange httpExchange) throws IOException{
        Map<String, String> inputs = responseManager.getInput(httpExchange);

        String name = inputs.get("name");
        String password = inputs.get("password");
        String email = inputs.get("email");

        String info;
        if(controller.createMentor(name, password, email)){
            info = "Mentor added successfully!";
        }else{
            info = "Something went wrong :(";
        }
        String response;
        JtwigTemplate template = JtwigTemplate.classpathTemplate(
                "static/admin/create_mentor.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("info", info);
        response =template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }


    private void showExperienceLevelCreation(HttpExchange httpExchange) throws IOException {

        List<String> expLevels = controller.getAllLevels();
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/admin/create_level.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("result", "");
        model.with("levels", expLevels);

        String response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void handleExperienceLevelCreation(HttpExchange httpExchange) throws IOException {
        Map<String,String> inputs = responseManager.getInput(httpExchange);
        String levelName = inputs.get("level_name");
        int coinsLimit = Integer.parseInt(inputs.get("coins_limit"));

        boolean isExportSuccess =  controller.createLevel(levelName, coinsLimit);

        String result;
        if(! isExportSuccess) {
            result = "creation failure, try again (haven't You type already existing level or invalid data?)";
        } else {
            result = "creation success!";
        }

        List<String> expLevels = controller.getAllLevels();
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/admin/create_level.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("result", result);
        model.with("levels", expLevels);
        response = template.render(model);

        responseManager.executeResponse(httpExchange, response);
    }
}
