package com.template.core

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.CollectSignaturesFlow
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.Party
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.NetworkMapCache
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

abstract class BaseFlow<T> : FlowLogic<T>() {
    val services by lazy(LazyThreadSafetyMode.NONE) { SupportServiceHub(serviceHub) }

    fun signByMe(transaction: TransactionBuilder): SignedTransaction {
        return serviceHub.signInitialTransaction(transaction)
    }

    @Suspendable
    fun signBy(signer: Party, transaction: SignedTransaction): SignedSession {
        val session = initiateFlow(signer)
        val signed = subFlow(CollectSignaturesFlow(transaction, setOf(session)))
        return SignedSession(session, signed)
    }

    @Suspendable
    fun signByNotary(signedSession: SignedSession): SignedTransaction {
        return subFlow(FinalityFlow(signedSession.signed, setOf(signedSession.session)))
    }
}

class SupportServiceHub(private val serviceHub: ServiceHub) {

    val me = MyInfoService(serviceHub)
    val network = NetworkSupportService(serviceHub.networkMapCache)
}

class MyInfoService(private val serviceHub: ServiceHub) {

    val legalIdentity get() = serviceHub.myInfo.legalIdentities.single()
}

class NetworkSupportService(private val networkMapCache: NetworkMapCache) {
    val defaultNotary get() = networkMapCache.notaryIdentities.single()
}