package server.webcontrollers;

import system.model.Student;

import java.util.List;
import java.util.Map;

public interface IMentorController extends IServerController {

    String getMentorName(int mentorId);
    String getMentorEmail(int mentorId);
    String getMentorClassWithStudents(int mentorId);
    List<String> getAllTeamsCollection();
    List<String> getAllClassCollection();
    boolean createStudent(String name, String password, String email, String codeCoolClass);
    boolean createTeam(String teamName);
    List<String> getQuests();
    boolean editQuest(Map<String, String> inputs);
    boolean addQuest(String name, int value, String description, String type, String category);
    boolean addArtifact(String name, int value, String type, String category);
    Map<String, String> getAllWallets();
    List<String> getArtifacts();
    boolean editArtifact(Map<String, String> inputs);
    Map<Integer, String> getStudentsWithIds();
    List<String> getStudentsByMentorId(int mentorId);
    boolean assignStudentToTeam(String studentData, String teamData);
    Map<String, List<String>> getStudentArtifacts(int studentId);
    String getStudentWallet(int studentId);
    int getStudentIdFromTextData(String studentData);
    String[] getMentorData(int mentorId);

}
