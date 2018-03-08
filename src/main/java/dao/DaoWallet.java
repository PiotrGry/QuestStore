package dao;

import model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DaoWallet implements IDaoWallet {

    @Override
    public Wallet createWallet(){
        return new Wallet();
    }

    @Override
    public Wallet createWallet(int allCoins, int availableCoins, List<Artifact> newArtifacts,
                               List<Artifact> usedArtifacts){
        return new Wallet(allCoins, availableCoins, newArtifacts, usedArtifacts);
    }

    @Override
    public Wallet importWallet(int userID) {
        Wallet wallet = null;
        PreparedStatement preparedStatement = null;
        List<Artifact> artifacts = null;
        String query = "SELECT * FROM wallets WHERE id_student= ?";
        try {
            preparedStatement = DbConnection.getConnection().prepareStatement(query);
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.isClosed()) {
                int allCoins = resultSet.getInt("all_coins");
                int availableCoins = resultSet.getInt("available_coins");
                List <Artifact> newArtifacts = getUserArtifacts(userID, "new");
                List <Artifact> usedArtifacts = getUserArtifacts(userID, "used");
                wallet = new Wallet(allCoins, availableCoins, newArtifacts, usedArtifacts);
                resultSet.close();
            }
            preparedStatement.close();

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return wallet;
    }

    @Override
    public void exportWallet(Student student){
        if(student == null){
            return;
        }
        int value = student.getUserId();
        int allCoins = student.getWallet().getAllCoins();
        int availableCoins = student.getWallet().getAvailableCoins();

        PreparedStatement preparedStatement = null;
        String query = "INSERT INTO wallets (id_student, all_coins, available_coins)" +
                "VALUES (?, ?, ?);";

        try{
            preparedStatement = DbConnection.getConnection().prepareStatement(query);
            preparedStatement.setInt(1, value);
            preparedStatement.setInt(2, allCoins);
            preparedStatement.setInt(3, availableCoins);

            preparedStatement.executeUpdate();
            preparedStatement.close();

        }catch (SQLException | ClassNotFoundException e){
            System.out.println("Wallet insertion failed");
        }

    }

    private List<Artifact> getUserArtifacts(int userID, String status) {

        List<Artifact> artifacts = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        String query = "SELECT artifacts.id_artifact FROM artifacts inner join artifacts_in_wallets "
                       + "on artifacts.id_artifact = artifacts_in_wallets.id_artifact "
                       + "WHERE artifacts_in_wallets.id_student = ? and artifacts_in_wallets.status = ?;";

        try {
            preparedStatement = DbConnection.getConnection().prepareStatement(query);
            preparedStatement.setInt(1, userID);
            preparedStatement.setString(2, status);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                int idArtifact = resultSet.getInt("id_artifact");
                Artifact artifact = new DaoArtifact().importArtifact(idArtifact);
                artifacts.add(artifact);
            }
            
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }

        return artifacts;
    }

    @Override
    public void updateWallet(Student student){

        int allCoins = student.getWallet().getAllCoins();
        int availableCoins = student.getWallet().getAvailableCoins();
        int userId = student.getUserId();

        PreparedStatement preparedStatement = null;
        String query = "UPDATE wallets SET all_coins = ?, available_coins = ?"+
                "WHERE id_student= ?;";

        try{
            preparedStatement = DbConnection.getConnection().prepareStatement(query);
            preparedStatement.setInt(1, allCoins);
            preparedStatement.setInt(2, availableCoins);
            preparedStatement.setInt(3, userId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException | ClassNotFoundException e){
            System.out.println("Wallet update failed");
        }
    }

    @Override
    public void exportStudentArtifact(int idArtifact, int idStudent) {

        String status = "new";

        PreparedStatement preparedStatement = null;
        String query = "INSERT INTO artifacts_in_wallets (id_artifact, id_student, status)" +
                "VALUES (?, ?, ?);";

        try {
            preparedStatement = DbConnection.getConnection().prepareStatement(query);
            preparedStatement.setInt(1, idArtifact);
            preparedStatement.setInt(2, idStudent);
            preparedStatement.setString(3, status);

            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Artifact insertion failed");
        }
    }

    @Override
    public void updateStudentsArtifact(int idArtifact, int idStudent){

        String statusArtifact = "used";

        PreparedStatement preparedStatement = null;
        String query = "UPDATE artifacts_in_wallets SET status = ?"+
                    "WHERE id_artifact= ? and id_student = ?;";

        try{
            preparedStatement = DbConnection.getConnection().prepareStatement(query);
            preparedStatement.setString(1, statusArtifact);
            preparedStatement.setInt(2, idArtifact);
            preparedStatement.setInt(3, idStudent);

            preparedStatement.executeUpdate();
            preparedStatement.close();

        }catch (SQLException | ClassNotFoundException e){
            System.out.println("Artifact update failed");
        }
    }
}