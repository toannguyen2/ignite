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

package org.apache.ignite.internal.pagemem.wal.record;

import java.util.Collections;
import java.util.List;
import org.apache.ignite.internal.util.tostring.GridToStringInclude;
import org.apache.ignite.internal.util.typedef.internal.A;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.util.typedef.internal.U;

/**
 * Logical data record with cache operation description.
 * This record contains information about operation we want to do.
 * Contains operation type (put, remove) and (Key, Value, Version) for each {@link DataEntry}
 */
public class DataRecord extends TimeStampRecord {
    /** */
    @GridToStringInclude
    private Object writeEntries;

    /** {@inheritDoc} */
    @Override public RecordType type() {
        return RecordType.DATA_RECORD_V2;
    }

    /**
     *
     */
    private DataRecord() {
        // No-op, used from builder methods.
    }

    /**
     * @param writeEntry Write entry.
     */
    public DataRecord(DataEntry writeEntry) {
        this(writeEntry, U.currentTimeMillis());
    }

    /**
     * @param writeEntries Write entries.
     */
    public DataRecord(List<DataEntry> writeEntries) {
        this(writeEntries, U.currentTimeMillis());
    }

    /**
     * @param writeEntries Write entries.
     * @param timestamp TimeStamp.
     */
    public DataRecord(Object writeEntries, long timestamp) {
        super(timestamp);

        A.notNull(writeEntries, "writeEntries");

        this.writeEntries = writeEntries;
    }

    /**
     * @param writeEntries Write entries.
     * @return {@code this} for chaining.
     */
    public DataRecord setWriteEntries(List<DataEntry> writeEntries) {
        this.writeEntries = writeEntries;

        return this;
    }

    /**
     * @return Collection of write entries.
     */
    public List<DataEntry> writeEntries() {
        if (writeEntries instanceof DataEntry)
            return Collections.singletonList((DataEntry)writeEntries);

        return (List<DataEntry>)writeEntries;
    }

    /** @return Count of {@link DataEntry} stored inside this record. */
    public int entryCount() {
        return (writeEntries instanceof DataEntry)
            ? 1
            : ((List<DataEntry>)writeEntries).size();
    }

    /**
     * @param idx Index of element.
     * @return {@link DataEntry} at the specified position.
     */
    public DataEntry get(int idx) {
        if (writeEntries instanceof DataEntry) {
            assert idx == 0;

            return (DataEntry)writeEntries;
        }

        return ((List<DataEntry>)writeEntries).get(idx);
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(DataRecord.class, this, "super", super.toString());
    }
}
