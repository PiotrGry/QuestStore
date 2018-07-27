package server.webcontrollers;

import java.util.List;

public interface IStudentController extends IServerController {

    String getStudentName(int studentId);
    String getStudentEmail(int studentId);
    String getStudentWallet(int studentId);
    String getStudentGroup(int studentId);
    String getStudentExpLevel(int studentId);
    String getStudentClass(int studentId);
    List<String> getTeamMembers(int studentId);
    List<String> getArtifacts();
    String buyArtifact(int studentId, String artifactName);
    String getMoney(int studentId);
    String[] getStudentData(int studentId);
}
