package fr.xephi.authme.command.help;

import ch.jalu.injector.testing.BeforeInjecting;
import ch.jalu.injector.testing.DelayedInjectionRunner;
import ch.jalu.injector.testing.InjectDelayed;
import fr.xephi.authme.command.CommandDescription;
import fr.xephi.authme.command.TestCommandsUtil;
import fr.xephi.authme.message.MessageFileHandler;
import fr.xephi.authme.message.MessageFileHandlerProvider;
import fr.xephi.authme.permission.DefaultPermission;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Set;
import java.util.function.Function;

import static fr.xephi.authme.TestHelper.getJarFile;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

/**
 * Test for {@link HelpMessagesService}.
 */
@RunWith(DelayedInjectionRunner.class)
public class HelpMessagesServiceTest {

    private static final String TEST_FILE = "/fr/xephi/authme/command/help/help_test.yml";
    private static final Set<CommandDescription> COMMANDS = TestCommandsUtil.generateCommands();

    @InjectDelayed
    private HelpMessagesService helpMessagesService;

    @Mock
    private MessageFileHandlerProvider messageFileHandlerProvider;

    @SuppressWarnings("unchecked")
    @BeforeInjecting
    public void initializeHandler() {
        MessageFileHandler handler = new MessageFileHandler(getJarFile(TEST_FILE), "messages/messages_en.yml");
        given(messageFileHandlerProvider.initializeHandler(any(Function.class))).willReturn(handler);
    }

    @Test
    public void shouldReturnLocalizedCommand() {
        // given
        CommandDescription command = TestCommandsUtil.getCommandWithLabel(COMMANDS, "authme", "register");

        // when
        CommandDescription localCommand = helpMessagesService.buildLocalizedDescription(command);

        // then
        assertThat(localCommand.getDescription(), equalTo("Registration"));
        assertThat(localCommand.getDetailedDescription(), equalTo("Registers the player"));
        assertThat(localCommand.getExecutableCommand(), equalTo(command.getExecutableCommand()));
        assertThat(localCommand.getPermission(), equalTo(command.getPermission()));
        assertThat(localCommand.getArguments(), hasSize(2));
        assertThat(localCommand.getArguments().get(0).getName(), equalTo("password"));
        assertThat(localCommand.getArguments().get(0).getDescription(), equalTo("The password"));
        assertThat(localCommand.getArguments().get(1).getName(), equalTo("confirmation"));
        assertThat(localCommand.getArguments().get(1).getDescription(), equalTo("The confirmation"));
    }

    @Test
    public void shouldReturnLocalizedCommandWithDefaults() {
        // given
        CommandDescription command = TestCommandsUtil.getCommandWithLabel(COMMANDS, "authme", "login");

        // when
        CommandDescription localCommand = helpMessagesService.buildLocalizedDescription(command);

        // then
        assertThat(localCommand.getDescription(), equalTo("Logging in"));
        assertThat(localCommand.getDetailedDescription(), equalTo("'login' test command"));
        assertThat(localCommand.getArguments(), hasSize(1));
        assertThat(localCommand.getArguments().get(0).getName(), equalTo("user password"));
        assertThat(localCommand.getArguments().get(0).getDescription(), equalTo("'password' argument description"));
    }

    @Test
    public void shouldReturnSameCommandForNoLocalization() {
        // given
        CommandDescription command = TestCommandsUtil.getCommandWithLabel(COMMANDS, "email");

        // when
        CommandDescription localCommand = helpMessagesService.buildLocalizedDescription(command);

        // then
        assertThat(localCommand, sameInstance(command));
    }

    @Test
    public void shouldKeepChildrenInLocalCommand() {
        // given
        CommandDescription command = TestCommandsUtil.getCommandWithLabel(COMMANDS, "authme");

        // when
        CommandDescription localCommand = helpMessagesService.buildLocalizedDescription(command);

        // then
        assertThat(localCommand.getChildren(), equalTo(command.getChildren()));
        assertThat(localCommand.getDescription(), equalTo("authme cmd"));
        assertThat(localCommand.getDetailedDescription(), equalTo("Main command"));
    }

    @Test
    public void shouldGetTranslationsForSectionAndMessage() {
        // given / when / then
        assertThat(helpMessagesService.getMessage(DefaultPermission.OP_ONLY), equalTo("only op"));
        assertThat(helpMessagesService.getMessage(HelpMessage.RESULT), equalTo("res."));
        assertThat(helpMessagesService.getMessage(HelpSection.ARGUMENTS), equalTo("arg."));
    }
}
