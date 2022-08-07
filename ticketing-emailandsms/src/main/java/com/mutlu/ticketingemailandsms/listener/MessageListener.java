package com.mutlu.ticketingemailandsms.listener;

import com.mutlu.ticketingemailandsms.dto.RegistrationEmailDto;
import com.mutlu.ticketingemailandsms.dto.TicketInformationMessageDto;
import com.mutlu.ticketingemailandsms.entity.Email;
import com.mutlu.ticketingemailandsms.entity.Message;
import com.mutlu.ticketingemailandsms.entity.SMS;
import com.mutlu.ticketingemailandsms.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;


@Service
public class MessageListener {
    private final Logger log = LoggerFactory.getLogger(MessageListener.class);
    private MessageRepository messageRepository;

    @Autowired
    public MessageListener(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }


    /**
     * Listens on queue named ticketing email and saves an email with given details to database.
     */
    @RabbitListener(queues = "ticketing.email")
    public void emailListener(RegistrationEmailDto emailDto) {
        log.info("Got a request for sending an email, {}", emailDto);
        Message email = new Email();
        email.setMessage("Sayın " + emailDto.firstName() + " " +
                emailDto.lastName() + " bilet uygulamasına kaydoldunuz.");
        email.setReceiver(emailDto.email());
        sendMessage(email);
    }


    /**
     * Listens on queue named ticketing sms and saves an sms with given details to database.
     */
    @RabbitListener(queues =  "ticketing.sms")
    public void smsListener(TicketInformationMessageDto ticketInformationMessageDto) {
        log.info("Got request for sending an SMS, {}", ticketInformationMessageDto);
        Message sms = new Email();
        sms.setMessage("Sayın " +
                ticketInformationMessageDto.userDto().firstName() +
                " " +
                ticketInformationMessageDto.userDto().lastName() +
                " " +
                ticketInformationMessageDto.numberOfTickets() +
                " yeni bilet satın aldınız. " +
                "Sefer bigileriniz: " +
                ticketInformationMessageDto.tripDto().departureStation() + "-" +
                ticketInformationMessageDto.tripDto().arrivalStation() + " " +
                ticketInformationMessageDto.tripDto().departureTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))

        );
        sms.setReceiver(ticketInformationMessageDto.userDto().email());
        sendMessage(sms);
    }

    private void sendMessage(Message message) {
        messageRepository.save(message);
        if (message instanceof Email) {
            log.info("Sending email {} to {}", message.getMessage(), message.getReceiver());
        } else if (message instanceof SMS) {
            log.info("Sending SMS {} to {}", message.getMessage(), message.getReceiver());

        }
    }

}
