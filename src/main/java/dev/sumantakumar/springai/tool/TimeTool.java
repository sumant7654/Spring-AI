package dev.sumantakumar.springai.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class TimeTool {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeTool.class);

    @Tool(name="getCurrentLocalTime", description = "Get the current time in the user's timezone.")
    public String getCurrentLocalTime() {
        return timeSupplier.get();
    }

    @Tool(name="getCurrentZonedTime", description = "Get the current time in the specified timezone.")
    public String getCurrentZonedTime(@ToolParam(description = "Value representing the timezone.") String timeZone) {
        return currentZonedTime.apply(timeZone);
    }

    Function<String, String> currentZonedTime = (zone) -> {
        LOGGER.info("Returning the current time in the specified timezone.");
        LocalDateTime now = LocalDateTime.now(ZoneId.of(zone));
        return now.toString();
    };

    Supplier<String> timeSupplier = () -> {
        LOGGER.info("Current time is {}", LocalDateTime.now());
        return String.valueOf(LocalDateTime.now());
    };
}
