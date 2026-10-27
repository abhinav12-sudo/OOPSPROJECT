/*
 * CONCEPTS: CLASS/OBJECT, ENCAPSULATION, CONSTRUCTOR, INTERFACE IMPLEMENTATION
 * A Course object is one row of the catalogue.
 */
public class Course implements Displayable {

    private String code; // unique, e.g. "CS201"
    private String name;
    private int credits;
    private String prerequisite; // course code, or "" (empty string) when there is none
    private int capacity; // maximum number of students

    public Course(String code, String name, int credits, String prerequisite, int capacity) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        // If the caller passes null, store "" so we never have to worry about null
        // later.
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

    // Helper: true if this course has a prerequisite.
    public boolean hasPrerequisite() {
        return !prerequisite.isEmpty();
    }

    @Override
    public void displayDetails() {
        // If there is no prerequisite we print "None" instead of an empty space.
        String prereqText = prerequisite;
        if (!hasPrerequisite()) {
            prereqText = "None";
        }
        // Join text and values with + and print one line.
        System.out.println("Code: " + code + " | Name: " + name + " | Credits: " + credits
                + " | Prerequisite: " + prereqText + " | Capacity: " + capacity);
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }
}
