package controller;

import view.ViewMentor;
import dao.*;
import iterator.MyIterator;
import model.*;

import java.util.ArrayList;

public class ControllerMentor implements IUserController{
    private ViewMentor viewMentor;
    private Mentor mentor;

    public ControllerMentor(Mentor mentor){
        this.viewMentor = new ViewMentor();
        this.mentor = mentor;
    }

    public void createStudent() {
        
        DaoStudent daoStudent = new DaoStudent();
        String studentName = viewMentor.getInputFromUser("Enter name of new student: ");
        String studentPassword = viewMentor.getInputFromUser("Enter password of new student: ");
        String studentEmail = viewMentor.getInputFromUser( "Enter email of new student: ");

        Student student = daoStudent.createInstance(studentName, studentPassword, studentEmail);
        daoStudent.exportInstance(student);

    }

    public void createTeam() {
        DaoTeam daoTeam = new DaoTeam();

        String nameRequest = "Enter name of new team: ";
        String teamName = viewMentor.getInputFromUser(nameRequest);

        Team team = daoTeam.createTeam(teamName);
        daoTeam.exportTeam(team);
    }

    public Student getStudent(){
        DaoStudent daoStudent = new DaoStudent();

        viewMentor.displayText("Students list: ");
        viewMentor.displayList(daoStudent.getAllStudents());
        ArrayList<Student> students = daoStudent.getAllStudents();

        int studentId = viewMentor.getIntInputFromUser("\nEnter id of student: ");

        return daoStudent.importInstance(studentId);
    }

    public Team getTeam(){
        DaoTeam daoTeam = new DaoTeam();

        ArrayList<Team> teams = daoTeam.getAllTeams();
        for(Team team: teams){
            viewMentor.displayText(team.getBasicInfo());
        }

        int teamId = viewMentor.getIntInputFromUser("\nEnter id of team: ");

        return daoTeam.importTeam(teamId);
    }

    public void addQuest(){
//        DaoQuest daoQuest = new DaoQuest();
//
//        String nameRequest = "Enter name of new quest: ";
//        String questName = viewMentor.getInputFromUser(nameRequest);
//
//        String valueRequest = "Enter value of new quest: ";
//        int questValue = viewMentor.getIntInputFromUser(valueRequest);
//
//        String descriptionRequest = "Enter description of quest";
//        String questDescription = viewMentor.getInputFromUser(descriptionRequest);
//
//        String questStatus = chooseStatus();
//        String questType = chooseType();
//
//        daoQuest.createQuest(questName, questValue, questDescription, questStatus, questType);
    }

    public void addArtifact() {
//        DaoArtifact daoArtifact = new DaoArtifact();
//
//        String nameRequest = "Enter name of new artifact: ";
//        String artifactName = viewMentor.getInputFromUser(nameRequest);
//
//        String valueRequest = "Enter value of new artifact: ";
//        int artifactValue = viewMentor.getIntInputFromUser(valueRequest);
//
//        String descriptionRequest = "Enter description of new artifact";
//        String artifactDescription = viewMentor.getInputFromUser(descriptionRequest);
//
//        String artifactStatus = chooseStatus();
//        daoArtifact.createArtifact(artifactName, artifactValue, artifactDescription, artifactStatus);
    }

    private String chooseStatus() {
        String statusRequest = "Choose status:\n1. Individual\n2. Team\nOption: ";
        String status = null;
        boolean choosingStatus = true;
        int option = 0;
        while(choosingStatus) {
            option = viewMentor.getIntInputFromUser(statusRequest);
            switch(option) {
                case 1:
                    status = "individual";
                    choosingStatus = false;
                    break;
                case 2:
                    status = "team";
                    choosingStatus = false;
                    break;
                default:
                    viewMentor.displayText("Wrong option number!");
            }
        }
        return status;
    }

    private String chooseType() {
        String typeRequest = "Choose type:\n1. Basic\n2. Extra\nOption: ";
        String type = null;
        boolean choosingType = true;
        int option = 0;
        while(choosingType) {
            option = viewMentor.getIntInputFromUser(typeRequest);
            switch(option) {
                case 1:
                    type = "basic";
                    choosingType = false;
                    break;
                case 2:
                    type = "extra";
                    choosingType = false;
                    break;
                default:
                    viewMentor.displayText("Wrong option number!");
            }
        }
        return type;
    }

    public void updateQuest(){
        toBeImplemented();
    }

    public void updateArtifact() {
        //seeArtifacts();
        toBeImplemented();
        
    }

    public void toBeImplemented(){
        String text = "Implementation in progress";
        viewMentor.displayText(text);
    }

    public void markQuest() {
        toBeImplemented();
    }

    public void markArtifact() {
        toBeImplemented();
    }

    public void markStudentAchivedQuest() {
        toBeImplemented();
    }

    public void markTeamAchivedQuest() {
        toBeImplemented();
    }

    public void markStudentBoughtArtifact() {
        toBeImplemented();
    }

    public void markTeamBoughtArtifact() {
        toBeImplemented();
    }

    public void seeAllWallets() {
//        int idOfMentorClass = mentor.getClassId();
//        viewMentor.displayText("Wallets of students of class: ");
//
//        DaoStudent daoStudent = new DaoStudent();
//        ArrayList<Student> allStudentsOfClass = daoStudent.getStudentsByClassId(idOfMentorClass);
//        MyIterator <Student> myIterator = new MyIterator<>(allStudentsOfClass);
//        while(myIterator.hasNext()){
//            Student student = myIterator.next();
//            viewMentor.displayText(student.toString() + "/n" + student.getWallet().toString());
//        }
    }
    private ArrayList<Wallet> getWalletsOfStudents(ArrayList<Student> students) {
        ArrayList<Wallet> walletsOfStudents = new ArrayList<>();
        for (Student student : students) {
            walletsOfStudents.add(student.getWallet());
        }
        return walletsOfStudents;
    }

    public void seeQuests() {
//        viewMentor.displayText("Available quests: ");
//        viewMentor.displayList(new DaoArtifact().importData());
    }

    public void seeArtifacts() {
//        viewMentor.displayText("Available artifacts: ");
//        viewMentor.displayList(new DaoArtifact().importData());
    }

    public void assignStudentsToTeam(){
        Team team = getTeam();

        boolean toContinue = true;
        do{
            viewMentor.displayList(viewMentor.getAssignStudentToTeamOptions());
            String chosenOption = viewMentor.getInputFromUser("Choose option: ");
            switch(chosenOption){
                case "1": assignStudentToTeam(team);
                    viewMentor.displayText("Student added to " + team.getName() + " team!");
                    break;
                case "0": toContinue = false;
                    break;
                default: viewMentor.displayText("Wrong option. Try again!");
                    break;
            }
        }while(toContinue);
    }

    public void assignStudentToTeam(Team team){
        Student student = getStudent();
        new DaoTeam().assignStudentToTeam(student.getUserId(), team.getGroupId());
    }

    public void runMenu() {
        String mentorOption = "";
        while (!mentorOption.equals("0")) {

            viewMentor.displayText("\nWhat would like to do?");
            viewMentor.displayList(viewMentor.getMentorOptions());

    mentorOption = viewMentor.getInputFromUser("Option: ");
    switch (mentorOption) {
        case "1": createStudent();
                break;
        case "2": createTeam();
                break;
        case "3": addQuest();
                break;
        case "4": addArtifact();
                break;
        case "5": updateQuest();
                break;
        case "6": updateArtifact();
                break;
        case "7": markQuest();
                break;
        case "8": markArtifact();
                break;
        case "9": seeAllWallets();
                break;
        case "10": assignStudentsToTeam();
                break;
        case "0": break;

        default: viewMentor.displayText("Wrong option. Try again!");
                 break;
    }
        }

    }

}
