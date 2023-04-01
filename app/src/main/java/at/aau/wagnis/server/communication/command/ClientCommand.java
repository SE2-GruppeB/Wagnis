package at.aau.wagnis.server.communication.command;

/**
 * A command to be run by the client
 */
public interface ClientCommand {

    void execute(/*ClientLogicParameter*/); // TODO client logic
}