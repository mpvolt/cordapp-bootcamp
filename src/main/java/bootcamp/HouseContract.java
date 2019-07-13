package bootcamp;

import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class HouseContract implements Contract {
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        if(tx.getCommands().size() != 1)
        {
            throw new IllegalArgumentException("Must have one command");
        }
        Command command = tx.getCommand(0);
        List<PublicKey> requiredSigners = command.getSigners();
        CommandData commandType = command.getValue();

        if (commandType instanceof Register)
        {
            //Registration Transaction Logic

            //Shape Constraints
            if(tx.getInputStates().size() != 0)
                throw new IllegalArgumentException("Registration transaction must have no inputs");

            if(tx.getOutputStates().size() != 1)
                throw new IllegalArgumentException("Registration transaction must have 1 output");

            //Content Constraints
            ContractState OutputState = tx.getOutput(0);
            if(!(OutputState instanceof HouseState))
            {
                throw new IllegalArgumentException("Must be a HouseState");
            }
            HouseState houseState = (HouseState) OutputState;
            if(houseState.getAddress().length() <= 3){
                throw new IllegalArgumentException("Address must be at least 3 characters");
            }

            if(houseState.getOwner().getName().getCountry() == "Brazil")
            {
                throw new IllegalArgumentException("Not allowed to register for Brazilian owners");
            }

            //Required Signer Constraints
            Party owner = houseState.getOwner();
            PublicKey ownersKey = owner.getOwningKey();

            if(!(requiredSigners.contains(ownersKey)))
            {
                throw new IllegalArgumentException("Owner of house must sign registration");
            }
        }
        else if (commandType instanceof Transfer)
        {
            //Shape Constraints
            if (tx.getInputStates().size() != 1)
                throw new IllegalArgumentException("Must have exactly one input state");
            if(tx.getOutputStates().size() != 1)
            {
                throw new IllegalArgumentException("Must have exactly one output state");
            }

            ContractState input = tx.getInput(0);
            ContractState output = tx.getOutput(0);

            //Shape Constraints
            if (tx.getInputStates() instanceof HouseState)
                throw new IllegalArgumentException("Input must be a houseState");
            if(tx.getOutputStates() instanceof HouseState)
            {
                throw new IllegalArgumentException("Output must be a houseState");
            }

            HouseState inputHouse = (HouseState) input;
            HouseState outputHouse = (HouseState) output;

            if(!((outputHouse).getAddress().equals(outputHouse.getAddress())))
            {
                throw new IllegalArgumentException("In a transfer, the address can't change");
            }

            if(inputHouse.getOwner().equals(outputHouse.getOwner()))
            {
                throw new IllegalArgumentException("In a transfer, the owner must change");
            }

            //Signer Constraints
            Party inputOwner = inputHouse.getOwner();
            Party outputOwner = outputHouse.getOwner();
            if(!(requiredSigners.contains(inputOwner.getOwningKey())))
            {
                throw new IllegalArgumentException("Current owner must sign transfer");
            }

            if(!(requiredSigners.contains(outputOwner.getOwningKey())))
            {
                throw new IllegalArgumentException("New owner must sign transfer");
            }

        }
        else
        {
            throw new IllegalArgumentException("Command not recognized");
        }
    }

    public static class Register implements CommandData {

    }

    public static class Transfer implements CommandData{

    }
}
