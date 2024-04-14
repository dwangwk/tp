package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.startup.Note;
import seedu.address.model.startup.Startup;

/**
 * Edits a Note of a startup in the address book!
 */
public class EditNoteCommand extends Command {
    public static final String COMMAND_WORD = "editnote";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits a note of the startup identified "
            + "by the index number used in the displayed startup list. "
            + "Parameters: INDEX (must be a positive integer) noteIndex (must be a positive integer) NOTE\n"
            + "Example: " + COMMAND_WORD + " 1 1 Updated note";

    public static final String MESSAGE_SUCCESS = "Note edited for Startup: %1$s";

    private final Index index;
    private final int noteIndex;
    private final Note newNote;

    /**
     * @param index of the startup in the filtered startup list to edit
     * @param noteIndex of the note to be edited
     * @param newNote new note object to be stored in the notes list
     */

    public EditNoteCommand(Index index, int noteIndex, Note newNote) {
        requireNonNull(index);
        requireNonNull(newNote);
        this.index = index;
        this.noteIndex = noteIndex - 1; // User input is 1-based, internal storage is 0-based.
        this.newNote = newNote;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Startup> lastShownList = model.getFilteredStartupList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STARTUP_DISPLAYED_INDEX);
        }

        Startup startupToEdit = lastShownList.get(index.getZeroBased());
        if (noteIndex >= startupToEdit.getNotes().size()) {
            throw new CommandException("The note index provided is invalid.");
        }

        ArrayList<Note> updatedNotes = new ArrayList<>(startupToEdit.getNotes());
        updatedNotes.set(noteIndex, newNote);
        Startup editedStartup = new Startup(
                startupToEdit.getName(),
                startupToEdit.getFundingStage(),
                startupToEdit.getIndustry(),
                startupToEdit.getPhone(),
                startupToEdit.getEmail(),
                startupToEdit.getAddress(),
                startupToEdit.getValuation(),
                startupToEdit.getTags(),
                updatedNotes,
                startupToEdit.getPersons()
        );

        model.setStartup(startupToEdit, editedStartup);
        return new CommandResult(String.format(MESSAGE_SUCCESS, editedStartup));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) { // same object instance
            return true;
        }

        if (!(other instanceof EditNoteCommand)) { // not the same type
            return false;
        }

        EditNoteCommand e = (EditNoteCommand) other;
        return index.equals(e.index)
                && noteIndex == e.noteIndex
                && newNote.equals(e.newNote);
    }
}

