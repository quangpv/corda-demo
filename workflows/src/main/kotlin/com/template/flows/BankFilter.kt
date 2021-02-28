package com.template.flows

import com.template.core.flows.FlowInterceptor
import com.template.core.throws
import net.corda.core.flows.FlowLogic

class BankFilter : FlowInterceptor {
    companion object {
        val NODES_NOT_SUPPORTED = arrayOf("O=Notary, L=London, C=GB")
    }

    override fun intercept(flow: FlowLogic<*>) {
        val myName = flow.serviceHub.myInfo.legalIdentities.single().name.toString()
        (myName in NODES_NOT_SUPPORTED) throws "${flow.javaClass.simpleName} not support for node $myName"
    }
}