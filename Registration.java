public class Registration implements Displayable {

    private Student student;
    private Course course;

    public Registration(Student student, Course course) {
        this.student = student;
        this.course = course;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    @Override
    public void displayDetails() {
        System.out.println(student.getName() + " (" + student.getId() + ") -> "
                + course.getCode() + " " + course.getName());
    }
}
