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

namespace Apache.Ignite.Core.Cache
{
    using System;

    /// <summary>
    /// Memory usage metrics.
    /// Obsolete, use <see cref="IDataRegionMetrics"/>.
    /// </summary>
    [Obsolete("See IDataRegionMetrics.")]
    public interface IMemoryMetrics
    {
        /// <summary>
        /// Gets the memory policy name.
        /// </summary>
        string Name { get; }

        /// <summary>
        /// Gets the count of allocated pages.
        /// </summary>
        long TotalAllocatedPages { get; }

        /// <summary>
        /// Gets the allocation rate, in pages per second.
        /// </summary>
        float AllocationRate { get; }

        /// <summary>
        /// Gets the eviction rate, in pages per second.
        /// </summary>
        float EvictionRate { get; }

        /// <summary>
        /// Gets the percentage of pages fully occupied by entries that are larger than page.
        /// </summary>
        float LargeEntriesPagesPercentage { get; }

        /// <summary>
        /// Gets the page fill factor: the percentage of used space.
        /// </summary>
        float PageFillFactor { get; }
    }
}
