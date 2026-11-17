package dev.sumantakumar.springai.tool;

import com.google.rpc.Help;
import dev.sumantakumar.springai.entity.HelpDeskTicket;
import dev.sumantakumar.springai.model.TicketRequest;
import dev.sumantakumar.springai.service.HelpDeskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import tools.jackson.core.TokenStreamFactory;

import java.util.List;

@Component
public class HelpDeskTool {

    private final static Logger LOGGER = LoggerFactory.getLogger(HelpDeskTool.class);

    @Autowired
    private HelpDeskService helpDeskService;



    @Tool(name="createHelpDeskTicket", description = "Create Help Desk issue ticket by inserting the record into database.", returnDirect = true)
    public String createHelpDeskTicket(@ToolParam(description = "Take the issue request as input")TicketRequest ticketRequest, ToolContext  toolContext) {
        String username = toolContext.getContext().getOrDefault("username", "Anonymous").toString();
        LOGGER.info("Create Help Desk issue ticket for username {}", username);
        HelpDeskTicket ticket = helpDeskService.createTicket(ticketRequest, username);
        LOGGER.info("Created Help Desk issue ticket for username {}: ticketId {}", username, ticket.getId());
        return "Ticket #"+ticket.getId()+" created successfully for user "+ticket.getUsername();
    }

    @Tool(name="getHelpDeskTicketDetails", description = "Fetch the ticket details based on a given username.")
    public List<HelpDeskTicket>  getHelpDeskTicketDetails(ToolContext  toolContext) {
        String username = toolContext.getContext().get("username").toString();
        LOGGER.info("Get Help Desk Ticket details for username {}", username);
        Assert.notNull(username, "username can't be null");
        Assert.isTrue(!username.trim().isEmpty(), "username can't be empty");
        List<HelpDeskTicket> helpDeskTickets = helpDeskService.getHelpDeskTickets(username);
        LOGGER.info("Found Help Desk Tickets for username {} ::size {}", username, helpDeskTickets.size());
        return helpDeskTickets;
    }
}













