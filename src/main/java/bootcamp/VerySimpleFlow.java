package bootcamp;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;

@InitiatingFlow
@StartableByRPC
public class VerySimpleFlow extends FlowLogic<Void> {

    @Suspendable
    public Void call() throws FlowException {
        return null;
    }
}
