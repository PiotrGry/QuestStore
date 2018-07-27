package system.dao;

import system.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DaoTeam extends SqlDao implements IDaoTeam {

    private final IDaoStudent daoStudent;
    private final String DATABASE_TABLE = "teams";
    private final String ID_LABEL = "id_team";

    DaoTeam(Connection connection, IDaoStudent daoStudent) {
        super(connection);
        this.daoStudent = daoStudent;
    }

    @Override
    public Team createTeam(String name) {

        try {
            int id = getLowestFreeIdFromGivenTable(DATABASE_TABLE, ID_LABEL);
            int coins = 0;
            Team team = new Team(id, name, new ArrayList<>(), coins);
            exportTeam(team);
            return team;
        } catch (SQLException e) {
            e.printStackTrace();
            return new NullTeam();
        }
    }

    @Override
    public Team importTeam(int teamId) {

        String query = "SELECT name, available_coins FROM teams WHERE id_team = ?;";

        try ( PreparedStatement preparedStatement = getConnection().prepareStatement(query) ) {

            preparedStatement.setInt(1, teamId);

            try( ResultSet resultSet = preparedStatement.executeQuery() ) {

                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    int availableCoins = resultSet.getInt("available_coins");
                    List<Student> students = getStudentsOfTeam(teamId);
                    return new Team(teamId, name, students, availableCoins);
                }
                return new NullTeam();
            }

        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Team not found");
            return new NullTeam();
        }
    }

    private boolean exportTeam(Team team) throws SQLException {

        String query = "INSERT INTO teams (name, available_coins) VALUES (?, ?);";

        try ( PreparedStatement preparedStatement = getConnection().prepareStatement(query) ) {

            preparedStatement.setString(1,  team.getName());
            preparedStatement.setInt(2, team.getAvailableCoins());
            preparedStatement.executeUpdate();
            return true;
        }
    }

    @Override
    public boolean updateTeamData(Team team) {

        String query = "UPDATE teams SET name = ?, available_coins = ? WHERE id_team = ?;";

        try ( PreparedStatement preparedStatement = getConnection().prepareStatement(query) ) {

            preparedStatement.setString(1, team.getName());
            preparedStatement.setInt(2, team.getAvailableCoins());
            preparedStatement.setInt(3, team.getGroupId());

            preparedStatement.executeUpdate();

            return true;

         } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(" insertion failed");
            return false;
        }
    }

    @Override
    public Team getTeamByStudentId(int studentId){

        String query = "SELECT id_team FROM students_in_teams WHERE id_student=?;";

        try ( PreparedStatement preparedStatement = getConnection().prepareStatement(query) ) {

            preparedStatement.setInt(1, studentId);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    Integer teamId = resultSet.getInt(ID_LABEL);
                    return importTeam(teamId);
                }
                return new NullTeam();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Selecting students team failed");
            return new NullTeam();
        }
    }

    @Override
    public List<Student> getStudentsOfTeam(int teamId) {
        List<Student> studentsOfTeam = new ArrayList<>();
        String query = "SELECT id_user FROM users INNER JOIN students_in_teams "
                     + "ON users.id_user = students_in_teams.id_student "
                     + "AND students_in_teams.id_team = ?";

        try ( PreparedStatement preparedStatement = getConnection().prepareStatement(query) ) {
            preparedStatement.setInt(1, teamId);

            try(ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int userId = resultSet.getInt("id_user");
                    Student student = daoStudent.importStudent(userId);
                    studentsOfTeam.add(student);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("No students");
        }
        return studentsOfTeam;
    }

    @Override
    public List<Team> getAllTeams() {
        List<Team> teams = new ArrayList<>();
        String query = "SELECT id_team FROM teams;";

        try (   PreparedStatement preparedStatement = getConnection().prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()){

            while (resultSet.next()){
                int teamId = resultSet.getInt("id_team");
                Team team = importTeam(teamId);
                teams.add(team);
            }

        } catch (SQLException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return teams;
    }

    @Override
    public boolean assignStudentToTeam(int studentId, int teamId) {
        try {
            List<Integer> existingTeamsIds = getAllTeamsIds();
            if(! existingTeamsIds.contains(teamId)) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String removeQuery = "DELETE FROM students_in_teams WHERE id_student=?";

        try ( PreparedStatement preparedStatement = getConnection().prepareStatement(removeQuery) ) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Assignment of student to team failed");
            return false;
        }

        String query = "INSERT INTO students_in_teams (id_team, id_student, id_student_in_team) " +
                "VALUES (?, ?, ?);";

        try ( PreparedStatement preparedStatement = getConnection().prepareStatement(query) ) {
            int idStudentInTeams = getLowestFreeIdFromGivenTable("students_in_teams",
                    "id_student_in_team");
            preparedStatement.setInt(1, teamId);
            preparedStatement.setInt(2, studentId);
            preparedStatement.setInt(3, idStudentInTeams);
            preparedStatement.executeUpdate();
            return true;
         } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Assignment of student to team failed");
            return false;
        }
    }

    private List<Integer> getAllTeamsIds() throws SQLException {
        String query = String.format("SELECT %s FROM %s ", ID_LABEL, DATABASE_TABLE);
        List<Integer> teamsIds = new ArrayList<>();
        try ( PreparedStatement preparedStatement = getConnection().prepareStatement(query) ) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                teamsIds.add(resultSet.getInt(ID_LABEL));
            }
        }
        return teamsIds;
    }
}
