package model;

public abstract class Item{

    protected String name;
    protected int value;
    protected String description;
    protected boolean status;
    protected int itemId;

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public boolean getStatus() {
        return status;
    }

}
