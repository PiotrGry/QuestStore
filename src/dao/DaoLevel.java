package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Level;
import model.Mentor;

public class DaoLevel{
    public Level createLevel(String name, int coinsLimit) {
        return new Level(name, coinsLimit);
    }

    public Level createLevel(int levelId, String name, int coinsLimit) {
        return new Level(levelId, name, coinsLimit);
    }

    public boolean exportLevel(Level level){
        String name = level.getName();
        int coinsLimit = level.getCoinsLimit();
        String query = "INSERT into levels (name, coins_limit)" +
                "values (?, ?);";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, coinsLimit);
            preparedStatement.executeUpdate();
            return true;

        }catch (SQLException  e){
            return false;
        }
    }


    public Level importLevel(int levelId) {
        Level level = null;
        String query = "SELECT name, coins_limit FROM levels WHERE id_level = ?;";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, levelId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (!resultSet.isClosed()) {
                    String name = resultSet.getString("name");
                    int coinsLimit = resultSet.getInt("coins_limit");
                    level = createLevel(levelId, name, coinsLimit);
                }
            }

        } catch (SQLException  e) {
            return level;
        }
        return level;
    }

    public ArrayList<Level> getAllLevels() {
        ArrayList<Level> levels = new ArrayList<>();
        String query = "SELECT id_level FROM levels ORDER BY coins_limit;";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()){
                int levelId = resultSet.getInt("id_level");
                Level level = importLevel(levelId);
                levels.add(level);
            }

        } catch (SQLException  e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }

        return levels;
    }

    public Level importLevelByCoins(int allCoins){
        ArrayList <Level> levels = getMatchingLevels(allCoins);
        Level level = getRightLevel(levels, allCoins);
        return level;
    }

    public ArrayList <Level> getMatchingLevels(int allCoins){
        Level level;
        String query = "SELECT * from levels WHERE coins_limit <= ?";
        ArrayList <Level> levels = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, allCoins);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    int limitCoins = resultSet.getInt("coins_limit");
                    level = createLevel(name, limitCoins);
                    levels.add(level);
                }
            }

        }catch(SQLException  e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return levels;

    }

    public Level getRightLevel(ArrayList <Level> levels, int availableCoins){
        Level level = levels.get(0);

        for(Level elem: levels){
            if(elem.getCoinsLimit() >= level.getCoinsLimit()){
                level = elem;
            }
        }

        return level;

    }
}
