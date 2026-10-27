/*
 * CONCEPT: INTERFACE
 * An interface is a contract: it lists method names WITHOUT bodies.
 * Any class that says "implements Displayable" MUST write its own displayDetails().
 * Student, Course and Registration all implement it, so the program can treat
 * them uniformly (that is what makes runtime polymorphism possible).
 */
public interface Displayable {

    // No body here, only the signature. Methods in an interface are public and abstract automatically.
    void displayDetails();
}
