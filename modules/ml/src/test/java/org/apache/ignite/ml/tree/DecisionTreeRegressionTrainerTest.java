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

package org.apache.ignite.ml.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.ignite.ml.dataset.feature.extractor.impl.DoubleArrayVectorizer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link DecisionTreeRegressionTrainer}.
 */
@RunWith(Parameterized.class)
public class DecisionTreeRegressionTrainerTest {
    /** Number of parts to be tested. */
    private static final int[] partsToBeTested = new int[] {1, 2, 3, 4, 5, 7};

    /** Number of partitions. */
    @Parameterized.Parameter()
    public int parts;

    /** Use index [= 1 if true]. */
    @Parameterized.Parameter(1)
    public int useIdx;

    /** Test parameters. */
    @Parameterized.Parameters(name = "Data divided on {0} partitions. Use index = {1}.")
    public static Iterable<Integer[]> data() {
        List<Integer[]> res = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            for (int part : partsToBeTested)
                res.add(new Integer[] {part, i});
        }

        return res;
    }

    /** */
    @Test
    public void testFit() {
        int size = 100;

        Map<Integer, double[]> data = new HashMap<>();

        Random rnd = new Random(0);
        for (int i = 0; i < size; i++) {
            double x = rnd.nextDouble() - 0.5;
            data.put(i, new double[]{x, x > 0 ? 1 : 0});
        }

        DecisionTreeRegressionTrainer trainer = new DecisionTreeRegressionTrainer(1, 0)
            .withUsingIdx(useIdx == 1);

        DecisionTreeNode treeNode = trainer.fit(data, parts, new DoubleArrayVectorizer<Integer>().labeled(1)).getRootNode();

        assertTrue(treeNode instanceof DecisionTreeConditionalNode);

        DecisionTreeConditionalNode node = (DecisionTreeConditionalNode)treeNode;

        assertEquals(0, node.getThreshold(), 1e-3);

        assertTrue(node.getThenNode() instanceof DecisionTreeLeafNode);
        assertTrue(node.getElseNode() instanceof DecisionTreeLeafNode);

        DecisionTreeLeafNode thenNode = (DecisionTreeLeafNode)node.getThenNode();
        DecisionTreeLeafNode elseNode = (DecisionTreeLeafNode)node.getElseNode();

        assertEquals(1, thenNode.getVal(), 1e-10);
        assertEquals(0, elseNode.getVal(), 1e-10);
    }
}
