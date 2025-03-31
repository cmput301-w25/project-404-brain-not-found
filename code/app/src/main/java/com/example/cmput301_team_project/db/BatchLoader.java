package com.example.cmput301_team_project.db;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.List;

/**
 * The BatchLoader class holds the processing of Firestore collections into batches. It keeps track
 * of the last document loaded to ensure pages are being loaded continuously.
 */
public class BatchLoader {
    private DocumentSnapshot low;
    private final int batchSize;
    private boolean isAllLoaded;

    /**
     * Creates a BatchLoader with specified batch size.
     *
     * @param batchSize The number of documents to retrieve in each batch.
     */
    public BatchLoader(int batchSize) {
        this.low = null;
        this.batchSize = batchSize;
        isAllLoaded = false;
    }

    /**
     * Creates a Firestore query to fetch the next batch of documents from the collection reference.
     *
     * @param collectionReference the collection reference of the documents to be fetched.
     * @return a Query object for the next batch of docs.
     */
    public Query getNextBatchQuery(CollectionReference collectionReference) {
        return low == null ? collectionReference.limit(batchSize) : collectionReference.startAfter(low).limit(batchSize);
    }

    /**
     * Creates a Firestore query to fetch the next batch of documents from the existing query
     *
     * @param query The already existing query
     * @return a Query object for the next batch of docs.
     */
    public Query getNextBatchQuery(Query query) {
        return low == null ? query.limit(batchSize) : query.startAfter(low).limit(batchSize);
    }

    /**
     * Updates the Batch Loader with the latest retrieved documents. Updates the reference to the
     * last fetched document and determines if all documents have been fetched.
     *
     * @param documents The list of documents retrieved.
     */
    public void nextBatch(List<DocumentSnapshot> documents) {
        if(!documents.isEmpty()) {
            low = documents.get(documents.size() - 1);
        }
        if(documents.size() < batchSize) {
            isAllLoaded = true;
        }
    }

    /**
     * Checks if all documents have been loaded.
     *
     * @return {@code true} if all documents are loaded, otherwise return {@code false}
     */
    public boolean isAllLoaded() {
        return isAllLoaded;
    }

    /**
     * Resets the Batch Loader by clearing the last document reference.
     */
    public void reset() {
        low = null;
    }
}
