public class Course implements Displayable {

    private String code;
    private String name;
    private int credits;
    private String prerequisite;
    private int capacity;

    public Course(String code, String name, int credits, String prerequisite, int capacity) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.prerequisite = (prerequisite == null) ? "" : prerequisite;
        this.capacity = capacity;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }

    public String getPrerequisite() {
        return prerequisite;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean hasPrerequisite() {
        return !prerequisite.isEmpty();
    }

    @Override
    public void displayDetails() {
        String prereqText = prerequisite;
        if (!hasPrerequisite()) {
            prereqText = "None";
        }
        System.out.println("Code: " + code + " | Name: " + name + " | Credits: " + credits
                + " | Prerequisite: " + prereqText + " | Capacity: " + capacity);
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }
}
