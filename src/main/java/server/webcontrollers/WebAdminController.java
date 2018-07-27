package server.webcontrollers;

import system.dao.*;
import system.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WebAdminController implements IAdminController {

    private IDaoAdmin daoAdmin;
    private IDaoMentor daoMentor;
    private IDaoClass daoClass;
    private IDaoLevel daoLevel;

    public static IAdminController create(IDaoAdmin daoAdmin, IDaoMentor daoMentor,
                                          IDaoClass daoClass, IDaoLevel daoLevel) {
        return new WebAdminController(daoAdmin, daoMentor, daoClass, daoLevel);
    }

    private WebAdminController(IDaoAdmin daoAdmin, IDaoMentor daoMentor,
                               IDaoClass daoClass, IDaoLevel daoLevel) {
        this.daoAdmin = daoAdmin;
        this.daoMentor = daoMentor;
        this.daoClass = daoClass;
        this.daoLevel = daoLevel;
    }


    @Override
    public String getAdminName(int adminId) {
        Admin admin = daoAdmin.importAdmin(adminId);
        if(admin.getUserId() != 0) {
            return admin.getName();
        }
        return "";
    }

    @Override
    public String getAdminEmail(int adminId) {
        Admin admin = daoAdmin.importAdmin(adminId);
        if(admin.getUserId() == 0) {
            return "";
        }
        return admin.getEmail();
    }

    @Override
    public List<String> getAllLevels() {
        List<String> levels = daoLevel.getAllLevels().stream().map(Level::toString).collect(Collectors.toList());
        if(levels == null) {
            return new ArrayList<>();
        }
        return levels;
    }

    public boolean createMentor(String name, String password, String email) {
        return daoMentor.createMentor(name, password, email).getUserId() != 0;
    }


    public boolean createClass(String name) {
        return daoClass.createClass(name).getGroupId() != 0;
    }


    public boolean editMentor(Map mentorData) {
        Mentor mentor = daoMentor.importMentor(Integer.parseInt(mentorData.get("Id").toString()));
        if(mentor.getUserId() != 0) {
            if(mentorData.containsKey("Name")) {
                mentor.setName(mentorData.get("Name").toString());
            }
            if(mentorData.containsKey("Password")) {
                mentor.setPassword(mentorData.get("Password").toString());
            }
            if(mentorData.containsKey("Email")) {
                mentor.setEmail(mentorData.get("Email").toString());
            }
            return daoMentor.updateMentor(mentor);
        } else{
            return false;
        }
    }

    public String getCodecoolClass(String name) {
        for (CodecoolClass codecoolClass : daoClass.getAllClasses()) {
            if (codecoolClass.getName().equals(name)) {
                return codecoolClass.getName();
            }
        }
        return "";
    }


    public String seeMentorData(String name) {
        for (Mentor mentor : daoMentor.getAllMentors()) {
            if(mentor.getName().equals(name)) {
                return mentor.toString();
            }
        }
        return "";
    }


    public boolean createLevel(String name, int coinsLimit) {
        return daoLevel.createLevel(name, coinsLimit).getLevelId() != 0;
    }

    @Override
    public List<String> getMentorsNames() {
        return daoMentor.getAllMentors().stream().map(User::getName).collect(Collectors.toList());
    }

    @Override
    public List<Integer> getMentorsId() {
        return daoMentor.getAllMentors().stream().
                                        map(User::getUserId).
                                        collect(Collectors.toList());
    }

    @Override
    public List<String> getMentorsFullData() {
        return daoMentor.getAllMentors().stream().map(User::toString).collect(Collectors.toList());
    }

    @Override
    public String getAllClasses() {

        List<CodecoolClass> classes = daoClass.getAllClasses();
        StringBuilder classesAsText = new StringBuilder();
        int counter = 1;
        int maxNumClassesInLine = 7;
        for(CodecoolClass codecoolClass : classes) {
            int classId = codecoolClass.getGroupId();
            if(classId != 0) {
                if(counter % maxNumClassesInLine == 1) {
                    classesAsText.append("<br>");
                }
                String classAsText = String.format(" '%s', ", codecoolClass.getName());
                classesAsText.append(classAsText);
                counter++;
            }
        }
        return classesAsText.toString();
    }

    @Override
    public List<String> getAllClassesCollection() {
        return daoClass.getAllClasses().stream()
                .map(c -> String.format("#%s %s", c.getGroupId(), c.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllMentorsCollection() {
        return daoMentor.getAllMentors().stream()
                .map(m -> String.format("#%s %s", m.getUserId(), m.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean assignMentorToClass(String mentorData, String classData) {
        try {
            int mentorId = gatherIdFromStringData(mentorData);
            int classId = gatherIdFromStringData(classData);
            return daoClass.assignMentorToClass(mentorId, classId);

        } catch (NumberFormatException | IndexOutOfBoundsException ex){
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public String[] getAdminData(int adminId) {
        String[] adminData = new String[]{"",""};
        if(adminId == 0) {
            return adminData;
        }
        int BASIC_DATA_INDEX = 0;
        int DETAILS_INDEX = 1;
        Admin admin = daoAdmin.importAdmin(adminId);

        adminData[BASIC_DATA_INDEX] = admin.getName();

        adminData[DETAILS_INDEX] = String.format("id:%s<br>email:%s<br>mentors:<br>%s", adminId,
                admin.getEmail(),
                daoMentor.getAllMentors()
                .stream().map(Mentor::toString)
                .collect(Collectors.joining("<br>")));
        return adminData;
    }

    private int gatherIdFromStringData(String data) throws NumberFormatException, IndexOutOfBoundsException {
            int idIndex = 0;
            return Integer.parseInt(data.replace("#", "").split(" ")[idIndex]);
    }
}
