package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;
import seedu.address.model.startup.Startup;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_STARTUP_DISPLAYED_INDEX = "The startup index provided is invalid";
    public static final String MESSAGE_STARTUPS_LISTED_OVERVIEW = "%1$d startups listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code startup} for display to the user.
     */
    public static String format(Startup startup) {
        final StringBuilder builder = new StringBuilder();
        builder.append(startup.getName())
                .append("; Industry: ")
                .append(startup.getIndustry())
                .append("; Funding Stage: ")
                .append(startup.getFundingStage())
                .append("; Phone: ")
                .append(startup.getPhone())
                .append("; Email: ")
                .append(startup.getEmail())
                .append("; Address: ")
                .append(startup.getAddress())
                .append("; Valuation: ")
                .append(startup.getValuation())
                .append("; Notes: ");

        if (startup.getNotes().isEmpty()) {
            builder.append("No notes added.");
        } else {
            startup.getNotes().forEach(note ->
                    builder.append(note.toString())
                            .append(" ")); // Add a space after each note for readability
        }

        builder.append("; Tags: ");
        startup.getTags().forEach(tag -> builder.append(tag).append(" ")); // Assuming Tag has a sensible toString()

        return builder.toString().trim(); // Trim to remove any trailing spaces
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Descriptions: ");

        if (person.getDescriptions().isEmpty()) {
            builder.append("No descriptions added.");
        } else {
            person.getDescriptions().forEach(description ->
                    builder.append(description.toString())
                            .append(System.lineSeparator())); // Each description on a new line for readability
        }

        return builder.toString().trim(); // Trim to remove any trailing spaces
    }


}
