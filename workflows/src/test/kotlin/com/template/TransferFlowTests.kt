package com.template

import com.template.flows.TransferRequester
import com.template.flows.TransferResponder
import net.corda.core.identity.CordaX500Name
import net.corda.core.utilities.getOrThrow
import net.corda.testing.core.internal.ContractJarTestUtils.makeTestJar
import net.corda.testing.core.singleIdentity
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkNotarySpec
import net.corda.testing.node.MockNodeParameters
import net.corda.testing.node.StartedMockNode
import org.junit.After
import org.junit.Before
import org.junit.Test


class TransferFlowTests {

    lateinit var mockNetwork: MockNetwork
    lateinit var a: StartedMockNode
    lateinit var b: StartedMockNode

    @Before
    fun setup() {
        mockNetwork = MockNetwork(
                listOf("com.template"),
                notarySpecs = listOf(MockNetworkNotarySpec(CordaX500Name("Notary","London","GB")))
        )
        a = mockNetwork.createNode(MockNodeParameters())
        b = mockNetwork.createNode(MockNodeParameters())
        val startedNodes = arrayListOf(a, b)
        // For real nodes this happens automatically, but we have to manually register the flow for tests
        startedNodes.forEach { it.registerInitiatedFlow(TransferResponder::class.java) }
        mockNetwork.runNetwork()
    }

    @Test
    fun `SignedTransaction returned by the flow is signed by the initiator`() {
        val flow = TransferRequester(1, b.info.singleIdentity())
        val future = a.startFlow(flow)
        mockNetwork.runNetwork()

        val signedTx = future.getOrThrow()
        signedTx.verifySignaturesExcept(b.info.singleIdentity().owningKey)
    }

    @Test
    fun `SignedTransaction returned by the flow is signed by the acceptor`() {
        val flow = TransferRequester(1, b.info.singleIdentity())
        val future = a.startFlow(flow)
        mockNetwork.runNetwork()

        val signedTx = future.getOrThrow()
        signedTx.verifySignaturesExcept(a.info.singleIdentity().owningKey)
    }

    @After
    fun tearDown() {
        mockNetwork.stopNodes()
    }

    @Test
    fun `dummy test`() {

    }
}