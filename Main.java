import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static RegistrationSystem system = new RegistrationSystem();

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            try {
                int choice = readInt("Enter your choice: ");
                switch (choice) {
                    case 1: addStudent(); break;
                    case 2: viewOrSearchStudents(); break;
                    case 3: addCourse(); break;
                    case 4: viewOrSearchCourses(); break;
                    case 5: registerStudent(); break;
                    case 6: viewSummary(); break;
                    case 7: viewStudentsInCourse(); break;
                    case 8:
                        FileManager.saveAll(system);
                        System.out.println("Data saved.");
                        break;
                    case 9:
                        FileManager.saveAll(system);
                        System.out.println("Data saved. Goodbye!");
                        running = false; 
                        break;
                    case 10: markCompleted(); break;
                    case 11: dropCourse(); break;
                    default: System.out.println("Invalid choice. Please enter 1-11.");
                }
            } catch (NoSuchElementException e) {
                FileManager.saveAll(system);
                running = false;
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nSTUDENT COURSE REGISTRATION SYSTEM");
        System.out.println("1. Add Student");
        System.out.println("2. View/Search Students");
        System.out.println("3. Add Course");
        System.out.println("4. View/Search Courses");
        System.out.println("5. Register Student for Course");
        System.out.println("6. View Student Academic Summary");
        System.out.println("7. View Students Registered in a Course");
        System.out.println("8. Save Data");
        System.out.println("9. Exit");
        System.out.println("10. Mark Course as Completed (for prerequisites)");
        System.out.println("11. Drop a Course");
    }
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) { 
                System.out.println("Please enter a valid number.");
            }
        }
    }
    private static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = sc.nextLine().trim();
            if (text.isEmpty()) {
                System.out.println("Input cannot be empty.");
            } else if (text.contains(",")) {
                System.out.println("Commas are not allowed.");
            } else {
                return text;
            }
        }
    }
    private static String readCode(String prompt) {
        return readText(prompt).toUpperCase();
    }

    private static void addStudent() {
        String id = readCode("Student ID: ");
        String name = readText("Name: ");
        String dept = readText("Department: ");
        int sem = readInt("Semester (1-8): ");
        if (sem < 1 || sem > 8) {
            System.out.println("Semester must be between 1 and 8.");
            return;
        }
        try {
            system.addStudent(new Student(id, name, dept, sem));
            FileManager.saveAll(system);
            System.out.println("Student added.");
        } catch (RegistrationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewOrSearchStudents() {
        System.out.println("1. View all   2. Search by ID   3. Show a student's registered courses");
        int ch = readInt("Choice: ");
        if (ch == 1) {
            ArrayList<Displayable> list = new ArrayList<>(system.getAllStudents());
            showAll(list);
        } else if (ch == 2) {
            Student s = system.findStudent(readCode("Student ID: "));
            if (s == null) {
                System.out.println("Student not found.");
            } else {
                s.displayDetails();
            }
        } else if (ch == 3) {
            String id = readCode("Student ID: ");
            if (system.findStudent(id) == null) {
                System.out.println("Student not found.");
            } else {
                ArrayList<Displayable> list = new ArrayList<>(system.getRegistrationsOf(id));
                showAll(list);
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void addCourse() {
        String code = readCode("Course code: ");
        String name = readText("Course name: ");
        int credits = readInt("Credits (1-10): ");
        if (credits < 1 || credits > 10) {
            System.out.println("Credits must be between 1 and 10.");
            return;
        }
        System.out.print("Prerequisite course code (press Enter for none): ");
        String prereq = sc.nextLine().trim().toUpperCase();
        if (prereq.contains(",")) {
            System.out.println("Commas are not allowed.");
            return;
        }
        int capacity = readInt("Maximum capacity: ");
        if (capacity < 1) {
            System.out.println("Capacity must be at least 1.");
            return;
        }
        try {
            system.addCourse(new Course(code, name, credits, prereq, capacity));
            FileManager.saveAll(system);
            System.out.println("Course added.");
        } catch (RegistrationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewOrSearchCourses() {
        System.out.println("1. View all   2. Search by code");
        int ch = readInt("Choice: ");
        if (ch == 1) {
            showAll(new ArrayList<Displayable>(system.getAllCourses()));
        } else if (ch == 2) {
            Course c = system.findCourse(readCode("Course code: "));
            if (c == null) {
                System.out.println("Course not found.");
            } else {
                c.displayDetails();
                System.out.println("Seats taken: " + system.countEnrolled(c.getCode()) + "/" + c.getCapacity());
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void registerStudent() {
        String sid = readCode("Student ID: ");
        String code = readCode("Course code: ");
        try {
            system.registerStudent(sid, code);
            FileManager.saveAll(system);
            System.out.println("Registration successful.");
        } catch (RegistrationException e) {
            System.out.println("Registration rejected: " + e.getMessage());
        }
    }

    private static void viewSummary() {
        try {
            system.printSummary(readCode("Student ID: "));
        } catch (RegistrationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewStudentsInCourse() {
        String code = readCode("Course code: ");
        if (system.findCourse(code) == null) {
            System.out.println("Course not found.");
            return;
        }
        showAll(new ArrayList<Displayable>(system.getRegistrationsIn(code)));
    }

    private static void markCompleted() {
        try {
            system.markCompleted(readCode("Student ID: "), readCode("Completed course code: "));
            FileManager.saveAll(system);
            System.out.println("Course marked as completed.");
        } catch (RegistrationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void dropCourse() {
        try {
            system.dropCourse(readCode("Student ID: "), readCode("Course code: "));
            FileManager.saveAll(system);
            System.out.println("Course dropped.");
        } catch (RegistrationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private static void showAll(ArrayList<Displayable> items) {
        if (items.isEmpty()) {
            System.out.println("(nothing to show)");
            return;
        }
        for (Displayable d : items) {
            d.displayDetails();
        }
    }
}
