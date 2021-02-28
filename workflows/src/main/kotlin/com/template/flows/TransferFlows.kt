package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.TransferContract
import com.template.core.contracts.publicKeys
import com.template.core.flows.FlowInterceptor
import com.template.core.flows.RequesterFlow
import com.template.core.flows.ResponderFlow
import com.template.states.TransferState
import net.corda.core.contracts.AlwaysAcceptAttachmentConstraint
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder


@InitiatingFlow
@StartableByRPC
class TransferRequester(
        private val value: Int,
        private val receiver: Party
) : RequesterFlow<SignedTransaction>() {

    override val interceptor: FlowInterceptor = BankFilter()

    @Suspendable
    override fun doCall(): SignedTransaction {
        val state = TransferState(value, services.me.legalIdentity, receiver)
        val transaction = TransactionBuilder(services.network.defaultNotary)
                .addOutputState(state, TransferContract.ID, AlwaysAcceptAttachmentConstraint)
                .addCommand(TransferContract.Create(), state.publicKeys)
                .apply { verify(services) }

        return signByNotary(signBy(receiver, signByMe(transaction)))
    }
}

@InitiatedBy(TransferRequester::class)
class TransferResponder(private val session: FlowSession) : ResponderFlow<SignedTransaction>() {

    @Suspendable
    override fun doCall(): SignedTransaction {
        return finalReceive(session, signTo(session).id)
    }
}
