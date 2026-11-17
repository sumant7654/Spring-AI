package dev.sumantakumar.springai.service;

import dev.sumantakumar.springai.entity.HelpDeskTicket;
import dev.sumantakumar.springai.model.TicketRequest;

import java.util.List;

public interface HelpDeskService {
    HelpDeskTicket createTicket(TicketRequest ticketRequest, String username);
    List<HelpDeskTicket> getHelpDeskTickets(String username);
}
