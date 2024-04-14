package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STARTUP;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_STARTUP;
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

public class EditNoteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_editNoteUnfilteredList_success() {
        Startup startupToEdit = model.getFilteredStartupList().get(INDEX_FIRST_STARTUP.getZeroBased());
        Note editedNote = new Note("Edited note content");
        EditNoteCommand editNoteCommand = new EditNoteCommand(INDEX_FIRST_STARTUP, 1, editedNote);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Startup expectedStartup = new StartupBuilder(startupToEdit).withNotes(editedNote.toString(),
                "Looking into Series A funding").build();
        expectedModel.setStartup(startupToEdit, expectedStartup);
        String expectedMessage = String.format(EditNoteCommand.MESSAGE_SUCCESS, expectedStartup);

        assertCommandSuccess(editNoteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidNoteIndexUnfilteredList_failure() {
        Index index = Index.fromOneBased(1); // Assuming the first startup has at least one note
        int invalidNoteIndex = 99; // Assuming no startup has this many notes
        Note editedNote = new Note("Edited note content");
        EditNoteCommand editNoteCommand = new EditNoteCommand(index, invalidNoteIndex, editedNote);

        assertCommandFailure(editNoteCommand, model, "The note index provided is invalid.");
    }

    @Test
    public void execute_invalidStartupIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredStartupList().size() + 1);
        Note editedNote = new Note("Edited note content");
        EditNoteCommand editNoteCommand = new EditNoteCommand(outOfBoundIndex, 1, editedNote);

        assertCommandFailure(editNoteCommand, model, Messages.MESSAGE_INVALID_STARTUP_DISPLAYED_INDEX);
    }

    @Test
    public void execute_editNoteInEmptyStartupList_failure() {
        model.updateFilteredStartupList(p -> false); // No startup will match this predicate -> empty list
        Note editedNote = new Note("Edited note content");
        EditNoteCommand editNoteCommand = new EditNoteCommand(INDEX_FIRST_STARTUP, 1, editedNote);

        assertCommandFailure(editNoteCommand, model, Messages.MESSAGE_INVALID_STARTUP_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditNoteCommand standardCommand = new EditNoteCommand(INDEX_FIRST_STARTUP, 1, new Note("Note"));

        // same values -> returns true
        EditNoteCommand commandWithSameValues = new EditNoteCommand(INDEX_FIRST_STARTUP, 1, new Note("Note"));
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditNoteCommand(INDEX_SECOND_STARTUP, 1, new Note("Note"))));

        // different noteIndex -> returns false
        assertFalse(standardCommand.equals(new EditNoteCommand(INDEX_FIRST_STARTUP, 2, new Note("Note"))));

        // different Note -> returns false
        assertFalse(standardCommand.equals(new EditNoteCommand(INDEX_FIRST_STARTUP, 1, new Note("Another note"))));
    }

}

