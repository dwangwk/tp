package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STARTUP;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_STARTUP;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_STARTUP;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.CommandTestUtil;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditStartupDescriptor;
import seedu.address.model.startup.Address;
import seedu.address.model.startup.Email;
import seedu.address.model.startup.FundingStage;
import seedu.address.model.startup.Industry;
import seedu.address.model.startup.Name;
import seedu.address.model.startup.Phone;
import seedu.address.model.startup.Valuation;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditStartupDescriptorBuilder;

public class EditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, CommandTestUtil.VALID_NAME_A, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + CommandTestUtil.NAME_DESC_A, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + CommandTestUtil.NAME_DESC_A, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 x/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1"
            + CommandTestUtil.INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "1"
            + CommandTestUtil.INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "1"
            + CommandTestUtil.INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, "1"
            + CommandTestUtil.INVALID_ADDRESS_DESC, Address.MESSAGE_CONSTRAINTS); // invalid address
        assertParseFailure(parser, "1"
            + CommandTestUtil.INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS); // invalid tag
        assertParseFailure(parser, "1"
            + CommandTestUtil.INVALID_INDUSTRY_DESC, Industry.MESSAGE_CONSTRAINTS); // invalid industry
        assertParseFailure(parser, "1"
            + CommandTestUtil.INVALID_FUNDING_DESC, FundingStage.MESSAGE_CONSTRAINTS); // invalid funding stage
        assertParseFailure(parser, "1"
            + CommandTestUtil.INVALID_VALUATION_DESC, Valuation.MESSAGE_CONSTRAINTS); // invalid valuation

        // invalid phone followed by valid email
        assertParseFailure(parser, "1"
            + CommandTestUtil.INVALID_PHONE_DESC
            + CommandTestUtil.EMAIL_DESC_A, Phone.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Startup} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, "1"
            + CommandTestUtil.TAG_DESC_POTENTIAL + CommandTestUtil.TAG_DESC_NEW
            + TAG_EMPTY, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + CommandTestUtil.TAG_DESC_POTENTIAL
            + TAG_EMPTY + CommandTestUtil.TAG_DESC_NEW, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_EMPTY
            + CommandTestUtil.TAG_DESC_POTENTIAL
            + CommandTestUtil.TAG_DESC_NEW, Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "1"
                + CommandTestUtil.INVALID_NAME_DESC + CommandTestUtil.INVALID_EMAIL_DESC
                + CommandTestUtil.VALID_ADDRESS_A + CommandTestUtil.VALID_PHONE_A,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_STARTUP;
        String userInput = targetIndex.getOneBased() + CommandTestUtil.PHONE_DESC_B
                + CommandTestUtil.TAG_DESC_NEW
                + CommandTestUtil.EMAIL_DESC_A + CommandTestUtil.ADDRESS_DESC_A
                + CommandTestUtil.NAME_DESC_A + CommandTestUtil.TAG_DESC_POTENTIAL;

        EditStartupDescriptor descriptor = new EditStartupDescriptorBuilder().withName(CommandTestUtil.VALID_NAME_A)
                .withPhone(CommandTestUtil.VALID_PHONE_B).withEmail(
                    CommandTestUtil.VALID_EMAIL_A).withAddress(CommandTestUtil.VALID_ADDRESS_A)
                .withTags(CommandTestUtil.VALID_TAG_NEW, CommandTestUtil.VALID_TAG_POTENTIAL).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_STARTUP;
        String userInput = targetIndex.getOneBased() + CommandTestUtil.PHONE_DESC_B + CommandTestUtil.EMAIL_DESC_A;

        EditStartupDescriptor descriptor = new EditStartupDescriptorBuilder().withPhone(CommandTestUtil.VALID_PHONE_B)
                .withEmail(CommandTestUtil.VALID_EMAIL_A).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_STARTUP;
        String userInput = targetIndex.getOneBased() + CommandTestUtil.NAME_DESC_A;
        EditStartupDescriptor descriptor = new EditStartupDescriptorBuilder().withName(
            CommandTestUtil.VALID_NAME_A).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = targetIndex.getOneBased() + CommandTestUtil.PHONE_DESC_A;
        descriptor = new EditStartupDescriptorBuilder().withPhone(
            CommandTestUtil.VALID_PHONE_A).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = targetIndex.getOneBased() + CommandTestUtil.EMAIL_DESC_A;
        descriptor = new EditStartupDescriptorBuilder().withEmail(
            CommandTestUtil.VALID_EMAIL_A).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // address
        userInput = targetIndex.getOneBased() + CommandTestUtil.ADDRESS_DESC_A;
        descriptor = new EditStartupDescriptorBuilder().withAddress(
            CommandTestUtil.VALID_ADDRESS_A).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // industry
        userInput = targetIndex.getOneBased() + CommandTestUtil.INDUSTRY_DESC_A;
        descriptor = new EditStartupDescriptorBuilder().withIndustry(
            CommandTestUtil.VALID_INDUSTRY_A).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // funding stage
        userInput = targetIndex.getOneBased() + CommandTestUtil.FUNDING_DESC_A;
        descriptor = new EditStartupDescriptorBuilder().withFundingStage(
            CommandTestUtil.VALID_FUNDING_A).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // valuation
        userInput = targetIndex.getOneBased() + CommandTestUtil.VALUATION_DESC_A;
        descriptor = new EditStartupDescriptorBuilder().withValuation(
          CommandTestUtil.VALID_VALUATION_A).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = targetIndex.getOneBased() + CommandTestUtil.TAG_DESC_POTENTIAL;
        descriptor = new EditStartupDescriptorBuilder().withTags(
            CommandTestUtil.VALID_TAG_POTENTIAL).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedNonTagValue_failure()

        // valid followed by invalid
        Index targetIndex = INDEX_FIRST_STARTUP;
        String userInput = targetIndex.getOneBased()
            + CommandTestUtil.INVALID_PHONE_DESC
            + CommandTestUtil.PHONE_DESC_B;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid followed by valid
        userInput = targetIndex.getOneBased() + CommandTestUtil.PHONE_DESC_B
            + CommandTestUtil.INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // mulltiple valid fields repeated
        userInput = targetIndex.getOneBased() + CommandTestUtil.PHONE_DESC_A
                + CommandTestUtil.ADDRESS_DESC_A + CommandTestUtil.EMAIL_DESC_A
                + CommandTestUtil.TAG_DESC_POTENTIAL + CommandTestUtil.PHONE_DESC_A
                + CommandTestUtil.ADDRESS_DESC_A + CommandTestUtil.EMAIL_DESC_A
                + CommandTestUtil.TAG_DESC_POTENTIAL
                + CommandTestUtil.PHONE_DESC_B + CommandTestUtil.ADDRESS_DESC_B
                + CommandTestUtil.EMAIL_DESC_B + CommandTestUtil.TAG_DESC_NEW;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));

        // multiple invalid values
        userInput = targetIndex.getOneBased() + CommandTestUtil.INVALID_PHONE_DESC
                + CommandTestUtil.INVALID_ADDRESS_DESC + CommandTestUtil.INVALID_EMAIL_DESC
                + CommandTestUtil.INVALID_PHONE_DESC + CommandTestUtil.INVALID_ADDRESS_DESC
                + CommandTestUtil.INVALID_EMAIL_DESC;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));
    }

    @Test
    public void parse_resetTags_success() {
        Index targetIndex = INDEX_THIRD_STARTUP;
        String userInput = targetIndex.getOneBased() + TAG_EMPTY;

        EditStartupDescriptor descriptor = new EditStartupDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }
}
