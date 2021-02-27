package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.TransferContract
import com.template.core.RequesterFlow
import com.template.core.ResponderFlow
import com.template.core.publicKeys
import com.template.states.TransferState
import net.corda.core.contracts.AlwaysAcceptAttachmentConstraint
import net.corda.core.contracts.SignatureAttachmentConstraint
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker


@InitiatingFlow
@StartableByRPC
class TransferRequester(
        private val value: Int,
        private val receiver: Party
) : RequesterFlow<SignedTransaction>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call(): SignedTransaction {

        val state = TransferState(value, services.me.legalIdentity, receiver)
        val transaction = TransactionBuilder(services.network.defaultNotary)
                .addOutputState(state, TransferContract.ID, AlwaysAcceptAttachmentConstraint)
                .addCommand(TransferContract.Create(), state.publicKeys)
                .apply { verify(serviceHub) }

        return signByNotary(signBy(receiver, signByMe(transaction)))
    }
}

@InitiatedBy(TransferRequester::class)
class TransferResponder(private val session: FlowSession) : ResponderFlow<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {
        val txId = signTo(session).id
        return subFlow(ReceiveFinalityFlow(session, expectedTxId = txId))
    }
}
