package dao;

import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DaoQuest extends SqlDao implements IDaoQuest {

    DaoQuest(Connection connection) {
        super(connection);
    }

    @Override
    public Quest createQuest(String name, int value, String description, String type, String category) {
        return new Quest(name, value, description, type, category);
    }

    @Override
    public Quest createQuest(int itemId, String name, int value, String description, String type, String category) {
        return new Quest(itemId, name, value, description, type, category);
    }

    @Override
    public Quest importQuest(int itemId) {
        Quest quest = null;

        String query = "SELECT * FROM quests WHERE id_quest = ?";
        try (
             PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
             preparedStatement.setInt(1, itemId);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {

                if (!resultSet.isClosed()) {
                    String name = resultSet.getString("name");
                    int value = resultSet.getInt("value");
                    String description = resultSet.getString("description");
                    String type = resultSet.getString("type");
                    String category = resultSet.getString("category");

                    quest = createQuest(itemId, name, value, description, type, category);

                }
            }

         }catch(SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return quest;
    }
    @Override
    public List<Quest> getAllQuests() {
        List<Quest> quests = new ArrayList<>();

        String query = "SELECT id_quest FROM quests";

        try (
             PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
             try(ResultSet resultSet = preparedStatement.executeQuery()) {

                 while (resultSet.next()) {
                     int questId = resultSet.getInt("id_quest");
                     Quest quest = importQuest(questId);
                     quests.add(quest);
                 }
             }

        }catch(SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return quests;
    }

    @Override
    public boolean updateQuest(Quest quest) {
        int itemId = quest.getItemId();
        String name = quest.getName();
        int value = quest.getValue();
        String description = quest.getDescription();
        String type = quest.getType();
        String category = quest.getCategory();

        String query = "UPDATE quests SET " +
        "name = ?, value = ?, description = ?, type = ?, category =? " +
        "WHERE id_quest = ?";

        try (
             PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, value);
            preparedStatement.setString(3, description);
            preparedStatement.setString(4, type);
            preparedStatement.setString(5, category);
            preparedStatement.setInt(6, itemId);

            preparedStatement.executeUpdate();
             return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean exportQuest(Quest quest) {

        String query = "INSERT INTO quests VALUES (?, ?, ?, ?, ?, ?);";

        try (
             PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {

            preparedStatement.setString(2, quest.getName());
            preparedStatement.setInt(3, quest.getValue());
            preparedStatement.setString(4, quest.getDescription());
            preparedStatement.setString(5, quest.getType());
            preparedStatement.setString(6, quest.getCategory());

            preparedStatement.executeUpdate();
             return true;

        }catch (SQLException e){
            return false;
        }
    }

    @Override
    public List<Quest> getTeamQuests() {
        List<Quest> quests = new ArrayList<>();

        String query = "SELECT id_quest FROM quests WHERE type = ?;";

        try (
             PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, "team");
            try(ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int questId = resultSet.getInt("id_quest");
                    Quest quest = importQuest(questId);
                    quests.add(quest);
                }
            }

        }catch(SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return quests;
    }

    @Override
    public List<Quest> getIndividualQuests() {
        List<Quest> quests = new ArrayList<>();

        String query = "SELECT id_quest FROM quests WHERE type = ?;";

        try (
             PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, "individual");
            try(ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int questId = resultSet.getInt("id_quest");
                    Quest quest = importQuest(questId);
                    quests.add(quest);
                }
            }

        }catch(SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return quests;
    }

}

