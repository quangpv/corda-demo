package com.template.core

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowSession
import net.corda.core.flows.SignTransactionFlow
import net.corda.core.transactions.SignedTransaction

abstract class ResponderFlow<T> : BaseFlow<T>() {

    @Suspendable
    fun signTo(session: FlowSession, checkFunc: (SignedTransaction) -> Unit = {}): SignedTransaction {
        return subFlow(object : SignTransactionFlow(session) {
            override fun checkTransaction(stx: SignedTransaction) {
                checkFunc(stx)
            }
        })
    }
}