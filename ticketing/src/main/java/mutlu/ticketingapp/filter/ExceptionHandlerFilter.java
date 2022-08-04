//package mutlu.ticketingapp.filter;
//
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import mutlu.ticketingapp.exception.AbstractTicketingException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//
//@Component
//public class ExceptionHandlerFilter extends OncePerRequestFilter {
//
//    Logger log = LoggerFactory.getLogger(this.getClass());
//    @Override
//    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        try {
//            log.info("Processing request: {}", request);
//            filterChain.doFilter(request, response);
//        } catch (RuntimeException ex) {
//
//            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
//            ex.getStackTrace().
//            response.getWriter().write(ex.getMessage());
//        }
//    }
//
//    public String convertObjectToJson(Object object) throws JsonProcessingException {
//        if (object == null) {
//            return null;
//        }
//        ObjectMapper mapper = new ObjectMapper();
//        return mapper.writeValueAsString(object);
//    }
//}