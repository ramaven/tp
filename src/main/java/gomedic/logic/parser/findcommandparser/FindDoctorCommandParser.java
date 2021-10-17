package gomedic.logic.parser.findcommandparser;

import gomedic.commons.core.Messages;
import gomedic.logic.commands.findcommand.FindDoctorCommand;
import gomedic.logic.commands.findcommand.FindPatientCommand;
import gomedic.logic.parser.exceptions.ParseException;
import gomedic.model.util.DepartmentContainsKeywordsPredicate;
import gomedic.model.util.NameContainsKeywordsPredicate;
import gomedic.model.util.PhoneNumberContainsKeywordsPredicate;
import static gomedic.logic.parser.CliSyntax.DEPARTMENT;
import static gomedic.logic.parser.CliSyntax.NAME;
import static gomedic.logic.parser.CliSyntax.PHONE_NUMBER;


import java.util.Arrays;

/**
 * Parses input arguments and creates a new FindDoctorCommand object
 */
public class FindDoctorCommandParser {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindDoctorCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindDoctorCommand.MESSAGE_USAGE));
        }

        // First find out which field it is supposed to match to
        // then create a FindCommand with the argument containing the corresponding
        // Predicate
        String[] fieldAndArguments = trimmedArgs.split("/");
        String[] argumentKeywords = fieldAndArguments[1].split("\\s+");

        String field = fieldAndArguments[0];

        switch (field) {
            case NAME:
                return new FindDoctorCommand(new NameContainsKeywordsPredicate<>(Arrays.asList(argumentKeywords)));
            case PHONE_NUMBER:
                return new FindDoctorCommand(new PhoneNumberContainsKeywordsPredicate<>(Arrays.asList(argumentKeywords)));
            case DEPARTMENT:
                return new FindDoctorCommand(new DepartmentContainsKeywordsPredicate<>(Arrays.asList(argumentKeywords)));

            default:
                throw new ParseException(
                        String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindDoctorCommand.MESSAGE_USAGE));
        }

    }
}
