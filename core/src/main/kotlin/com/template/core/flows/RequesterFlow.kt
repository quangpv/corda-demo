package com.template.core.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowSession
import net.corda.core.identity.Party

abstract class RequesterFlow<T> : BaseFlow<T>() {

    @Suspendable
    fun createSession(party: Party): FlowSession {
        return initiateFlow(party)
    }
}