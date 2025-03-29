package com.example.cmput301_team_project.db;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.List;

public class BatchLoader {
    private DocumentSnapshot low;
    private final int batchSize;
    private boolean isAllLoaded;

    public BatchLoader(int batchSize) {
        this.low = null;
        this.batchSize = batchSize;
        isAllLoaded = false;
    }

    public Query getNextBatchQuery(CollectionReference collectionReference) {
        return low == null ? collectionReference.limit(batchSize) : collectionReference.startAfter(low).limit(batchSize);
    }

    public Query getNextBatchQuery(Query query) {
        return low == null ? query.limit(batchSize) : query.startAfter(low).limit(batchSize);
    }

    public void nextBatch(List<DocumentSnapshot> documents) {
        if(!documents.isEmpty()) {
            low = documents.get(documents.size() - 1);
        }
        if(documents.size() < batchSize) {
            isAllLoaded = true;
        }
    }

    public boolean isAllLoaded() {
        return isAllLoaded;
    }

    public void reset() {
        low = null;
    }
}
