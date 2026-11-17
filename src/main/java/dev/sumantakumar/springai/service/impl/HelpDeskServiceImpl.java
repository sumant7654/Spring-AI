package dev.sumantakumar.springai.service.impl;

import dev.sumantakumar.springai.entity.HelpDeskTicket;
import dev.sumantakumar.springai.model.TicketRequest;
import dev.sumantakumar.springai.repository.HelpDeskTicketRepository;
import dev.sumantakumar.springai.service.HelpDeskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HelpDeskServiceImpl implements HelpDeskService {

    private HelpDeskTicketRepository  helpDeskTicketRepository;

    @Autowired
    public void setHelpDeskTicketRepository(HelpDeskTicketRepository helpDeskTicketRepository) {
        this.helpDeskTicketRepository = helpDeskTicketRepository;
    }
    @Override
    public HelpDeskTicket createTicket(TicketRequest ticketRequest, String username) {
        HelpDeskTicket ticket = HelpDeskTicket.builder()
                .issue(ticketRequest.issue())
                .username(username)
                .status("OPEN")
                .createdAt(LocalDateTime.now())
                .eta(LocalDateTime.now().plusDays(7))
                .build();
        return helpDeskTicketRepository.save(ticket);
    }

    @Override
    public List<HelpDeskTicket> getHelpDeskTickets(String username) {
        return helpDeskTicketRepository.findByUsername(username);
    }
}
