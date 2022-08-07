package mutlu.ticketing_admin.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExcepitonHandler {

    Logger log = LoggerFactory.getLogger(GlobalExcepitonHandler.class);

    @ExceptionHandler(AbstractTicketingException.class)
    public ResponseEntity<String> handle(AbstractTicketingException exception) {
        log.error("An exception occurred: {}", exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handle(IllegalArgumentException exception) {
        log.error("An exception occurred: {}", exception.getMessage());
        return new ResponseEntity<>("Yanlış parametreler ile endpoint çağrıldı." +
                exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handle(HttpMessageNotReadableException exception) {
        log.error("An exception occurred: {}", exception.getMessage());
        return new ResponseEntity<>("JSON girdisi işlenemedi. \n" +
                exception.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception exception) {
        log.error("An exception occurred: {}", exception.getMessage());
        return new ResponseEntity<>("Birşeyler ters gitti. \n", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
