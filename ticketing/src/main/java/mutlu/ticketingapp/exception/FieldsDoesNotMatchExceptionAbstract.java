package mutlu.ticketingapp.exception;

public class FieldsDoesNotMatchExceptionAbstract extends AbstractTicketingException {
    private String fieldName;
    public FieldsDoesNotMatchExceptionAbstract(String fieldName) {
        super(fieldName + " fields does not match.");
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
