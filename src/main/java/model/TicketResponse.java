package model;

import lombok.Data;

@Data
public class TicketResponse {
    private Ticket ticket;
    private String signature;
}