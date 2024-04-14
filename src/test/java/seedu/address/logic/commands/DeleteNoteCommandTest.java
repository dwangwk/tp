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




public class DeleteNoteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_deleteNoteUnfilteredList_success() {
        DeleteNoteCommand deleteNoteCommand = new DeleteNoteCommand(INDEX_FIRST_STARTUP, 1);
        Startup startupToEdit = model.getFilteredStartupList().get(INDEX_FIRST_STARTUP.getZeroBased());
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Startup expectedStartup = new StartupBuilder(startupToEdit).withNotes("Looking into Series A funding").build();
        String expectedMessage = String.format(DeleteNoteCommand.MESSAGE_SUCCESS, expectedStartup);
        expectedModel.setStartup(startupToEdit, expectedStartup);
        assertCommandSuccess(deleteNoteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidStartupIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredStartupList().size() + 1);
        DeleteNoteCommand deleteNoteCommand = new DeleteNoteCommand(outOfBoundIndex, 1);

        assertCommandFailure(deleteNoteCommand, model, Messages.MESSAGE_INVALID_STARTUP_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidNoteIndex_throwsCommandException() {
        int invalidNoteIndex = model.getFilteredStartupList().get(INDEX_FIRST_STARTUP.getZeroBased())
                .getNotes().size() + 1;
        DeleteNoteCommand deleteNoteCommand = new DeleteNoteCommand(INDEX_FIRST_STARTUP, invalidNoteIndex);

        assertCommandFailure(deleteNoteCommand, model, "The note index provided is invalid.");
    }

    @Test
    public void execute_emptyNotesList_throwsCommandException() {
        DeleteNoteCommand deleteNoteCommand = new DeleteNoteCommand(INDEX_THIRD_STARTUP, 1);

        assertCommandFailure(deleteNoteCommand, model, "The note index provided is invalid.");
    }


}
