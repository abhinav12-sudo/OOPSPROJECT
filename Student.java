import java.util.ArrayList;
public class Student extends Person implements Displayable {
    private String department;
    private int semester;
    private ArrayList<String> completedCourses;
    public Student(String id, String name, String department, int semester) {
        super(id, name);
        this.department = department;
        this.semester = semester;
        this.completedCourses = new ArrayList<>(); 
    }

    public String getDepartment() {
        return department;
    }

    public int getSemester() {
        return semester;
    }
    public ArrayList<String> getCompletedCourses() {
        return completedCourses;
    }
    public void addCompletedCourse(String courseCode) {
        if (!completedCourses.contains(courseCode)) {
            completedCourses.add(courseCode);
        }
    }
    public boolean hasCompleted(String courseCode) {
        return completedCourses.contains(courseCode);
    }
    @Override
    public String getRole() {
        return "Student";
    }
    @Override
    public void displayDetails() {
        System.out.println("ID: " + getId() + " | Name: " + getName()
                + " | Department: " + department + " | Semester: " + semester);
    }
}
