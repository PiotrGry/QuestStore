package manager.database;

import enums.dbEnums.DbDriver;
import enums.dbEnums.DbFilePath;
import enums.dbEnums.DbUrl;

import java.util.Properties;

public class SqlConfig implements DatabaseConfiguration {

    private final String DRIVER;
    private final String URL;
    private final String FILEPATH;
    private final Properties PROPERTIES;

    // get configuration object via static method - different for varied database engines
    public static DatabaseConfiguration createSQLiteConfiguration(DbUrl URL, DbDriver DRIVER,
                                                                  DbFilePath FILEPATH, Properties properties) {
        return new SqlConfig(URL, DRIVER, FILEPATH, properties);
    }

    public static DatabaseConfiguration createSQLiteConfiguration(DbUrl URL, DbDriver DRIVER,
                                                         DbFilePath FILEPATH) {
        return new SqlConfig(URL, DRIVER, FILEPATH, getSQLiteDefaultProperties());
    }

    public static DatabaseConfiguration createPosgresConfiguration(DbUrl URL, DbDriver DRIVER,
                                                                  DbFilePath FILEPATH) {
        return new SqlConfig(URL, DRIVER, FILEPATH, getPostgresDefaultProperties());
    }

    private SqlConfig(DbUrl URL, DbDriver DRIVER, DbFilePath FILEPATH, Properties properties) {
        this.URL = URL.getUrl();
        this.DRIVER = DRIVER.getDriver();
        this.FILEPATH = FILEPATH.getPath();
        this.PROPERTIES = properties;
    }

    @Override
    public String getUrl() {
        return URL;
    }

    @Override
    public String getDriver() {
        return DRIVER;
    }

    @Override
    public String getFilepath() {
        return FILEPATH;
    }

    @Override
    public Properties getProperties() {
        return PROPERTIES;
    }

    private static Properties getSQLiteDefaultProperties() {
        org.sqlite.SQLiteConfig config = new org.sqlite.SQLiteConfig();
        config.enforceForeignKeys(true);
        return config.toProperties();
    }

    private static Properties getPostgresDefaultProperties() {
        Properties props = new Properties();
        props.setProperty("user","szyfratorzy");
        props.setProperty("password","#apaj1234");
        props.setProperty("ssl","true");
        return props;
    }

}
