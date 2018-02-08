package controller;

import dao.DaoArtifact;
import model.*;
import view.ViewTeam;

import java.util.ArrayList;
import java.util.Random;

public class ControllerTeam implements IUserController {

    private ViewTeam viewTeam;
    private Team team;

    public ControllerTeam(Team team) {
        this.viewTeam = new ViewTeam();
        this.team = team;
    }

    public Artifact getArtifact(String type) {
        DaoArtifact daoArtifact = new DaoArtifact();

        viewTeam.displayText("Available artifacts:\n");
        viewTeam.displayList(daoArtifact.getArtifacts(type));

        int artifactId = viewTeam.getIntInputFromUser("\nEnter id of artifact: ");
        Artifact artifact = daoArtifact.importArtifact(artifactId);

        return artifact;
    }

    public void buyArtifact() {

    }

    private boolean checkTeamCoinsDivisibleByTeamSize(int teamCoins, int teamSize) {
        return (teamCoins % teamSize) == 0;
    }

    private Student popRandomStudent(ArrayList<Student> students) {
        int randomIndex = new Random().nextInt(students.size());
        Student randomStudent = students.get(randomIndex);
        students.remove(randomStudent);
        return randomStudent;
    }

    private void splitMoneyEqually() {
        ArrayList<Student> students = team.getStudents();
        int teamCoins = team.getAvailableCoins();
        int teamSize = team.getSize();

        int coinsForOneStudent = teamCoins / teamSize;

        for (Student student: students) {
            student.addCoins(coinsForOneStudent);
        }
    }

    private void splitMoneyAlmostEqually() {
//        ArrayList<Student> students = team.getStudents().clone();
        ArrayList<Student> students = new ArrayList<>();

        int teamCoins = team.getAvailableCoins();
        int teamSize = team.getSize();

        splitMoneyEqually();
        int remainderCoins = (teamCoins % teamSize);

        while (remainderCoins > 0) {
            Student luckyStudent = popRandomStudent(students);
            luckyStudent.addCoins(1);
            remainderCoins--;
        }
    }

    public void splitTeamMoney() {
        int teamCoins = team.getAvailableCoins();
        int teamSize = team.getSize();

        if (teamCoins == 0) {
            viewTeam.displayText("Team has no coins to split");
            return;
        }

        if (checkTeamCoinsDivisibleByTeamSize(teamCoins, teamSize)) {
            splitMoneyEqually();
        } else {
            splitMoneyAlmostEqually();
        }
    }

    public void runMenu() {

        String teamOption = "";
        while (!teamOption.equals("0")) {

            viewTeam.displayText(this.team.getBasicTeamInfo());
            viewTeam.displayText("\nWhat would like to do?");
            viewTeam.displayList(viewTeam.getStudentOptions());

            teamOption = viewTeam.getInputFromUser("Option: ");
            switch (teamOption) {
                case "1":
                    buyArtifact();
                    break;
                case "2":
                    splitTeamMoney();
                    break;
                case "0":
                    break;
                default: viewTeam.displayText("Wrong option. Try again!");
                    break;
            }
        }
    }
}
