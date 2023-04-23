package sd2223.trab1.api.soap;

public class FeedsException extends Exception {


    public FeedsException() {
        super("");
    }

    public FeedsException(String errorMessage) {
        super(errorMessage);
    }

    private static final long serialVersionUID = 1L;
}
