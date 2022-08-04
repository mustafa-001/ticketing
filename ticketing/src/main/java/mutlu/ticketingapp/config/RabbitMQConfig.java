package mutlu.ticketingapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.email.queue}")
    private String emailQueueName;
    @Value("${rabbitmq.sms.queue}")
    private String smsQueueName;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Bean
    public Queue emailQueue() {
        return new Queue(emailQueueName, false);
    }

    @Bean
    public Queue smsQueue() {
        return new Queue(smsQueueName, false);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange exchange) {
        return BindingBuilder.bind(emailQueue).to(exchange).with(emailQueueName);
    }

    @Bean
    public Binding smsBinding(Queue smsQueue, DirectExchange exchange) {
        return BindingBuilder.bind(smsQueue).to(exchange).with(smsQueueName);
    }

    public MessageConverter jsonMessageConverter(ObjectMapper springInternalObjectMapper) {
        springInternalObjectMapper.findAndRegisterModules();
        return new Jackson2JsonMessageConverter(springInternalObjectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter(new ObjectMapper()));
        return rabbitTemplate;
    }
}


