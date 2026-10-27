# Student Course Registration System: Study Guide

## 1. How to run

```
javac *.java
java Main
```
Keep all `.java` files in one folder. The program creates `students.csv`, `courses.csv` and `registrations.csv` in the folder you run it from.

## 2. The approach, step by step

I solved it in this order, from the simplest building block to the most complex. You can use the same order to explain it in the viva.

1. **Read the requirements and list the "nouns"**: Student, Course, Registration, Person (common data). Nouns became classes.
2. **List the "rules"** (6 registration checks, duplicate IDs, 20-credit limit). Rules need a place to live, so I made one coordinator class, `RegistrationSystem`.
3. **Decide where data lives**: students and courses are looked up by ID/code, so `HashMap`. Registrations are just an ordered list, so `ArrayList`.
4. **Decide how failures are reported**: a custom checked exception, `RegistrationException`, carrying a human-readable message.
5. **Separate responsibilities** (modular design):
   - `Main`: talks to the user only.
   - `RegistrationSystem`: decides what is valid.
   - `FileManager`: reads/writes files only.
6. **Build bottom-up**: Displayable -> Person -> RegistrationException -> Student -> Course -> Registration -> RegistrationSystem -> FileManager -> Main.
7. **Test every rule** with a scripted session, then restart the program to confirm data reloads.

## 3. Class map

| Class | Role | Key concept |
|---|---|---|
| `Displayable` | contract: `displayDetails()` | Interface |
| `Person` | id + name, abstract `getRole()` | Abstract class, encapsulation |
| `Student` | extends Person, adds department, semester, completed courses | Inheritance, overriding |
| `Course` | code, name, credits, prerequisite, capacity | Class/object, constructor |
| `Registration` | links one Student to one Course | Association |
| `RegistrationException` | invalid-operation error | User-defined exception |
| `RegistrationSystem` | all rules and data | Collections, throwing exceptions |
| `FileManager` | CSV save/load | File handling, try-with-resources |
| `Main` | menu and input | Polymorphism, input validation |

## 4. Mandatory concepts: where each one is used

1. **Classes and objects**: every file. `new Student(...)` creates an object from the Student blueprint.
2. **Encapsulation**: all fields are `private`; access is through getters (`getId()`), and `setName()` validates before changing.
3. **Constructors**: `Person(id, name)`, `Student(...)` calls `super(id, name)` first, `Course(...)`, `Registration(...)`.
4. **Inheritance**: `Student extends Person`; `RegistrationException extends Exception`.
5. **Overriding + runtime polymorphism**: `getRole()`, `displayDetails()`, `toString()` are overridden. In `Main.showAll`, a `Displayable` list holds Students, Courses and Registrations; Java picks the right `displayDetails()` at run time.
6. **Abstract class**: `Person` (cannot do `new Person()`; forces `getRole()`).
7. **Interface**: `Displayable`, implemented by three classes.
8. **Collections**: `HashMap<String, Student>`, `HashMap<String, Course>`, `ArrayList<Registration>`, `ArrayList<String>` (completed courses).
9. **Exception handling**: `try/catch` in `Main` and `FileManager`; `NumberFormatException` for bad numbers; `IOException` for files.
10. **User-defined exception**: `RegistrationException`, thrown with `throw new RegistrationException("...")`.
11. **File handling**: `FileWriter`/`PrintWriter` to save, `FileReader`/`BufferedReader` to load, in try-with-resources.
12. **Modular design**: small single-purpose methods; `main()` only loads data and runs the menu loop.

## 5. The six registration checks (Module C)

They sit in `RegistrationSystem.registerStudent`, in the order the assignment lists them. Each failing check throws immediately, so the next check never runs on bad data.

1. `findStudent` returns `null` -> student does not exist.
2. `findCourse` returns `null` -> course does not exist.
3. `isRegistered` loops over registrations -> already registered.
4. `course.hasPrerequisite() && !student.hasCompleted(prereq)` -> prerequisite missing.
5. current credits + course credits > 20 -> credit limit.
6. `countEnrolled(code) >= capacity` -> course full.

Only if all pass is `registrations.add(new Registration(...))` reached.

## 6. Design decisions you should be ready to justify

- **Registrations are stored once**, in `RegistrationSystem`. Seats taken and credits used are *calculated* by counting/summing, never stored separately, so they can never get out of sync.
- **Extra menu options 10 and 11** (mark completed, drop). Without "mark completed", no student could ever satisfy a prerequisite. The assignment allows menu changes as long as all required functions exist.
- **Marking a course completed removes it from current registrations**, freeing credits and a seat.
- **Commas are rejected in input** because CSV uses commas as separators.
- **IDs and codes are converted to upper case**, so `cs101` and `CS101` are the same.
- **`LinkedHashMap` is used** (it is a `HashMap`) so records display in insertion order.
- **Auto-save after every change**, plus manual save (option 8) and save on exit.
- **Load order is students -> courses -> registrations**, because a registration refers to a student and a course.

## 7. Test results (all passed)

| Test | Result |
|---|---|
| Duplicate student ID | "Student ID S1 already exists." |
| Duplicate course code | "Course code CS101 already exists." |
| Unknown student / course | rejected with message |
| Already registered | rejected |
| Missing prerequisite | rejected |
| 14 + 10 credits = 24 | "Credit limit exceeded... (maximum 20)" |
| Course with capacity 1 | second student rejected: "Course CAP1 is full" |
| Restart program | students, courses, registrations and completed courses all reloaded |

## 8. Likely viva questions

- **Why is `Person` abstract?** A generic "person" is never created on its own; it only holds shared data and forces subclasses to define `getRole()`.
- **Abstract class vs interface?** An abstract class shares state and code between related classes (is-a). An interface is a pure contract any class can sign (can-do). A class can extend one class but implement many interfaces.
- **Where is runtime polymorphism?** `Main.showAll`: the variable type is `Displayable`, the real object is Student/Course/Registration, and the actual method is chosen at run time.
- **Why a checked exception?** It forces callers to deal with invalid registrations rather than ignoring them.
- **Why `HashMap` for students?** Looking up by ID is fast and `containsKey` gives duplicate prevention in one line.
- **What does try-with-resources do?** Closes the file automatically, even on error, so data is not lost.
- **What does `super(id, name)` do?** Calls the parent constructor to set the inherited fields; it must be the first statement.
- **What does `split(",", -1)` do?** Splits a CSV line and keeps trailing empty fields (a student with no completed courses).
- **What happens if a CSV line is corrupt?** That line is skipped with a message; the rest still loads.
- **How is remaining credit computed?** `MAX_CREDITS - sum of credits of the student's registrations`.







Here is the flow as a student would naturally work through it, moving from file to file and going back when needed. It differs slightly from the exact order I created the files in, because I wrote bottom-up in one pass. This is more realistic for someone doing it by hand.

Step 1: Student.java. I’d read the assignment and start with the first noun, Student. I write the fields (id, name, department, semester), a constructor and getters. Then I realize a Course will also need an id or code and a name, but I don’t stop to think about that yet.

Step 2: Course.java. Same idea: code, name, credits, prerequisite, capacity, constructor, getters. Now I have two plain data classes. I add a displayDetails() method to each so I can print them.

Step 3: Back to Student.java, to add completed courses. While reading the prerequisite rule, I realize a student must remember which courses they finished. I go back and add an ArrayList<String> completedCourses, plus addCompletedCourse() and hasCompleted().

Step 4: Person.java. I notice Student has id and name, and the assignment suggests a Person class. I create Person and move those two fields into it. Then I change Student to extends Person and call super(id, name). I make Person abstract with an abstract getRole(), and Student overrides it.

Step 5: Displayable.java. The assignment requires an interface, and both Student and Course have displayDetails(). I create the interface with that one method and add implements Displayable to both classes.

Step 6: Registration.java. I need something that says “this student is in this course”, so I write a small class holding one Student and one Course.

Step 7: RegistrationException.java. Before writing the rules, I need something to throw when they fail, so I create the custom exception. It’s three lines.

Step 8: RegistrationSystem.java. Now the main work. I create the HashMaps for students and courses and the ArrayList for registrations. I write these in order:

addStudent and addCourse, throwing the exception on duplicates
findStudent and findCourse
registerStudent with the six checks, one at a time
helper methods the checks need: isRegistered, countEnrolled, getRegisteredCredits
the summary method

I’d keep going back to Student and Course to add getters I forgot.

Step 9: Main.java. I build a basic menu with just “Add Student” and “Add Course”, run it, and fix mistakes. Then I add one menu option at a time. Each option reads input, calls RegistrationSystem and prints the result. Here I also add input checks for bad numbers and empty text.

Step 10: FileManager.java. The program works but forgets everything on exit, so I write saveAll first and check the CSV files look right. Then I write loadAll. In Main I call load at startup and save after each change.

Step 11: Back to Main and RegistrationSystem for the gap. Testing the prerequisite rule shows no way to mark a course completed, so I add markCompleted to the system and menu option 10. I add dropCourse and option 11 as an extra.

Step 12: Testing. I try every failure on purpose (duplicate ID, missing prerequisite, over 20 credits, full course), then restart the program to check the data reloads.

The idea behind this order is to write the simple data classes first, then the rules that use them, then the menu that calls the rules, and finally the file saving. Any mistake you find along the way, you go back and fix in the earlier file.