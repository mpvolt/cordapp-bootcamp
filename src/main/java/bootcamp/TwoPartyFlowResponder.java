package bootcamp;

import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.ServiceHub;

import java.util.List;

@InitiatedBy(TwoPartyFlow.class)
public class TwoPartyFlowResponder extends FlowLogic<Void> {

    FlowSession counterPartySession;

    public TwoPartyFlowResponder(FlowSession counterPartySession)
    {
        this.counterPartySession = counterPartySession;
    }

    @Override
    public Void call() throws FlowException {
        ServiceHub serviceHub = getServiceHub();

        List<StateAndRef<HouseState>> statesFromTheVault = serviceHub.getVaultService().queryBy(HouseState.class).getStates();


        CordaX500Name alicesName = new CordaX500Name("Alice", "Manchester", "UK");
        NodeInfo alice = serviceHub.getNetworkMapCache().getNodeByLegalName(alicesName);

        int platformVersion = serviceHub.getMyInfo().getPlatformVersion();

        int receivedInt = counterPartySession.receive(Integer.class).unwrap(it ->{
            if(it > 3)
                throw new IllegalArgumentException("Number too high");
            return it;
        });

        int receivedIntPlusOne = receivedInt + 1;

        counterPartySession.send(receivedIntPlusOne);

        return null;
    }
}
