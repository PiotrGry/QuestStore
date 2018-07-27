package server.webcontrollers;

import system.dao.*;
import system.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class WebStudentController implements IStudentController {

    private final IDaoWallet daoWallet;
    private final IDaoStudent daoStudent;
    private final IDaoArtifact daoArtifact;
    private final IDaoLevel daoLevel;
    private final IDaoTeam daoTeam;
    private final IDaoClass daoClass;

    public static IStudentController create(IDaoWallet daoWallet, IDaoStudent daoStudent,
                                            IDaoArtifact daoArtifact, IDaoLevel daoLevel,
                                            IDaoTeam daoTeam, IDaoClass daoClass) {
        return new WebStudentController(daoWallet, daoStudent, daoArtifact,
                daoLevel, daoTeam, daoClass);
    }

    private WebStudentController(
                             IDaoWallet daoWallet, IDaoStudent daoStudent,
                             IDaoArtifact daoArtifact, IDaoLevel daoLevel,
                             IDaoTeam daoTeam, IDaoClass daoClass) {

        this.daoWallet = daoWallet;
        this.daoStudent = daoStudent;
        this.daoArtifact = daoArtifact;
        this.daoLevel = daoLevel;
        this.daoTeam = daoTeam;
        this.daoClass = daoClass;
    }

    @Override
    public String getStudentName(int studentId) {
        Student student = getStudentById(studentId);
        if(student.getUserId() == 0) {
            return "";
        }
        return student.getName();
    }

    @Override
    public String getStudentEmail(int studentId) {
        Student student = getStudentById(studentId);
        if(student.getUserId() == 0) {
            return "";
        }
        return student.getEmail();
    }

    @Override
    public String getStudentWallet(int studentId) {
        Student student = getStudentById(studentId);
        if(student.getUserId() == 0) {
            return "";
        }
        return student.getWallet().toString();
    }

    @Override
    public String getStudentGroup(int studentId) {
        Team team = daoTeam.getTeamByStudentId(studentId);
        if(team.getGroupId() == 0) {
            return "";
        }
        return team.getName();
    }

    @Override
    public String getStudentExpLevel(int studentId) {
        Student student = getStudentById(studentId);

        int coins = student.getWallet().getAllCoins();
        String level = daoLevel.importLevelByCoins(coins).getName();
        return String.format("level: %s / collected coins: %s", level, coins);
    }

    @Override
    public String getStudentClass(int studentId) {
        CodecoolClass codecoolClass = daoClass.getStudentClass(studentId);
        if(codecoolClass.getGroupId() == 0) {
            return "";
        }
        return codecoolClass.getName();
    }

    private Student getStudentById(int studentId) {
        return daoStudent.importStudent(studentId);
    }

    @Override
    public List<String> getTeamMembers(int studentId) {
        Team team = daoTeam.getTeamByStudentId(studentId);
        List<Student> members = team.getStudents();
        List<String> membersNames = new ArrayList<>();
        for (Student student : members){
            membersNames.add(student.getName());
        }
        return membersNames;
    }

    @Override
    public List<String> getArtifacts() {
        return daoArtifact.getAllArtifacts().stream().sorted(Comparator.comparing(Artifact::getItemId))
                .map(t -> String.format("#%s %s %dcc", t.getItemId(), t.getName(), t.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public String buyArtifact(int studentId , String artifactName) {
        int artifactId = gatherIdFromStringData(artifactName);
        Artifact artifact = daoArtifact.importArtifact(artifactId);
        Student student = daoStudent.importStudent(studentId);
        int price = artifact.getValue();
        if(student.hasEnoughCoins(price)){
            student.subtractCoins(price);
            student.addNewArtifact(artifact);
            daoWallet.updateWallet(student);
            daoWallet.exportStudentArtifact(artifact.getItemId(), studentId);
            return "Artifact bought!";
        } else {
            return "Not this time.. :(";
        }
    }

    @Override
    public String[] getStudentData(int studentId) {
        String[] studentData = new String[]{"",""};
        if(studentId == 0) {
            return studentData;
        }
        int BASIC_DATA_INDEX = 0;
        int DETAILS_INDEX = 1;
        Student student = daoStudent.importStudent(studentId);

        studentData[BASIC_DATA_INDEX] = student.getName();

        studentData[DETAILS_INDEX] = String.format("id: %s<br>email: %s<br>" +
                        "level: %s<br>wealth: %s<br>" +
                        "class: %s<br>team: %s<br>"  +
                        "teammates:<br>%s",
                studentId,
                student.getEmail(),
                getStudentExpLevel(studentId),
                getMoney(studentId),
                getStudentClass(studentId),
                getStudentGroup(studentId),
                daoTeam.getStudentsOfTeam(daoTeam.getTeamByStudentId(studentId).getGroupId())
                        .stream().map(Student::toString)
                        .collect(Collectors.joining("<br>")));
        return studentData;
    }

    @Override
    public String getMoney(int studentId) {
        Student student = getStudentById(studentId);
        return Integer.toString(student.getWallet().getAvailableCoins());
    }

    private int gatherIdFromStringData(String data) {
        try {
            int idIndex = 0;
            return Integer.parseInt(data.replace("#", "").split(" ")[idIndex]);
        } catch (NumberFormatException | IndexOutOfBoundsException ex){
            ex.printStackTrace();
            return 0;
        }
    }
}





