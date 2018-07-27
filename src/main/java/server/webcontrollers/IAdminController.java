package server.webcontrollers;

import java.util.List;
import java.util.Map;

public interface IAdminController extends IServerController {

    String getAdminName(int adminId);
    boolean createMentor(String name, String password, String email);
    boolean createClass(String name);
    boolean editMentor(Map mentorData);
    String getCodecoolClass(String name);
    String seeMentorData(String mentor);
    boolean createLevel(String name, int coinsLimit);
    List<String> getMentorsNames();
    List<String> getMentorsFullData();
    List<Integer> getMentorsId();
    String getAdminEmail(int adminId);
    List<String> getAllLevels();
    String getAllClasses();
    List<String> getAllClassesCollection();
    List<String> getAllMentorsCollection();
    boolean assignMentorToClass(String mentorData, String classData);
    String[] getAdminData(int adminId);
}
