/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.spi.discovery.tcp;

import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.internal.TcpDiscoveryNode;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;

/**
 * Test for {@link TcpDiscoveryNode#consistentId()}
 */
public class TcpDiscoveryNodeConsistentIdSelfTest extends GridCommonAbstractTest {
    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String igniteInstanceName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(igniteInstanceName);

        cfg.setLocalHost("0.0.0.0");

        return cfg;
    }

    /** {@inheritDoc} */
    @Override protected void beforeTest() throws Exception {
        startGrids(1);
    }

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        stopAllGrids();
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testConsistentId() throws Exception {
        Object id0 = grid(0).localNode().consistentId();

        int port0 = getDiscoveryPort(grid(0));

        for (int i = 0; i < 10; ++i) {
            stopAllGrids();

            startGrids(1);

            if (port0 == getDiscoveryPort(grid(0)))
                assertEquals(id0, grid(0).localNode().consistentId());
        }
    }

    /**
     * @param ignite Ignite.
     * @return Discovery port.
     */
    private int getDiscoveryPort(Ignite ignite) {
        return ((TcpDiscoveryNode)ignite.cluster().localNode()).discoveryPort();
    }
}
