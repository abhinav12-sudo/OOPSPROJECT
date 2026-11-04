import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
public class RegistrationSystem {
    public static final int MAX_CREDITS = 20;
    private HashMap<String, Student> students = new LinkedHashMap<>();
    private HashMap<String, Course> courses = new LinkedHashMap<>();
    private ArrayList<Registration> registrations = new ArrayList<>();


    public void addStudent(Student student) throws RegistrationException {
        if (students.containsKey(student.getId())) {
            throw new RegistrationException("Student ID " + student.getId() + " already exists.");
        }
        students.put(student.getId(), student); 
    }

    public Student findStudent(String id) {
        return students.get(id);
    }

    public ArrayList<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

    public void addCourse(Course course) throws RegistrationException {
        if (courses.containsKey(course.getCode())) {
            throw new RegistrationException("Course code " + course.getCode() + " already exists.");
        }
        if (course.getCode().equals(course.getPrerequisite())) {
            throw new RegistrationException("A course cannot be its own prerequisite.");
        }
        courses.put(course.getCode(), course);
    }

    public Course findCourse(String code) {
        return courses.get(code);
    }

    public ArrayList<Course> getAllCourses() {
        return new ArrayList<>(courses.values());
    }
    public void registerStudent(String studentId, String courseCode) throws RegistrationException {

        Student student = findStudent(studentId);
        if (student == null) {
            throw new RegistrationException("Student " + studentId + " does not exist.");
        }
        Course course = findCourse(courseCode);
        if (course == null) {
            throw new RegistrationException("Course " + courseCode + " does not exist.");
        }
        if (isRegistered(studentId, courseCode)) {
            throw new RegistrationException(student.getName() + " is already registered for " + courseCode + ".");
        }
        if (course.hasPrerequisite() && !student.hasCompleted(course.getPrerequisite())) {
            throw new RegistrationException("Prerequisite " + course.getPrerequisite()
                    + " has not been completed by " + student.getName() + ".");
        }
        int newTotal = getRegisteredCredits(studentId) + course.getCredits();
        if (newTotal > MAX_CREDITS) {
            throw new RegistrationException("Credit limit exceeded: this would make " + newTotal
                    + " credits (maximum " + MAX_CREDITS + ").");
        }
        if (countEnrolled(courseCode) >= course.getCapacity()) {
            throw new RegistrationException("Course " + courseCode + " is full ("
                    + course.getCapacity() + " seats).");
        }
        registrations.add(new Registration(student, course));
    }
    public void addRegistrationWithoutChecks(Student student, Course course) {
        registrations.add(new Registration(student, course));
    }
    public void dropCourse(String studentId, String courseCode) throws RegistrationException {
        for (int i = 0; i < registrations.size(); i++) {
            Registration r = registrations.get(i);
            if (r.getStudent().getId().equals(studentId) && r.getCourse().getCode().equals(courseCode)) {
                registrations.remove(i);
                return;
            }
        }
        throw new RegistrationException("Student " + studentId + " is not registered for " + courseCode + ".");
    }

    public void markCompleted(String studentId, String courseCode) throws RegistrationException {
        Student student = findStudent(studentId);
        if (student == null) {
            throw new RegistrationException("Student " + studentId + " does not exist.");
        }
        if (findCourse(courseCode) == null) {
            throw new RegistrationException("Course " + courseCode + " does not exist.");
        }
        student.addCompletedCourse(courseCode);
        if (isRegistered(studentId, courseCode)) {
            dropCourse(studentId, courseCode);
        }
    }


    public boolean isRegistered(String studentId, String courseCode) {
        for (Registration r : registrations) {
            if (r.getStudent().getId().equals(studentId) && r.getCourse().getCode().equals(courseCode)) {
                return true;
            }
        }
        return false;
    }

    public int countEnrolled(String courseCode) {
        int count = 0;
        for (Registration r : registrations) {
            if (r.getCourse().getCode().equals(courseCode)) {
                count++;
            }
        }
        return count;
    }
    public ArrayList<Registration> getRegistrationsOf(String studentId) {
        ArrayList<Registration> result = new ArrayList<>();
        for (Registration r : registrations) {
            if (r.getStudent().getId().equals(studentId)) {
                result.add(r);
            }
        }
        return result;
    }
    public ArrayList<Registration> getRegistrationsIn(String courseCode) {
        ArrayList<Registration> result = new ArrayList<>();
        for (Registration r : registrations) {
            if (r.getCourse().getCode().equals(courseCode)) {
                result.add(r);
            }
        }
        return result;
    }
    public int getRegisteredCredits(String studentId) {
        int total = 0;
        for (Registration r : getRegistrationsOf(studentId)) {
            total += r.getCourse().getCredits(); // total = total + credits
        }
        return total;
    }

    public ArrayList<Registration> getAllRegistrations() {
        return registrations;
    }

    public void printSummary(String studentId) throws RegistrationException {
        Student student = findStudent(studentId);
        if (student == null) {
            throw new RegistrationException("Student " + studentId + " does not exist.");
        }

        System.out.println("\n===== ACADEMIC SUMMARY =====");
        System.out.println("Student ID : " + student.getId());
        System.out.println("Name       : " + student.getName());
        System.out.println("Department: " + student.getDepartment() + ", Semester " + student.getSemester());

        System.out.println("\nRegistered courses:");
        ArrayList<Registration> regs = getRegistrationsOf(studentId);
        if (regs.isEmpty()) {
            System.out.println("  (none)");
        }
        for (Registration r : regs) {
            Course c = r.getCourse();
            System.out.println("  " + c.getCode() + "  " + c.getName() + "  (" + c.getCredits() + " credits)");
        }

        int used = getRegisteredCredits(studentId);
        System.out.println("\nTotal registered credits : " + used);
        System.out.println("Completed prerequisites  : "
                + (student.getCompletedCourses().isEmpty() ? "(none)" : student.getCompletedCourses()));
        System.out.println("Remaining credit allowance: " + (MAX_CREDITS - used));
    }
}
