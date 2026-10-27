/*
 * CONCEPTS: ABSTRACT CLASS, ENCAPSULATION, CONSTRUCTOR
 * An abstract class cannot be instantiated (you can't write new Person(...)).
 * It exists only to hold what is COMMON to its children (id and name) and to
 * force children to implement abstract methods (getRole).
 */
public abstract class Person {

    // ENCAPSULATION: fields are private, so outside code cannot change them directly.
    // Access happens only through the getters/setters below, which can validate data.
    private String id;
    private String name;

    // CONSTRUCTOR: runs when an object is created; sets the starting state.
    // Child classes call it using super(id, name).
    public Person(String id, String name) {
        this.id = id;       // "this.id" is the field; "id" on the right is the parameter
        this.name = name;
    }

    // GETTERS: allow read-only access to the private fields.
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // SETTER: allows controlled change. We refuse empty names (simple validation).
    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    // ABSTRACT METHOD: no body. Every non-abstract child MUST override it.
    // This is what we use later to demonstrate RUNTIME POLYMORPHISM.
    public abstract String getRole();

    // METHOD OVERRIDING of Object.toString(): every class inherits toString from Object;
    // here we replace it with our own version.
    @Override
    public String toString() {
        return getRole() + " [" + id + "] " + name;
    }
}
