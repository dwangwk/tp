---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# CapitalConnect Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `StartupListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Startup` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a startup).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Startup` objects (which are contained in a `UniqueStartupList` object).
* stores the currently 'selected' `Startup` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Startup>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Startup` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Startup` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.addressbook.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Find Commands
The find commands allows users to find the startups with matching names, funding stages, or industries.
There are three find commands in CapitalConnect: `find n/`, `find i/`, `find f`. The implementation of these three commands are similar. Here we use the `find n/` command to illustrate how they are executed.

#### Find by Name
The `find n/` function allows users to find the startups that contain the names that users are interested in.

This function will display startup cards that have the same name as users provide. The startup card contains information other than the startup names, such as addresses, emails, phone numbers, etc..

To find the wanted startup, a `NamesContainsKeywordsPredicate` is created to test whether a startup has a name that matches the user's input keywords. Similarly, `find f/` and `find i/` will create `FundingStageContainsKeywordsPredicate` and `InudstryContainsKeywordsPredicate` respectively. These commands only change the displayed list of startups, stored as `filteredStartups` in `Model`, without affecting the data stored in CapitalConnect.

A typical program flow is as follows:

1. User enters a command to find startups by names, e.g. `find n/Apple`.
2. The input is passed to the `AddressbookParser` class which calls `FindCommandParser`. `FindCommandParser` attempts to parse the flags present, and in this case is `n/`. Note that `FindCommandParser` does not check invalid inputs like partial keywords. `FindCommandParser` will only throw an exception if the keyword is empty.
3. If the parse is successful, a `NamesContainsKeywordsPredicate` is created to find the startups that contain the name `Apple`.
4. A new `FindCommand` is created from the predicate and passed back to `LogicManager`.
5. The command is executed. The `filteredStartups` is updated with the predicate passed into the command.
6. The command result is created and passed back to the `LogicManager`

The following sequence diagram illustrates the execution process of a find by name command.

<puml src="diagrams/FindByName.puml" alt="FindByName" />

### Add Person Command
The add person command allows users to add key employees' information into a startup.

The key employee information will be displayed in the people pane, next to the startup card view. Through this function, users can keep track of employees' name, email, and other related information.

A typical program flow is as follows:
1. User enters a command to add a key employee to the first startup, e.g. `add-p 1 pn/John pe/johndoe@example.com pd/founder`.
2. The input is passed to the `AddressbookParser` class which calls `AdderPersonCommandParser`, and then the `AddPersonCommandParser` parses the keywords from the user's input.
3. The `AddPersonCommandParser` checks whether all required fields are entered and whether the index is valid. If all checks are passed, the program will move onto `AddPersonCommand`.
4. If the key employee does not exist in the startup, the startup's employee information will then be updated to include the new person.

The following sequence diagram illustrates the process of execution of an add person command.

<puml src="diagrams/AddPerson.puml" alt="AddPerson" />

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th startup in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/CapitalConnect …​` to add a new startup. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the startup was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the startup being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope


**Target user profile**: A venture capital portfolio manager who

* has a need to keep a significant amount of startup investments
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Venture capital firms manage diverse portfolios of startup
investments across various industries. The app streamlines the management of startup
investments, enabling VC firms to easily add, categorize, and track a diverse
portfolio of investments in various industries and funding stages.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority  | As a …​           | I want to …​                                  | So that I can…​                                                                               |
|-----------|-------------------|-----------------------------------------------|-----------------------------------------------------------------------------------------------|
| `* * *`   | new user          | see usage instructions                        | refer to instructions when I forget how to use the App                                        |
| `* * *`   | user              | view the startup investments in my portfolio  | see the list of startup investments that I'm interested in                                    |
| `* * *`   | user              | add a new startup investment to my portfolio  | save the details of the new startup investment                                                |
| `* * *`   | user              | delete a startup investment to my portfolio   | remove the startup investment that I am no longer interested in                               |
| `* *`     | user              | find a startup investment by names            | locate a startup investment by its name without having to go through the entire list          |
| `* *`     | user              | find a startup investment by industries       | locate a startup investment by its industry without having to go through the entire list      |
| `* *`     | user              | find a startup investment by funding stages   | locate a startup investment by its funding stage without having to go through the entire list |
| `* *`     | user              | edit a startup investment in my portfolio     | update a startup information in my portfolio                                                  |
| `* *`     | intermediate user | add a note to the startups I'm interested in  | know more about the startup investment when checking it through the app                       |
| `* *`     | intermediate user | edit a note to the startups I'm interested in | update the startup investment in the app                                                      |
| `* *`     | intermediate user | delete a note to the startups                 | get rid off redundant information                                                             |
| `* *`     | intermediate user | add key employee's information to startups    | know more about the startup through its people                                                |
| `* *`     | intermediate user | edit key employee's information to startups   | update the startups' employees' information                                                   |
| `* *`     | intermediate user | delete key employee's information to startups | remove redundant or outdated information                                                      |




### Use cases

(For all use cases below, the **System** is the `AddressBook` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Add a startup investment**

**MSS**

1.  User requests to add a new startup investment to their portfolio.
2.  CapitalConnect dashboard prompts the user to provide details including:
    * Startup name
    * Industry
    * Funding stage
    * Address
    * Contact information
    * Valuation of Startup
3.  User provides the necessary details.
4.  CapitalConnect verifies the input for validity.
5.  CapitalConnect adds the new startup's profile to the user's portfolio in the dashboard.

    Use case ends.

**Extensions**

* 4a. Invalid input or missing parameters.

    * 4a1. CapitalConnect shows an error message.

      Use case resumes at step 2.

* 4b. Startup name already exists in the portfolio.

    * 4b1. CapitalConnect notifies the user about the duplicate entry.

      Use case resumes at step 2.


**Use case: Search for startup investments by industry & funding stage**

**MSS**

1.  User requests to search for startup investments by either industry and funding stage.
2.  CapitalConnect dashboard prompts the user to input the industry and funding stage.
3.  User provides the industry and funding stage.
4.  CapitalConnect verifies the input for validity.
5.  CapitalConnect searches for startup investments matching the specified industry and funding stage in the user's portfolio.
6.  CapitalConnect displays the startup investments matching the search criteria.

    Use case ends.

**Extensions**

* 4a. Invalid input or missing parameters.

    * 4a1. CapitalConnect shows an error message.

      Use case resumes at step 2.

* 6a. No startup investments match the search criteria.

    * 6a1. CapitalConnect shows an error message indicating no matches found.

      Use case ends.


**Use case: Search for startup investments by name**

**MSS**

1.  User requests to search for startup investments by name.
2.  CapitalConnect dashboard prompts the user to input the name of the startup.
3.  User provides the name of the startup.
4.  CapitalConnect verifies the input for validity.
5.  CapitalConnect searches for startup investments matching the specified name in the user's portfolio.
6.  CapitalConnect displays the startup investments matching the search criteria.

    Use case ends.

**Extensions**

* 4a. Invalid input or missing parameters.

    * 4a1. CapitalConnect shows an error message.

      Use case resumes at step 2.

* 6a. No startup investments match the provided name.

    * 6a1. CapitalConnect shows an error message indicating no matches found.

      Use case ends.


**Use case: Save the current state of CapitalConnect dashboard**

**MSS**

1.  CapitalConnect automatically saves the state of the dashboard after every add and delete operation.

    Use case ends.

**Extensions**

* 1a. Issue with saving the dashboard state.

    * 1a1. CapitalConnect shows an error message indicating the issue with saving the dashboard state.

      Use case ends.


**Use case: Delete a startup investment from the portfolio**

**MSS**

1.  User requests to delete a specific startup investment from their portfolio.
2.  CapitalConnect dashboard prompts the user to input the index of the startup investment to be deleted.
3.  User provides the index of the startup investment.
4.  CapitalConnect verifies the input for validity.
5.  CapitalConnect deletes the startup investment at the specified index from the user's portfolio.
6.  CapitalConnect displays a confirmation message indicating successful deletion of the startup investment.

    Use case ends.

**Extensions**

* 4a. Invalid input or missing parameters.

    * 4a1. CapitalConnect shows an error message.

      Use case resumes at step 2.

* 5a. Specified index is out of range or no startup investments at the specified index.

    * 5a1. CapitalConnect shows an error message indicating the issue.

      Use case ends.

**Use case: Edit a startup investment from the portfolio**

**MSS**

1.  User requests to edit a specific startup investment from their portfolio.
2.  CapitalConnect dashboard prompts the user to input the index of the startup investment to be deleted.
3.  User provides the index of the startup investment.
4.  CapitalConnect verifies the input for validity.
5. CapitalConnect dashboard prompts the user to provide details to update, where details could be:
    * Startup name
    * Industry
    * Funding stage
    * Address
    * Contact information
    * Valuation of Startup
6. User provides the necessary details.
7. CapitalConnect verifies the input for validity.
8. CapitalConnect edits the startup investment at the specified index from the user's portfolio.
9. CapitalConnect displays a confirmation message indicating successful edit of the startup investment.

    Use case ends.

**Extensions**

* 4a. Invalid input or missing parameters.

    * 4a1. CapitalConnect shows an error message.

      Use case resumes at step 2.

* 7a. Invalid input or missing parameters.

    * 7a1. CapitalConnect shows an error message.

      Use case resumes at step 2.

* 8a. Specified index is out of range or no startup investments.

    * 8a1. CapitalConnect shows an error message indicating the issue.

      Use case ends.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
2.  Should be able to hold up to 1000 startups without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  The system should have a response time of less than 1 second for common operations, such as adding or deleting a startup investment, to provide a smooth user experience.
5.  The application should provide clear and user-friendly error messages in case of invalid input or system errors to assist users in troubleshooting issues efficiently.
6.  The dashboard interface should be responsive and adapt to different screen sizes, ensuring a seamless user experience across devices such as desktops, laptops, tablets, and smartphones.


### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **CapitalConnect dashboard**: The user interface of the CapitalConnect application where users can manage their startup investments, perform searches, and view their portfolio.
* **Startup investment**: An investment made by a user in a startup company, typically including details such as the startup name, industry, funding stage, address, and contact information.
* **Industry**: The sector or field in which a startup operates, such as Tech, Biotech, or Finance.
* **Funding stage**: The development stage of a startup at which it has received a certain level of investment, such as Pre-Seed, Seed, Series A, Series B or Series C.
* **Valuation**: The valuation of the company.
* **Dashboard state**: The current configuration and data displayed in the CapitalConnect dashboard, including startup investments and any applied filters or search results.
* **Index**: A numeric value representing the position of an item within a list, used in commands to reference specific startup investments in the portfolio.
* **Confirmation message**: A notification displayed to the user indicating the successful completion of an action, such as adding or deleting a startup investment.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   2. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

2. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   2. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.


### Deleting a startup

1. Deleting a startup while all startups are being shown

   1. Prerequisites: List all startups using the `list` command. Multiple startups in the list.

   2. Test case: `delete 1`<br>
      Expected: First startup is deleted from the list. Details of the deleted startup shown.

   3. Test case: `delete 0`<br>
      Expected: No startup is deleted. Error details shown in the status message. Status bar remains the same.

   4. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

### Adding a startup

1. Adding a startup with valid inputs.

   1. Test case: `add n/Google p/999 e/sundarpichal@example.com v/100000 a/Menlo Park, block 123, #01-01 f/A i/tech`<br>
        Expected: The startup with the respective details have been successfully added to CapitalConnect. Details of the added startup shown.

2. Adding a startup with missing / invalid inputs.

    1. Test case: `add n/ p/98765432 e/sundarpichal@example.com v/100000 a/Menlo Park, block 123, #01-01 f/A i/tech`<br>
        Expected: No startup is added, message sent to user that name should not be blank.

    2. Test case: `add n/Google p/1 e/sundarpichal@example.com v/100000 a/Menlo Park, block 123, #01-01 f/A i/tech`<br>
        Expected: No startup is added, message sent to user that phone number should be at least 3 digits long.

    3. Test case: `add n/Google p/98765432 e/sundarpichal@example.com v/100000 a/Menlo Park, block 123, #01-01 f/J i/tech`<br>
        Expected: No startup is added, message sent to user on appropriate inputs for funding stages.

    4. Test case: `add n/Google p/98765432 e/sundarpichal@example.com v/-1 a/Menlo Park, block 123, #01-01 f/A i/tech` <br>
        Expected: No startup is added, message sent to user on the valid inputs for company valuation.

### Editing a startup

1. Prerequisites: One startup in CapitalConnect at the first position.

2. Editing startup with valid inputs

    1. Test case: `edit 1 n/test` <br>
        Expected: The startup at position 1 has its name changed. Details of the edited startup is shown.

    2. Test case: `edit 1 v/9999` <br>
        Expected: The startup at position 1 has its valuation changed, details of the edited startup is shown. The new valuation is displayed as `9.9k`.

3. Editing a startup with invalid inputs

    1. Test case: `edit 1 f/H` <br>
        Expected: No edits made to any startups, users are informed on valid input for funding stages.

    2. Test case: `edit 1 i/` <br>
        Expected: No edits made to any startups, users are informed on valid industry inputs.

### Adding a Note to a Startup

1. Prerequisites: One startup in CapitalConnect at the first position with 0 or more notes

2. Adding a note with valid inputs

   1. Test case: `addnote 1 Beautiful and Handsome` <br>
      Expected: The startup at position 1 has a note added to it. Details of the Note is shown in the Note Display Box.

3. Adding a note with invalid inputs

    1. Test case: `addnote 1` <br>
       Expected: No note is added to the Startup and Note Box, users are informed on valid input for the addnote command.

    2. Test case: `addnote No index given` <br>
       Expected: No note is added to the Startup and Note Box, users are informed on valid input for the addnote command.


### Editing a Note in a Startup

1. **Prerequisites**: A startup in CapitalConnect in position 1 with at least one note.

2. **Editing a note with valid inputs**

    1. Test case: `editnote 1 1 New content for the note`
        - Expected: The first note of the startup at index 1 is edited to "New content for the note". The details of the note are shown in the Note Display Box.

3. **Editing a note with invalid inputs**

    1. Test case: `editnote`
        - Expected: No note is edited. Error message about invalid command format is shown.

    2. Test case: `editnote 1`
        - Expected: No note is edited. Error message about invalid command format is shown.

    3. Test case: `editnote 1 2`
        - Expected: No note is edited. Error message about invalid command format is shown.

    4. Test case: `editnote 1 99 New content but invalid note index`
        - Expected: No note is edited as the note index is out of range. Error message about invalid note index is shown.

    5. Test case: `editnote 99 1 New content but invalid startup index`
        - Expected: No note is edited as the startup index is out of range. Error message about invalid startup index is shown.

### Deleting a Note from a Startup

1. **Prerequisites**: A startup in CapitalConnect in position 1 with at least one note.

2. **Deleting a note with valid inputs**

    1. Test case: `deletenote 1 1`
        - Expected: The first note of the startup at index 1 is deleted. Details of the updated notes list are shown in the Note Display Box.

3. **Deleting a note with invalid inputs**

    1. Test case: `deletenote`
        - Expected: No note is deleted. Error message about invalid command format is shown.

    2. Test case: `deletenote 1`
        - Expected: No note is deleted. Error message about invalid command format is shown.

    3. Test case: `deletenote 1 99`
        - Expected: No note is deleted as the note index is out of range. Error message about invalid note index is shown.

    4. Test case: `deletenote 99 1`
        - Expected: No note is deleted as the startup index is out of range. Error message about invalid startup index is shown.


## **Appendix: Planned Enhancement**

Team size: 4

1. Limit length of startup name: Currently we do not limit length of startup name user can input. This
results in overflow. We plan to address this in the future by limiting length of startup names to 32 letters long.

2. Supporting country codes: Currently we do not allow users to specify country codes using "-", this makes it hard to track
international numbers. We plan to allow for the user to add an optional country code field alongside the phone number in further iterations.

3. Limit phone numbers: Currently we do not limit the length of phone numbers users can input. This may result in a UI overflow.
We plan to address this in the future by limiting the length of the phone number to 8 digits for the number and 3 digits for the country code after planned enhancement 2 has been completed.

4. Supporting non-alphanumerical characters in startup names: Some company names may contain non-alphanumerical characters. We plan to address this in the future by allowing for such characters to be in the name,
but also changing the current regular expression rules to ensure that the input remains valid.

5. Supporting non-alphanumerical characters in tags: Currently tags must be in alphanumerical characters without spacings. This forces users to find alternative means to add tags, such as using camel case within their tags instead. For example,
`US based` would not be an allowed tag, resorting in users tagging the startup as `USBased` instead. We plan to address this in the future by allowing for such characters to be in the tag,
but also changing the current regular expression rules to ensure that the input remains valid.

6. Display Index for Notes: Currently, Notes displayed in the UI do not indicate their index, making identifying them for editing/deleting difficult. We plan to support this for a future iteration.

7. Delete All Notes Feature: Currently, Notes need to be deleted one by one, which may be troublesome for users who want to organise and clear their startup information quickly. We plan to support this in a future iteration.

8. Limited types of funding stage: Currently, we only support traditional funding stages, so inputs for `FUNDING_STAGE` should be either `S`, `PS`, `A`, `B` or `C` in order to find a matching startup. `A`, `B`, `C` represents the respective funding series whilst `PS` refers to pre-seed and `S` refers to the seed stage. Please take note that inputs other than the ones mentioned above will also be accepted, but zero matching startups will be listed.

9. Supporting partial matching in find commands: We understand that you might want to use partial matching to find matching startups, but this feature is currently under development. This feature will be dropped soon!

10. Potential limitation in detecting duplicated persons: Note that duplicated persons in one startup are detected by `pe/EMAIL`. We assume that email is unique for every person. In other words, we assume that it is possible to have 3 Johns in one company, and they all have different emails. Before adding a new person to the startup, always double-check their email to make sure that the person is not added already. Also take note that we allow one person to work in multiple startups.

