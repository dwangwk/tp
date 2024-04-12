package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STARTUP;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_STARTUP;
import static seedu.address.testutil.TypicalStartups.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.startup.Startup;
import seedu.address.testutil.StartupBuilder;

public class DeletePersonCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_deletePersonUnfilteredList_success() {
        DeletePersonCommand deletePersonCommand = new DeletePersonCommand(INDEX_FIRST_STARTUP, 1);
        Startup startupToEdit = model.getFilteredStartupList().get(INDEX_FIRST_STARTUP.getZeroBased());
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Startup expectedStartup = new StartupBuilder(startupToEdit).withPersons().build();
        String expectedMessage = String.format(DeletePersonCommand.MESSAGE_SUCCESS, Messages.format(expectedStartup));
        expectedModel.setStartup(startupToEdit, expectedStartup);

        assertCommandSuccess(deletePersonCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidStartupIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredStartupList().size() + 1);
        DeletePersonCommand deletePersonCommand = new DeletePersonCommand(outOfBoundIndex, 1);

        assertCommandFailure(deletePersonCommand, model, Messages.MESSAGE_INVALID_STARTUP_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidPersonIndex_throwsCommandException() {
        int invalidPersonIndex = model.getFilteredStartupList().get(INDEX_FIRST_STARTUP.getZeroBased())
                .getPersons().size() + 1;
        DeletePersonCommand deletePersonCommand = new DeletePersonCommand(INDEX_FIRST_STARTUP, invalidPersonIndex);

        assertCommandFailure(deletePersonCommand, model, "The person index provided is invalid.");
    }

    @Test
    public void execute_emptyPersonsList_throwsCommandException() {
        DeletePersonCommand deletePersonCommand = new DeletePersonCommand(INDEX_THIRD_STARTUP, 1);

        assertCommandFailure(deletePersonCommand, model, "The person index provided is invalid.");
    }


}
