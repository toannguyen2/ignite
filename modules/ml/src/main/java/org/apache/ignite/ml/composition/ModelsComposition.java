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

package org.apache.ignite.ml.composition;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.ignite.ml.Exportable;
import org.apache.ignite.ml.Exporter;
import org.apache.ignite.ml.IgniteModel;
import org.apache.ignite.ml.composition.predictionsaggregator.PredictionsAggregator;
import org.apache.ignite.ml.environment.deploy.DeployableObject;
import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.apache.ignite.ml.util.ModelTrace;

/**
 * Model consisting of several models and prediction aggregation strategy.
 */
public class ModelsComposition<M extends IgniteModel<Vector, Double>>
    implements IgniteModel<Vector, Double>, Exportable<ModelsCompositionFormat>, DeployableObject {
    /**
     * Predictions aggregator.
     */
    protected PredictionsAggregator predictionsAggregator;

    /**
     * Models.
     */
    protected List<M> models;

    /**
     * Constructs a new instance of composition of models.
     *
     * @param models Basic models.
     * @param predictionsAggregator Predictions aggregator.
     */
    public ModelsComposition(List<M> models, PredictionsAggregator predictionsAggregator) {
        this.predictionsAggregator = predictionsAggregator;
        this.models = Collections.unmodifiableList(models);
    }

    /** */
    public ModelsComposition() {
    }

    /**
     * Applies containing models to features and aggregate them to one prediction.
     *
     * @param features Features vector.
     * @return Estimation.
     */
    @Override public Double predict(Vector features) {
        double[] predictions = new double[models.size()];

        for (int i = 0; i < models.size(); i++)
            predictions[i] = models.get(i).predict(features);

        return predictionsAggregator.apply(predictions);
    }

    /**
     * Returns predictions aggregator.
     */
    public PredictionsAggregator getPredictionsAggregator() {
        return predictionsAggregator;
    }

    /**
     * Returns containing models.
     */
    public List<M> getModels() {
        return models;
    }

    /** {@inheritDoc} */
    @Override public <P> void saveModel(Exporter<ModelsCompositionFormat, P> exporter, P path) {
        ModelsCompositionFormat format = new ModelsCompositionFormat(models, predictionsAggregator);
        exporter.save(format, path);
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return toString(false);
    }

    /** {@inheritDoc} */
    @Override public String toString(boolean pretty) {
        return ModelTrace.builder("Models composition", pretty)
            .addField("aggregator", predictionsAggregator.toString(pretty))
            .addField("models", models)
            .toString();
    }

    /** {@inheritDoc} */
    @JsonIgnore
    @Override public List<Object> getDependencies() {
        return Collections.singletonList(predictionsAggregator);
    }
}
