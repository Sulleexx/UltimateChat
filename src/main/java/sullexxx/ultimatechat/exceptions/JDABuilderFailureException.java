package sullexxx.ultimatechat.exceptions;

public class JDABuilderFailureException extends RuntimeException {
    public JDABuilderFailureException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "-------------JDAFailure-------------\n" + getMessage()
                + "\n-------------JDAFailure-------------\nStacktrace |- ";
    }
}
