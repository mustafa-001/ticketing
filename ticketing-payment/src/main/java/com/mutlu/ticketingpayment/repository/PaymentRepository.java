package com.mutlu.ticketingpayment.repository;


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Optional;

import com.mutlu.ticketingpayment.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public  class PaymentRepository  {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insert;

    @Autowired
    public PaymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.insert = new SimpleJdbcInsert(jdbcTemplate);
        this.insert.setTableName("payment");
        this.insert.usingGeneratedKeyColumns("payment_id");
    }

    /**
     * @param payment A payment request to be saved to database.
     * @return Given payment object with its auto generated paymentId field set.
     */
    public Payment save(Payment payment) {
        // jdbcTemplate.update("INSERT INTO payment(user_id, payment_type) values (?,
        // ?)", paymentDto.getUserId(), paymentDto.getPaymentType().ordinal());
        var parameters = new HashMap<String, Object>();
        parameters.put("user_id", payment.getUserId());
        parameters.put("payment_type", payment.getPaymentType().toString());
        parameters.put("card_number", payment.getCardNumber());
        parameters.put("payment_date", Timestamp.valueOf(payment.getPaymentTime()));

        Number id = insert.executeAndReturnKey(parameters);
        return findById(id).orElseThrow(() -> new IllegalStateException(""));
    }

    public Optional<Payment> findById(Number id) {
        Payment payment = jdbcTemplate.queryForObject("select * from payment where payment_id = ?", new PaymentRowMapper(),
                id);
        if (payment == null) {
            return Optional.empty();
        } else {
            return Optional.of(payment);
        }
    }
}
