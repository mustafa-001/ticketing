package mutlu.ticketingapp.exception;

public class FieldsDoesNotMatchException extends AbstractTicketingException {
    private final String fieldName;
    public FieldsDoesNotMatchException(String fieldName) {
        super(fieldName + " fields does not match.");
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
