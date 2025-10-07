package com.training;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

public class MongoJavaApp2 {


    private static final String URI = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "finance_db";
    private static final String COLLECTION_NAME = "transactions";
    private static MongoClient mongoClient;

    public static void main(String[] args) {
        MongoCollection<Document> collection = connectToMongo(URI, DATABASE_NAME, COLLECTION_NAME);

        System.out.println("Initial document count: " + collection.countDocuments());

        // Insert a document
        // Example of inserting a document with nested fields
        Document txn = new Document("txnId", "T2001")
                .append("accountId", "A5001")
                .append("type", "Credit")
                .append("amount", 3000.75)
                .append("currency", "INR")
                .append("date", Date.from(Instant.now()))
                .append("status", "SUCCESS")
                .append("channel", "MobileBanking")
                .append("remarks", "Salary credit")
                .append("address", new Document("city", "Mumbai")
                        .append("state", "Maharashtra")
                        .append("country", "India"))
                .append("tags", Arrays.asList("salary", "credit", "monthly"));
        insertDocument(collection, txn);

// Example of inserting multiple documents with nested fields
        Document txn2 = new Document("txnId", "T2002")
                .append("accountId", "A5002")
                .append("type", "Debit")
                .append("amount", 1500.00)
                .append("currency", "INR")
                .append("date", Date.from(Instant.now()))
                .append("status", "PENDING")
                .append("channel", "ATM")
                .append("remarks", "ATM Withdrawal")
                .append("address", new Document("city", "Delhi")
                        .append("state", "Delhi")
                        .append("country", "India"))
                .append("tags", Arrays.asList("withdrawal", "debit", "atm"));

        Document txn3 = new Document("txnId", "T2003")
                .append("accountId", "A5003")
                .append("type", "Credit")
                .append("amount", 5000.00)
                .append("currency", "INR")
                .append("date", Date.from(Instant.now()))
                .append("status", "FAILED")
                .append("channel", "OnlineTransfer")
                .append("remarks", "Project payment")
                .append("address", new Document("city", "Bangalore")
                        .append("state", "Karnataka")
                        .append("country", "India"))
                .append("tags", Arrays.asList("project", "credit", "online"));

        insertManyDocuments(collection, Arrays.asList(txn2, txn3));

        // Find all documents
        System.out.println("\nAll transactions:");
        findDocuments(collection, new Document());

        // Find transactions above 2500
        System.out.println("\nTransactions above 2500:");
        findDocuments(collection, Filters.gt("amount", 2500));

        // Find transactions of type Credit and status SUCCESS
        System.out.println("\nTransactions of type Credit and status SUCCESS:");
        findDocuments(collection, Filters.and(Filters.eq("type", "Credit"), Filters.eq("status", "SUCCESS")));

        // find all documents and print only the txnId and amount fields
        System.out.println("\nAll transactions (txnId and amount only):");
        findDocuments(collection, new Document(), Projections.include("txnid", "amount"));


        // find all documents where address.city is Mumbai
        System.out.println("\nTransactions from Mumbai:");
        findDocuments(collection, Filters.eq("address.city", "Mumbai"));

        // aggregate to find total amount of successful transactions
        System.out.println("\nTotal amount of successful transactions:");
        sumOfTransactions(collection);

        // Update a document
        System.out.println("\nUpdating remarks of transaction T2001:");
        updateDocument(collection, Filters.eq("txnId", "T2001"), Updates.set("remarks", "Updated Salary credit"));

        // Update the address field to include a new nested field (zipCode)
        updateDocument(collection, Filters.eq("txnId", "T2001"), Updates.set("address.zipCode", "400001"));

        // Add a new nested object (contact) to the document
        updateDocument(collection, Filters.eq("txnId", "T2001"), Updates.set("contact",
                new Document("email", "user@example.com")
                        .append("phone", "1234567890")));

//         Delete a document
            System.out.println("\nDeleting transaction T2001:");
            deleteDocument(collection, Filters.eq("txnId", "T2001"));

        mongoClient.close();
    }


    private static MongoCollection<Document> connectToMongo(String uri, String databaseName, String collectionName) {
        mongoClient = MongoClients.create(uri);
        System.out.println("Databases:");
        mongoClient.listDatabaseNames().forEach(System.out::println);

        MongoDatabase database = mongoClient.getDatabase(databaseName);
        System.out.println("Collections in " + databaseName + ":");
        database.listCollectionNames().forEach(System.out::println);

        MongoCollection<Document> collection = database.getCollection(collectionName);

        return collection;
    }

    private static void insertDocument(MongoCollection<Document> collection, Document document) {
        try {
            InsertOneResult result = collection.insertOne(document);
            System.out.println("Inserted a document with the following id: " + result.getInsertedId().asObjectId().getValue());
        } catch (MongoException e) {
            System.out.println("Error inserting document: " + e.getMessage());
        }
    }

    private static void insertManyDocuments(MongoCollection<Document> collection, java.util.List<Document> documents) {
        try {
            InsertManyResult result = collection.insertMany(documents);
            System.out.println("Inserted count: " + result.getInsertedIds().size());
        } catch (MongoException e) {
            System.out.println("Error inserting documents: " + e.getMessage());
        }

    }

    private static void findDocuments(MongoCollection<Document> collection, Bson filter) {
        try {
            collection.find(filter).forEach(doc -> System.out.println(doc.toJson()));
        } catch (MongoException e) {
            System.out.println("Error finding documents: " + e.getMessage());
        }
    }

    private static void findDocuments(MongoCollection<Document> collection, Bson filter, Bson projection) {
        try {
            collection.find(filter).projection(projection).forEach(doc -> System.out.println(doc.toJson()));
        } catch (MongoException e) {
            System.out.println("Error finding documents: " + e.getMessage());
        }
    }

    private static void sumOfTransactions(MongoCollection<Document> collection) {
        try {
            double totalSuccessAmount = collection.aggregate(Arrays.asList(
                    new Document("$match", new Document("status", "SUCCESS")),
                    new Document("$group", new Document("_id", null).append("total", new Document("$sum", "$amount")))
            )).first().getDouble("total");
            System.out.println("Total successful amount: " + totalSuccessAmount);
        } catch (MongoException e) {
            System.out.println("Error aggregating documents: " + e.getMessage());
        }
    }


    private static void updateDocument(MongoCollection<Document> collection, Bson filter, Bson update) {
        try {
            UpdateResult result = collection.updateOne(filter, update);
            System.out.println("Matched " + result.getMatchedCount() + " document(s)");
            if (result.getMatchedCount() > 0) {
                System.out.println("After update the document is: " + collection.find(filter).first().toJson());
            }
        } catch (MongoException e) {
            System.out.println("Error updating document: " + e.getMessage());
        }
    }

    private static void deleteDocument(MongoCollection<Document> collection, Bson filter) {
        try {
            DeleteResult result = collection.deleteOne(filter);
            System.out.println("Deleted " + result.getDeletedCount() + " document(s)");
        } catch (MongoException e) {
            System.out.println("Error deleting document: " + e.getMessage());
        }
    }
}

