package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import server.helpers.IResponseManager;
import server.sessions.ISessionManager;
import server.webcontrollers.IMentorController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MentorHandler implements HttpHandler {

    private final IMentorController controller;
    private final ISessionManager sessionManager;
    private final IResponseManager responseManager;


    public static HttpHandler create(ISessionManager sessionManager,
                                     IMentorController controller,
                                     IResponseManager responseManager) {
        return new MentorHandler(sessionManager, controller, responseManager);
    }

    private MentorHandler(ISessionManager sessionManager,
                          IMentorController controller,
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
                    case "/mentor":
                        displayMentorHomePage(httpExchange);
                        break;
                    case "/mentor/create_student":
                        displayCreateStudentPage(httpExchange);
                        break;
                    case "/mentor/create_team":
                        displayCreateTeamPage(httpExchange);
                        break;
                    case "/mentor/assign_student_to_team":
                        displayAssignStudentToTeam(httpExchange);
                        break;
                    case "/mentor/edit_quest":
                        displayQuestToUpdate(httpExchange);
                    case "/mentor/add_quest":
                        displayAddQuestPage(httpExchange);
                        break;
                    case "/mentor/add_artifact":
                        displayAddArtifactPage(httpExchange);
                        break;
                    case "/mentor/see_all_wallets":
                        displayStudentWallet(httpExchange);
                        break;
                    case "/mentor/edit_artifact":
                        displayEditArtifactPage(httpExchange);
                        break;
                    case "/mentor/display_student_to_pick":
                        showStudentToPick(httpExchange);
                }
            }
            if (method.equals("POST")) {
                String uri = httpExchange.getRequestURI().toString();
                System.out.println("URI: " + uri);

                switch(uri) {
                    case "/mentor/create_student":
                        createStudent(httpExchange);
                        break;
                    case "/mentor/create_team":
                        createTeam(httpExchange);
                        break;
                    case "/mentor/assign_student_to_team":
                        handleAssignStudentToTeam(httpExchange);
                        break;
                    case "/mentor/edit_quest":
                        updateQuest(httpExchange);
                    case "/mentor/add_quest":
                        addQuest(httpExchange);
                        break;
                    case "/mentor/add_artifact":
                        addArtifact(httpExchange);
                        break;
                    case "/mentor/edit_artifact":
                        editArtifact(httpExchange);
                        break;
                    case "/mentor/display_student_to_pick":
                        showStudentArtifact(httpExchange);
                    case "/mentor/show_student_artifacts":
                        showStudentArtifact(httpExchange);
                    case "/mentor/see_all_wallets":
                        handleStudentWalletInfo(httpExchange);
                        break;
                }
            }
        }
    }

    private void showStudentArtifact(HttpExchange httpExchange) throws IOException {
        Map<String,String> input = responseManager.getInput(httpExchange);
        System.out.println(input.get("studentId"));
        int studentId = Integer.parseInt(input.get("studentId"));
        Map<String, List<String>> studentArtifacts = controller.getStudentArtifacts(studentId);
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/mentor/show_student_artifacts.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("newStudentArtifacts",studentArtifacts.get("newArtifacts"));
        model.with("usedStudentArtifacts",studentArtifacts.get("usedArtifacts"));
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void showStudentToPick(HttpExchange httpExchange) throws IOException {
        Map<Integer, String> students = controller.getStudentsWithIds();
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/mentor/display_student_to_pick.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("students", students);
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);

    }

    private void editArtifact(HttpExchange httpExchange) throws IOException {
        Map<String, String> inputs = responseManager.parseEditData(httpExchange);
        String info;
        boolean isArtifactEdited = controller.editArtifact(inputs);
        if(isArtifactEdited){
            info = "Quest edited successfully!";
        }else{
            info = "Something went wrong :(";
        }
        List<String> artifacts = controller.getArtifacts();
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/mentor/edit_artifact.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("artifacts", artifacts);
        model.with("info", info);
        String response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void displayEditArtifactPage(HttpExchange httpExchange) throws IOException {
        List<String> artifacts = controller.getArtifacts();
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/mentor/edit_artifact.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("artifacts", artifacts);
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);

    }

    private void updateQuest(HttpExchange httpExchange) throws IOException {

        Map<String, String> inputs = responseManager.parseEditData(httpExchange);
        String info;
        boolean isQuestEdited = controller.editQuest(inputs);
        if(isQuestEdited){
            info = "Quest edited successfully!";
        }else{
            info = "Something went wrong :(";
        }
        List<String> quests = controller.getQuests();
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/mentor/edit_quest.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("quests", quests);
        model.with("info", info);
        String response = template.render(model);
        responseManager.executeResponse(httpExchange, response);

    }

    private void displayQuestToUpdate(HttpExchange httpExchange) throws IOException {
        List<String> quests = controller.getQuests();
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/mentor/edit_quest.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("quests", quests);
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);

    }

    private void createTeam(HttpExchange httpExchange) throws IOException {
        Map<String, String> inputs = responseManager.getInput(httpExchange);
        String teamName = inputs.get("team");
        String info;
        if(controller.createTeam(teamName)){
            info = "Team added successfully!";
        }else{
            info = "Something went wrong - perhaps You've used already existing name?";
        }
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/mentor/create_team.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("teams", controller.getAllTeamsCollection());
        model.with("info", info);
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);

    }

    private void createStudent(HttpExchange httpExchange) throws IOException {
        Map<String, String> inputs = responseManager.getInput(httpExchange);
        String name = inputs.get("name");
        String password = inputs.get("password");
        String email = inputs.get("email");
        String codeCoolClass = inputs.get("class");
        String info;
        if(controller.createStudent(name, password, email, codeCoolClass)){
            info = "Student added successfully!";
        }else{
            info = "Operation failed!";
        }
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/mentor/create_student.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("classes", controller.getAllClassCollection());
        model.with("result", info);
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void displayCreateStudentPage(HttpExchange httpExchange) throws IOException {
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/mentor/create_student.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("classes", controller.getAllClassCollection());
        model.with("result", "");
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void displayCreateTeamPage(HttpExchange httpExchange) throws IOException {
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/mentor/create_team.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("teams", controller.getAllTeamsCollection());
        model.with("info", "");
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void displayMentorHomePage(HttpExchange httpExchange) throws IOException {
        String response;
        int MAIN_INFO_INDEX = 0;
        int DETAILS_INDEX = 1;
        String[] adminData = controller.getMentorData(sessionManager
                .getCurrentUserId(httpExchange));
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/mentor/profile.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("mainInfo", adminData[MAIN_INFO_INDEX]);
        model.with("details", adminData[DETAILS_INDEX]);
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void displayAddQuestPage(HttpExchange httpExchange) throws IOException {
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/mentor/add_quest.html");
        JtwigModel model = JtwigModel.newModel();
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void displayAddArtifactPage(HttpExchange httpExchange) throws IOException {
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/mentor/add_artifact.html");
        JtwigModel model = JtwigModel.newModel();
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void addQuest(HttpExchange httpExchange) throws IOException{
        Map<String, String> inputs = responseManager.getInput(httpExchange);

        String name = inputs.get("questname");
        int value = Integer.parseInt(inputs.get("value"));
        String description = inputs.get("description");
        String type = inputs.get("type");
        String category = inputs.get("category");

        String info = "Error!";
        if(controller.addQuest(name, value, description, type, category)){
            info = "Done!";
        }
        String response;
        JtwigTemplate template = JtwigTemplate.classpathTemplate(
                "static/mentor/add_quest.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("info", info);
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void addArtifact(HttpExchange httpExchange) throws IOException {
        Map<String, String> inputs = responseManager.getInput(httpExchange);

        String name = inputs.get("artifactname");
        int value = Integer.parseInt(inputs.get("value"));
        String description = inputs.get("description");
        String type = inputs.get("type");

        String info = "Error!";
        if(controller.addArtifact(name, value, description, type)){
            info = "Done!";
        }
        String response;
        JtwigTemplate template = JtwigTemplate.classpathTemplate(
                "static/mentor/add_artifact.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("info", info);
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void displayStudentWallet(HttpExchange httpExchange) throws IOException {
        String response;
        JtwigTemplate template = JtwigTemplate.classpathTemplate(
                "static/mentor/see_all_wallets.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("studentName", "");
        model.with("wallet", "");
        model.with("students", controller.getStudentsByMentorId(sessionManager.getCurrentUserId(httpExchange)));
        model.with("result", "");
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void handleStudentWalletInfo(HttpExchange httpExchange) throws IOException {
        String response;
        Map<String, String> inputs = responseManager.getInput(httpExchange);
        String studentData = inputs.get("student");
        int studentId = controller.getStudentIdFromTextData(studentData);
        JtwigTemplate template = JtwigTemplate.classpathTemplate(
                "static/mentor/see_all_wallets.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("studentName", String.format("(%s)", studentData));
        model.with("wallet", controller.getStudentWallet(studentId));
        model.with("students", controller.getStudentsByMentorId(sessionManager.getCurrentUserId(httpExchange)));
        model.with("result", "");
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void displayAssignStudentToTeam(HttpExchange httpExchange ) throws IOException {
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/mentor/assign_student_to_team.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("students", controller.getStudentsByMentorId(sessionManager.getCurrentUserId(httpExchange)));
        model.with("teams",  controller.getAllTeamsCollection());
        model.with("result", "");
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }

    private void handleAssignStudentToTeam(HttpExchange httpExchange) throws IOException {
        Map<String, String> inputs = responseManager.getInput(httpExchange);
        String studentData = inputs.get("student");
        String teamData = inputs.get("team");
        String response;
        JtwigTemplate template =
                JtwigTemplate.classpathTemplate(
                        "static/mentor/assign_student_to_team.html");
        JtwigModel model = JtwigModel.newModel();
        model.with("students", controller.getStudentsByMentorId(sessionManager.getCurrentUserId(httpExchange)));
        model.with("teams", controller.getAllTeamsCollection());
        boolean isSuccess= controller.assignStudentToTeam(studentData, teamData);
        String result;
        if(isSuccess) {
            result = studentData + " assigned to team " + teamData + "!";
        } else {
            result = "operation failed! student: " + studentData;
        }
        model.with("result", result);
        response = template.render(model);
        responseManager.executeResponse(httpExchange, response);
    }
}