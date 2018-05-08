package server.webcontrollers;

import com.sun.net.httpserver.HttpExchange;
import dao.*;
import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebMentorController implements IMentorController {

    private final IDaoWallet daoWallet;
    private final IDaoStudent daoStudent;
    private final IDaoArtifact daoArtifact;
    private final IDaoLevel daoLevel;
    private final IDaoTeam daoTeam;
    private final IDaoClass daoClass;
    private final IDaoQuest daoQuest;
    private final IDaoMentor daoMentor;

    public static IMentorController create(IDaoWallet daoWallet, IDaoStudent daoStudent,
                                            IDaoArtifact daoArtifact, IDaoLevel daoLevel,
                                            IDaoTeam daoTeam, IDaoClass daoClass,
                                            IDaoQuest daoQuest, IDaoMentor daoMentor) {
        return new WebMentorController(daoWallet, daoStudent, daoArtifact,
                daoLevel, daoTeam, daoClass, daoQuest, daoMentor);
    }

    private WebMentorController(
                             IDaoWallet daoWallet, IDaoStudent daoStudent,
                             IDaoArtifact daoArtifact, IDaoLevel daoLevel,
                             IDaoTeam daoTeam, IDaoClass daoClass,
                             IDaoQuest daoQuest, IDaoMentor daoMentor) {

        this.daoWallet = daoWallet;
        this.daoStudent = daoStudent;
        this.daoArtifact = daoArtifact;
        this.daoLevel = daoLevel;
        this.daoTeam = daoTeam;
        this.daoClass = daoClass;
        this.daoQuest = daoQuest;
        this.daoMentor = daoMentor;
    }

    @Override
    public String getMentorName(int mentorId) {
        Mentor mentor = getMentorById(mentorId);
        if(mentor == null) {
            return "";
        }
        return mentor.getName();
    }

    @Override
    public String getMentorEmail(int mentorId) {
        Mentor mentor = getMentorById(mentorId);
        if(mentor == null) {
            return "";
        }
        return mentor.getEmail();
    }

    @Override
    public String getMentorClassWithStudents(int mentorId) {
        CodecoolClass codecoolClass = daoClass.getMentorsClass(mentorId);
        if(codecoolClass == null) {
            return "no class assigned yet..";
        }
        String[] classData = codecoolClass.toString().split("ID:");
        int counter = 0;
        StringBuilder sb = new StringBuilder(codecoolClass.getBasicInfo());
        sb.append(", students:<br>");
        for(String element : classData) {
            if(counter > 0) {
                sb.append("<br>###   id: ");
                sb.append(element);
            }
            counter++;
        }
        return sb.toString();
    }

    @Override
    public String getClassNames() {
        List<String> classes = daoClass.getAllClassNames();
        String result = "";
        for(String className : classes) {
            result += className;
        }
        return result;
    }

    @Override
    public boolean createStudent(String name, String password, String email, int classId) {
        Student student = new Student(name, password, email);
        boolean isStudentCreated = daoStudent.exportStudent(student);
        int studentId = daoStudent.importNewStudent(email).getUserId();
        daoClass.assignStudentToClass(studentId, classId);
        return isStudentCreated;
    }

    public List<String> getQuests() {
        List<String> quests = new ArrayList<>();
        for(Quest quest : daoQuest.getAllQuests()) {
            quests.add(quest.getFullInfo());
        }
        return quests;
    }

    @Override
    public boolean editQuest(Map<String, String> questData) {
        Quest quest = daoQuest.importQuest(Integer.parseInt(questData.get("quest-id")));
        if(quest !=null) {
            if(questData.containsKey("questname")) {
                quest.setName(questData.get("questname"));
            }
            if(questData.containsKey("description")) {
                quest.setDescription(questData.get("description"));
            }
            if(questData.containsKey("value")) {
                quest.setValue(Integer.parseInt(questData.get("value")));
            }
            if(questData.containsKey("type")) {
                quest.setType(questData.get("type"));
            }
            if(questData.containsKey("category")) {
                quest.setCategory(questData.get("category"));
            }

            return daoQuest.updateQuest(quest);
        } else{
            return false;
        }
    }

    @Override
    public boolean createTeam(String teamName) {
        Team team = daoTeam.createTeam(teamName);
        return daoTeam.exportTeam(team);
    }

    private Mentor getMentorById(int mentorId) {
        return daoMentor.importMentor(mentorId);
    }

}





