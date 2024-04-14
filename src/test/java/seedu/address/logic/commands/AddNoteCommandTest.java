package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STARTUP;
import static seedu.address.testutil.TypicalStartups.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.startup.Note;
import seedu.address.model.startup.Startup;
import seedu.address.testutil.StartupBuilder;


public class AddNoteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());


    @Test
    public void execute_addNoteUnfilteredList_success() {
        Startup startupToEdit = model.getFilteredStartupList().get(INDEX_FIRST_STARTUP.getZeroBased());
        Note newNote = new Note("New note content");
        AddNoteCommand addNoteCommand = new AddNoteCommand(INDEX_FIRST_STARTUP, newNote);

        Startup expectedStartup = new StartupBuilder(startupToEdit).withNotes("Innovative project ideas",
                "Looking into Series A funding", newNote.toString()).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        expectedModel.setStartup(startupToEdit, expectedStartup);
        String expectedMessage = String.format(AddNoteCommand.MESSAGE_SUCCESS, expectedStartup);

        assertCommandSuccess(addNoteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidStartupIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredStartupList().size() + 1);
        Note newNote = new Note("This note won't be added");
        AddNoteCommand addNoteCommand = new AddNoteCommand(outOfBoundIndex, newNote);

        assertCommandFailure(addNoteCommand, model, Messages.MESSAGE_INVALID_STARTUP_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        Note firstNote = new Note("First note");
        Note secondNote = new Note("Second note");
        AddNoteCommand addFirstNoteCommand = new AddNoteCommand(INDEX_FIRST_STARTUP, firstNote);
        AddNoteCommand addSecondNoteCommand = new AddNoteCommand(INDEX_FIRST_STARTUP, secondNote);

        // same object -> returns true
        assertTrue(addFirstNoteCommand.equals(addFirstNoteCommand));

        // same values -> returns true
        AddNoteCommand addFirstNoteCommandCopy = new AddNoteCommand(INDEX_FIRST_STARTUP, firstNote);
        assertTrue(addFirstNoteCommand.equals(addFirstNoteCommandCopy));

        // different types -> returns false
        assertFalse(addFirstNoteCommand.equals(1));

        // null -> returns false
        assertFalse(addFirstNoteCommand.equals(null));

        // different note -> returns false
        assertFalse(addFirstNoteCommand.equals(addSecondNoteCommand));
    }

}
