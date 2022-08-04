package com.mutlu.ticketingemailandsms.dto;


public record GetUserDto(Long userId, UserType userType, String email, String phoneNumber, String firstName, String lastName) {
}
