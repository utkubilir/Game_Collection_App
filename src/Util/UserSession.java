package Util;

public final class UserSession {

    private static UserSession instance;

    private int userId;
    private String userName;

    private UserSession(int userId, String userName) {//I use Singleton Design Pattern
        this.userId = userId;
        this.userName = userName;
    }

    public static void createInstance(int userId, String userName) {
        if (instance == null) {
            instance = new UserSession(userId, userName);
        } else {
            instance.userId = userId;
            instance.userName = userName;
        }
    }

    public static UserSession getInstance() {
        if (instance == null) {
            throw new IllegalStateException("UserSession has not been created yet. Please log in first.");
        }
        return instance;
    }
    
    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }
    
    public static void cleanUserSession() {
        instance = null;
    }
}
