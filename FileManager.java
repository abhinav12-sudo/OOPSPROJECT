import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * CONCEPTS: FILE HANDLING, EXCEPTION HANDLING (try/catch, try-with-resources), STATIC METHODS
 * Data is stored as CSV (comma-separated values) text files, one record per line:
 *   students.csv      id,name,department,semester,completed1;completed2
 *   courses.csv       code,name,credits,prerequisite,capacity
 *   registrations.csv studentId,courseCode
 * "static" methods belong to the class itself, so we call FileManager.saveAll(...) without creating an object.
 */
public class FileManager {

    private static final String STUDENT_FILE = "students.csv";
    private static final String COURSE_FILE = "courses.csv";
    private static final String REGISTRATION_FILE = "registrations.csv";

    // ------------------------------------------------------------------ SAVE

    public static void saveAll(RegistrationSystem system) {
        // try-with-resources: the writer in the brackets is closed AUTOMATICALLY at the end,
        // even if an error occurs. (Forgetting to close can lose data.)
        // FileWriter opens/creates the file (overwriting it); PrintWriter gives us println().
        try (PrintWriter out = new PrintWriter(new FileWriter(STUDENT_FILE))) {
            for (Student s : system.getAllStudents()) {
                // String.join(";", list) glues the completed course codes together: "CS101;MA101"
                out.println(s.getId() + "," + s.getName() + "," + s.getDepartment() + ","
                        + s.getSemester() + "," + String.join(";", s.getCompletedCourses()));
            }
        } catch (IOException e) { // IOException = anything that goes wrong with files
            System.out.println("Error saving students: " + e.getMessage());
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(COURSE_FILE))) {
            for (Course c : system.getAllCourses()) {
                out.println(c.getCode() + "," + c.getName() + "," + c.getCredits() + ","
                        + c.getPrerequisite() + "," + c.getCapacity());
            }
        } catch (IOException e) {
            System.out.println("Error saving courses: " + e.getMessage());
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(REGISTRATION_FILE))) {
            for (Registration r : system.getAllRegistrations()) {
                out.println(r.getStudent().getId() + "," + r.getCourse().getCode());
            }
        } catch (IOException e) {
            System.out.println("Error saving registrations: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ LOAD

    /*
     * ORDER MATTERS: registrations refer to students and courses,
     * so those two must be loaded first.
     */
    public static void loadAll(RegistrationSystem system) {
        loadStudents(system);
        loadCourses(system);
        loadRegistrations(system);
    }

    private static void loadStudents(RegistrationSystem system) {
        // BufferedReader reads a file line by line efficiently.
        // If the file does not exist (first run) FileReader throws FileNotFoundException, a kind of IOException.
        try (BufferedReader in = new BufferedReader(new FileReader(STUDENT_FILE))) {
            String line;
            // in.readLine() returns null at the end of the file, which stops the loop.
            while ((line = in.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // skip blank lines
                }
                // split(",", -1): the -1 keeps empty trailing fields (e.g. no completed courses).
                String[] p = line.split(",", -1);
                try {
                    Student s = new Student(p[0], p[1], p[2], Integer.parseInt(p[3]));
                    // Re-create the completed list from "CS101;MA101"
                    if (p.length > 4 && !p[4].isEmpty()) {
                        for (String code : p[4].split(";")) {
                            s.addCompletedCourse(code);
                        }
                    }
                    system.addStudent(s);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException | RegistrationException e) {
                    // Bad line: report it and carry on with the rest of the file.
                    System.out.println("Skipping bad student record: " + line);
                }
            }
        } catch (IOException e) {
            // First run: no file yet. That is normal, so we stay silent.
        }
    }

    private static void loadCourses(RegistrationSystem system) {
        try (BufferedReader in = new BufferedReader(new FileReader(COURSE_FILE))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] p = line.split(",", -1);
                try {
                    // Integer.parseInt converts text "4" into the number 4; throws NumberFormatException if invalid.
                    system.addCourse(new Course(p[0], p[1], Integer.parseInt(p[2]), p[3], Integer.parseInt(p[4])));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException | RegistrationException e) {
                    System.out.println("Skipping bad course record: " + line);
                }
            }
        } catch (IOException e) {
            // no file yet: fine
        }
    }

    private static void loadRegistrations(RegistrationSystem system) {
        try (BufferedReader in = new BufferedReader(new FileReader(REGISTRATION_FILE))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] p = line.split(",", -1);
                if (p.length < 2) {
                    System.out.println("Skipping bad registration record: " + line);
                    continue;
                }
                Student s = system.findStudent(p[0]);
                Course c = system.findCourse(p[1]);
                if (s != null && c != null) {
                    system.addRegistrationWithoutChecks(s, c);
                } else {
                    System.out.println("Skipping registration with unknown student/course: " + line);
                }
            }
        } catch (IOException e) {
            // no file yet: fine
        }
    }
}
