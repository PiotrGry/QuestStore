package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import server.helpers.IResponseManager;
import server.sessions.ISessionManager;
import server.webcontrollers.IStudentController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class StudentHandler implements HttpHandler {

    private final IStudentController controller;
    private final ISessionManager sessionManager;
    private final IResponseManager responseManager;


    public static HttpHandler create(ISessionManager sessionManager,
                                     IStudentController controller,
                                     IResponseManager responseManager) {
        return new StudentHandler(sessionManager, controller, responseManager);
    }

    private StudentHandler(ISessionManager sessionManager,
                           IStudentController controller,
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
                    case "/student":
                        displayStudentHomePage(httpExchange);
                        break;
                    case "/student/menage_team":
                        displayStudentTeamData(httpExchange);
                        break;
                    case "/student/buy_artifact":
                        displayArtifacts(httpExchange);
                        break;
                }
            }
            if (method.equals("POST")) {
                System.out.println(method);
                String uri = httpExchange.getRequestURI().toString();
                System.out.println(uri);
                switch (uri) {
                    case "/student/buy_artifact":
                        buyArtifact(httpExchange);
                        break;
                }
            }
        }
    }

    private void displayStudentHomePage(HttpExchange httpExchange) throws IOException {
        String response;
        int MAIN_INFO_INDEX = 0;
        int DETAILS_INDEX = 1;
        String[] studentData = controller.getStudentData(sessionManager
                .getCurrentUserId(httpExchange));
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/student/profile.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("mainInfo", studentData[MAIN_INFO_INDEX]);
        model.with("details", studentData[DETAILS_INDEX]);
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void displayStudentTeamData(HttpExchange httpExchange) throws IOException{
        int studentId = this.sessionManager.getCurrentUserId(httpExchange);
        String teamName = controller.getStudentGroup(studentId);
        List<String> memebrs = controller.getTeamMembers(studentId);
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/student/menage_team.html");

        JtwigModel model = JtwigModel.newModel();
        model.with("teamName", teamName);
        model.with("members", memebrs);
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void displayArtifacts(HttpExchange httpExchange) throws IOException{
        List<String> artifacts = controller.getArtifacts();
        int studentId = this.sessionManager.getCurrentUserId(httpExchange);
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/student/buy_artifact.html");

        JtwigModel model = JtwigModel.newModel();
        model.with("info", "");
        model.with("artifacts", artifacts);
        model.with("money", controller.getMoney(studentId));
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void buyArtifact(HttpExchange httpExchange) throws IOException{
        int studentId = this.sessionManager.getCurrentUserId(httpExchange);

        Map<String, String> inputs = responseManager.getInput(httpExchange);
        String artifactName = inputs.get("artifact");

        String info = controller.buyArtifact(studentId , artifactName);

        List<String> artifacts = controller.getArtifacts();
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/student/buy_artifact.html");

        JtwigModel model = JtwigModel.newModel();
        model.with("info", info);
        model.with("artifacts", artifacts);
        model.with("money", controller.getMoney(studentId));
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

}