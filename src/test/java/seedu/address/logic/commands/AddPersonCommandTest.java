package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STARTUP;
import static seedu.address.testutil.TypicalStartups.getTypicalAddressBook;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Description;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonEmail;
import seedu.address.model.person.PersonName;
import seedu.address.model.startup.Startup;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.StartupBuilder;
import seedu.address.testutil.TypicalPersons;

class AddPersonCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_addPersonUnfilteredList_success() {
        Startup startupToEdit = model.getFilteredStartupList().get(INDEX_FIRST_STARTUP.getZeroBased());
        Person newPerson = new PersonBuilder().withName("Mike")
                .withEmail("mike@yahoo.com").withDescriptions("founder").build();
        AddPersonCommand addPersonCommand = new AddPersonCommand(INDEX_FIRST_STARTUP, newPerson);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Startup expectedStartup = new StartupBuilder(startupToEdit).withPersons(TypicalPersons.AMY, newPerson).build();
        String expectedStartupMessage = Messages.format(expectedStartup);
        String expectedMessage = String.format(AddPersonCommand.MESSAGE_SUCCESS, expectedStartupMessage);
        expectedModel.setStartup(startupToEdit, expectedStartup);

        assertCommandSuccess(addPersonCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidStartupIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredStartupList().size() + 1);
        PersonName pn = new PersonName("Mike");
        PersonEmail pe = new PersonEmail("mike@yahoo.com");
        Set<Description> des = new HashSet<>();
        des.add(new Description("founder"));
        Person newPerson = new Person(pn, pe, des);
        AddPersonCommand addPersonCommand = new AddPersonCommand(outOfBoundIndex, newPerson);

        assertCommandFailure(addPersonCommand, model, Messages.MESSAGE_INVALID_STARTUP_DISPLAYED_INDEX);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        AddPersonCommand addPersonCommand = new AddPersonCommand(INDEX_FIRST_STARTUP, TypicalPersons.AMY);

        assertThrows(CommandException.class, AddPersonCommand.MESSAGE_DUPLICATE_PERSON, () -> addPersonCommand
                .execute(model));
    }


    @Test
    public void equals() {
        Person alice = new PersonBuilder().withName("Alice")
                .withEmail("alice@gmail.com")
                .withDescriptions("CTO").build();
        Person bob = new PersonBuilder().withName("Bob")
                .withEmail("bob@gmail.com")
                .withDescriptions("founder").build();
        AddPersonCommand addAliceCommand = new AddPersonCommand(INDEX_FIRST_STARTUP, alice);
        AddPersonCommand addBobCommand = new AddPersonCommand(INDEX_FIRST_STARTUP, bob);

        // same object -> returns true
        assertTrue(addAliceCommand.equals(addAliceCommand));

        // same values -> returns true
        AddPersonCommand addAliceCommandCopy = new AddPersonCommand(INDEX_FIRST_STARTUP, alice);
        assertTrue(addAliceCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        assertFalse(addAliceCommand.equals(1));

        // null -> returns false
        assertFalse(addAliceCommand.equals(null));

        // different startup -> returns false
        assertFalse(addAliceCommand.equals(addBobCommand));
    }

    @Test
    public void toStringMethod() {
        Person bob = new PersonBuilder().withName("Bob")
                .withEmail("bob@gmail.com")
                .withDescriptions("founder").build();
        AddPersonCommand addPersonCommand = new AddPersonCommand(INDEX_FIRST_STARTUP, bob);
        String expected = AddPersonCommand.class.getCanonicalName() + "{toAdd=" + bob + "}";
        assertEquals(expected, addPersonCommand.toString());
    }
}
