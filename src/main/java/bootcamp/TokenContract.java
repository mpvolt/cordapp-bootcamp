package bootcamp;

import examples.ArtContract;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;
import sun.tools.jstat.Token;

import java.security.PublicKey;
import java.util.List;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
public class TokenContract implements Contract {
@Override


public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

    if(tx.getCommands().size() != 1)
    {
        throw new IllegalArgumentException("Must have 1 command");
    }

    if (tx.getInputStates().size() != 0) {
        throw new IllegalArgumentException("Must have no inputs");
    }

    if (tx.getOutputStates().size() != 1) {
        throw new IllegalArgumentException("Must have 1 output");
    }


    Command command = tx.getCommand(0);
    List<PublicKey> requiredSigners = command.getSigners();
    CommandData commandType = command.getValue();

    if (!(commandType instanceof Commands.Issue))
    {
        throw new IllegalArgumentException("Command must be the Issue command");
    }
    else if(commandType instanceof Commands.Issue)
    {

        if(!(tx.getOutput(0) instanceof TokenState))
        {
            throw new IllegalArgumentException("Output must be a TokenState");
        }

        TokenState outputToken = (TokenState) tx.getOutput(0);

        int amount = outputToken.getAmount();

        if(amount <= 0)
        {
            throw new IllegalArgumentException("Output must be greater than 0");
        }

        //Required Signer Constraints
        Party issuer = outputToken.getIssuer();
        PublicKey issuerKey = issuer.getOwningKey();

        if (!(requiredSigners.contains(issuerKey))) {
            throw new IllegalArgumentException("Issuer of the tokens must sign registration");
        }
    }
}

    public static String ID = "bootcamp.TokenContract";



    public interface Commands extends CommandData {
        class Issue implements Commands { }
    }
}