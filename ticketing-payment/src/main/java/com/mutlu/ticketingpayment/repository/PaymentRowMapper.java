package com.mutlu.ticketingpayment.repository;


import com.mutlu.ticketingpayment.common.PaymentType;
import com.mutlu.ticketingpayment.entity.Payment;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentRowMapper implements RowMapper<Payment> {
    @Override
    public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Payment result = new Payment();
        result.setPaymentId(rs.getLong("payment_id"));
        result.setUserId(rs.getLong("user_id"));
        result.setTripId(rs.getLong("trip_id"));
        result.setPaymentType(PaymentType.valueOf(rs.getString("payment_type")));
        result.setCardNumber(rs.getString("card_number"));
        result.setPaymentTime(rs.getTimestamp("payment_date").toLocalDateTime());
        return result;
    }
}
