import java.util.ArrayList;

/*
 * CONCEPTS: INHERITANCE, METHOD OVERRIDING, INTERFACE IMPLEMENTATION, COLLECTIONS
 * "extends Person"      -> Student INHERITS id, name, getId(), getName() from Person.
 * "implements Displayable" -> Student promises to provide displayDetails().
 */
public class Student extends Person implements Displayable {

    // Extra fields that only a student has (also private = encapsulated).
    private String department;
    private int semester;

    // COLLECTION: an ArrayList (a resizable array) of course codes the student has
    // finished.
    // Used to check prerequisites. We store codes (Strings) rather than Course
    // objects: simple and easy to save to a file.
    private ArrayList<String> completedCourses;

    // CONSTRUCTOR: first calls the parent constructor with super(...), then sets
    // its own fields.
    public Student(String id, String name, String department, int semester) {
        super(id, name); // must be the FIRST line in a child constructor
        this.department = department;
        this.semester = semester;
        this.completedCourses = new ArrayList<>(); // start with an empty list
    }

    public String getDepartment() {
        return department;
    }

    public int getSemester() {
        return semester;
    }

    // Returns the list so callers can read it (e.g. FileManager when saving).
    public ArrayList<String> getCompletedCourses() {
        return completedCourses;
    }

    // Adds a completed course code, but only if it is not already there (avoids
    // duplicates).
    public void addCompletedCourse(String courseCode) {
        if (!completedCourses.contains(courseCode)) {
            completedCourses.add(courseCode);
        }
    }

    // Returns true if the student has finished the given course.
    public boolean hasCompleted(String courseCode) {
        return completedCourses.contains(courseCode);
    }

    // METHOD OVERRIDING: Person declared getRole() abstract; Student gives the real
    // body.
    @Override
    public String getRole() {
        return "Student";
    }

    // METHOD OVERRIDING again: implements the interface method.
    @Override
    public void displayDetails() {
        // Join text and values with + and print one line.
        // getId() and getName() are used because id and name are private in Person.
        System.out.println("ID: " + getId() + " | Name: " + getName()
                + " | Department: " + department + " | Semester: " + semester);
    }
}
