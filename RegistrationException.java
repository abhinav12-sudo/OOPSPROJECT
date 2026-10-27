/*
 * CONCEPT: USER-DEFINED (CUSTOM) EXCEPTION
 * We extend Exception, so this is a CHECKED exception: any method that may throw it
 * must either catch it (try/catch) or declare it with "throws".
 * This forces the programmer to handle invalid registrations explicitly.
 */
public class RegistrationException extends Exception {

    // The constructor passes our message up to the parent Exception class.
    // super(message) stores it so that e.getMessage() can return it later.
    public RegistrationException(String message) {
        super(message);
    }
}
