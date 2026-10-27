import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/*
 * CONCEPTS: COLLECTIONS (HashMap + ArrayList), EXCEPTION THROWING, MODULAR DESIGN
 * This is the "brain". It owns all the data and enforces all the rules.
 * Main only talks to the user; FileManager only reads/writes files; THIS class decides what is valid.
 */
public class RegistrationSystem {

    // Rule from the assignment: a student may not exceed 20 credits.
    // "static final" = a constant shared by all objects; it can never change.
    public static final int MAX_CREDITS = 20;

    // HashMap<Key, Value>: lookup by key is very fast. Key = student ID, Value = Student object.
    // We create a LinkedHashMap (a HashMap subclass) so students are displayed in the order they were added.
    private HashMap<String, Student> students = new LinkedHashMap<>();
    private HashMap<String, Course> courses = new LinkedHashMap<>();

    // ArrayList: ordered list of all registrations. We need to loop over it, so a list is natural.
    private ArrayList<Registration> registrations = new ArrayList<>();

    // ---------------------------------------------------------------- Module A: students

    // "throws RegistrationException" = this method may fail and tells the caller to be ready for that.
    public void addStudent(Student student) throws RegistrationException {
        // containsKey checks the map for an existing ID: this is the duplicate-ID prevention.
        if (students.containsKey(student.getId())) {
            // "throw new ..." creates the exception object and stops the method immediately.
            throw new RegistrationException("Student ID " + student.getId() + " already exists.");
        }
        students.put(student.getId(), student); // key -> value
    }

    // Returns the student or null if not found (map.get returns null for a missing key).
    public Student findStudent(String id) {
        return students.get(id);
    }

    public ArrayList<Student> getAllStudents() {
        // values() is a view of all Student objects; we copy it into an ArrayList for easy looping.
        return new ArrayList<>(students.values());
    }

    // ---------------------------------------------------------------- Module B: courses

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

    // ---------------------------------------------------------------- Module C: registration

    /*
     * The six checks, in the exact order the assignment lists them.
     * The FIRST failing check throws, so later code never runs on invalid data.
     */
    public void registerStudent(String studentId, String courseCode) throws RegistrationException {

        // 1. Student validity
        Student student = findStudent(studentId);
        if (student == null) {
            throw new RegistrationException("Student " + studentId + " does not exist.");
        }

        // 2. Course validity
        Course course = findCourse(courseCode);
        if (course == null) {
            throw new RegistrationException("Course " + courseCode + " does not exist.");
        }

        // 3. Duplicate registration
        if (isRegistered(studentId, courseCode)) {
            throw new RegistrationException(student.getName() + " is already registered for " + courseCode + ".");
        }

        // 4. Prerequisite completion (only checked when the course HAS a prerequisite)
        if (course.hasPrerequisite() && !student.hasCompleted(course.getPrerequisite())) {
            throw new RegistrationException("Prerequisite " + course.getPrerequisite()
                    + " has not been completed by " + student.getName() + ".");
        }

        // 5. Credit limit: current credits + new course credits must stay <= 20
        int newTotal = getRegisteredCredits(studentId) + course.getCredits();
        if (newTotal > MAX_CREDITS) {
            throw new RegistrationException("Credit limit exceeded: this would make " + newTotal
                    + " credits (maximum " + MAX_CREDITS + ").");
        }

        // 6. Capacity: how many students are already in this course?
        if (countEnrolled(courseCode) >= course.getCapacity()) {
            throw new RegistrationException("Course " + courseCode + " is full ("
                    + course.getCapacity() + " seats).");
        }

        // All checks passed -> create the Registration object and store it.
        registrations.add(new Registration(student, course));
    }

    // Used by FileManager when loading: the data was already validated when first saved,
    // so we skip the checks and just add it.
    public void addRegistrationWithoutChecks(Student student, Course course) {
        registrations.add(new Registration(student, course));
    }

    // Drop a course (RegistrationSystem is also responsible for "dropping" per the suggested design).
    public void dropCourse(String studentId, String courseCode) throws RegistrationException {
        // Loop over the list and remove the matching registration.
        for (int i = 0; i < registrations.size(); i++) {
            Registration r = registrations.get(i);
            if (r.getStudent().getId().equals(studentId) && r.getCourse().getCode().equals(courseCode)) {
                registrations.remove(i);
                return; // done; leave the method
            }
        }
        // If the loop ended without returning, nothing matched.
        throw new RegistrationException("Student " + studentId + " is not registered for " + courseCode + ".");
    }

    // Mark a course as completed (needed so that prerequisites can ever be satisfied).
    public void markCompleted(String studentId, String courseCode) throws RegistrationException {
        Student student = findStudent(studentId);
        if (student == null) {
            throw new RegistrationException("Student " + studentId + " does not exist.");
        }
        if (findCourse(courseCode) == null) {
            throw new RegistrationException("Course " + courseCode + " does not exist.");
        }
        student.addCompletedCourse(courseCode);
        // A finished course is no longer "currently registered": free the credits and the seat.
        if (isRegistered(studentId, courseCode)) {
            dropCourse(studentId, courseCode);
        }
    }

    // ---------------------------------------------------------------- queries (helper methods)

    public boolean isRegistered(String studentId, String courseCode) {
        for (Registration r : registrations) { // for-each loop: visits every element
            if (r.getStudent().getId().equals(studentId) && r.getCourse().getCode().equals(courseCode)) {
                return true;
            }
        }
        return false;
    }

    // Number of students currently in a course.
    public int countEnrolled(String courseCode) {
        int count = 0;
        for (Registration r : registrations) {
            if (r.getCourse().getCode().equals(courseCode)) {
                count++;
            }
        }
        return count;
    }

    // All registrations belonging to one student.
    public ArrayList<Registration> getRegistrationsOf(String studentId) {
        ArrayList<Registration> result = new ArrayList<>();
        for (Registration r : registrations) {
            if (r.getStudent().getId().equals(studentId)) {
                result.add(r);
            }
        }
        return result;
    }

    // All registrations in one course (Menu option 7).
    public ArrayList<Registration> getRegistrationsIn(String courseCode) {
        ArrayList<Registration> result = new ArrayList<>();
        for (Registration r : registrations) {
            if (r.getCourse().getCode().equals(courseCode)) {
                result.add(r);
            }
        }
        return result;
    }

    // Sum of credits over a student's registrations.
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

    // ---------------------------------------------------------------- Module D: summary

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
