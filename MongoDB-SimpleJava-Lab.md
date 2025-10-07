# MongoDB Java Lab: Step-by-Step Instructions (Function-based)

This lab will guide you to create a **Java application** that connects to MongoDB, performs CRUD operations, and works with nested fields and arrays. Each operation will be implemented as a separate function and invoked from `main()`.

---

## Step 1: Create a new Java project

1. Use IntelliJ IDEA or your preferred IDE to create a new Maven project.
   Set the `groupId` to `com.training` and `artifactId` to `mongo-java-app`.
2. Add **Maven support** to manage dependencies.
3. Add the MongoDB Java driver dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongodb-driver-sync</artifactId>
    <version>4.11.0</version>
</dependency>
```

---

## Step 2: Configure logging

Create a file `src/main/resources/application.properties` with the following content:

```properties
logging.level.org.mongodb.driver=warn
```

This will set the MongoDB driver logging level to WARN.

---

## Step 3: Create the main class

1. Create a class `MongoJavaApp` inside the package com.training and define the database connection details

```java
package com.training;

public class MongoJavaApp {

    // Static constants for MongoDB connection
    private static final String URI = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "finance_db";
    private static final String COLLECTION_NAME = "transactions";

    // MongoClient
    private static com.mongodb.client.MongoClient mongoClient;

    // Empty main method
    public static void main(String[] args) {
        // CRUD operations will be added in later steps
    }
}
```

---

## Step 4: Connect to MongoDB

Add a function to connect to MongoDB and return a collection:

```java
private static MongoCollection<Document> connectToMongo(String uri, String dbName, String collName) {
    mongoClient = MongoClients.create(uri);
    MongoDatabase database = mongoClient.getDatabase(dbName);
    return database.getCollection(collName);
}
```

In the `main` method, call the `getCollection` method and print the current count of documents

```java
   public static void main(String[] args) {
    MongoCollection<Document> collection = connectToMongo(URI, DATABASE_NAME, COLLECTION_NAME);
    System.out.println("Initial document count: " + collection.countDocuments());
   }
```

---

## Step 5: Insert a single document

- Create a method to insert a single document

```java
  private static void insertDocument(MongoCollection<Document> collection, Document document) {
      try {
         collection.insertOne(document);
         System.out.println("Document inserted: " + document.toJson());
      } catch (MongoException e){
         System.out.println("Error inserting document: " + e.getMessage());
      }
  }
```

In the `main` method, create a new document and call the `insertDocument` method

```java
    Document txn = new Document("txnId", "T2001")
        .append("accountId", "A5001")
        .append("type", "Credit")
        .append("amount", 3000.75)
        .append("currency", "INR")
        .append("date", java.util.Date.from(java.time.Instant.now()))
        .append("status", "SUCCESS")
        .append("channel", "MobileBanking")
        .append("remarks", "Salary credit")
        .append("address", new Document("city", "Mumbai")
            .append("state", "Maharashtra")
            .append("country", "India"))
        .append("tags", java.util.Arrays.asList("salary", "credit", "monthly"));

    insertDocument(collection, txn);
```

---

## Step 6: Insert multiple documents

- Create a method to accept collection and a list of documents to insert multiple documents

```java
private static void insertManyDocuments(MongoCollection<Document> collection, java.util.List<Document> documents) {
        try {
            InsertManyResult result = collection.insertMany(documents);
            System.out.println("Inserted count: " + result.getInsertedIds().size());
        } catch (MongoException e) {
            System.out.println("Error inserting documents: " + e.getMessage());
        }
    }
```

In the `main` method, create 2 documents and call the `insertManyDocuments` method

```java
Document txn2 = new Document("txnId", "T2002")
.append("accountId", "A5002")
.append("type", "Debit")
.append("amount", 1500.00)
.append("currency", "INR")
.append("date", java.util.Date.from(java.time.Instant.now()))
.append("status", "PENDING")
.append("channel", "ATM")
.append("remarks", "ATM Withdrawal")
.append("address", new Document("city", "Delhi")
.append("state", "Delhi")
.append("country", "India"))
.append("tags", java.util.Arrays.asList("withdrawal", "debit", "atm"));

Document txn3 = new Document("txnId", "T2003")
.append("accountId", "A5003")
.append("type", "Credit")
.append("amount", 5000.00)
.append("currency", "INR")
.append("date", java.util.Date.from(java.time.Instant.now()))
.append("status", "FAILED")
.append("channel", "OnlineTransfer")
.append("remarks", "Project payment")
.append("address", new Document("city", "Bangalore")
.append("state", "Karnataka")
.append("country", "India"))
.append("tags", java.util.Arrays.asList("project", "credit", "online"));

InsertManyResult result = insertManyDocuments(collections, Arrays.asList(txn2, txn3));
```

---

## Step 7: Read documents

### Read all documents (no filter)

```java
private static void findDocuments(MongoCollection<Document> collection, Bson filter) {
   try {
            collection.find(filter).forEach(doc -> System.out.println(doc.toJson()));
        } catch (MongoException e) {
            System.out.println("Error finding documents: " + e.getMessage());
 }
}
```

In the `main` method, call the `findDocuments` method and pass an empty filter

```java
// Find all documents
        System.out.println("\nAll transactions:");
        findDocuments(collection, new Document());
```

### 7.2 Read filtered documents

In the `main` method, call the `findDocuments` method and pass appropriate filter

```java
// Find transactions above 2500
System.out.println("\nTransactions above 2500:");
findDocuments(collection, Filters.gt("amount", 2500));

// find all documents where address.city is Mumbai
System.out.println("\nTransactions from Mumbai:");
findDocuments(collection, Filters.eq("address.city", "Mumbai"));

// Find transactions of type Credit and status SUCCESS
System.out.println("\nTransactions of type Credit and status SUCCESS:");
findDocuments(collection, Filters.and(Filters.eq("type", "Credit"), Filters.eq("status", "SUCCESS")));
```

### 7.3 Read with projection (selecting which fields to display in output)

```java
private static void findDocuments(MongoCollection<Document> collection, Bson filter, Bson projection) {
        try {
            collection.find(filter).projection(projection).forEach(doc -> System.out.println(doc.toJson()));
        } catch (MongoException e) {
            System.out.println("Error finding documents: " + e.getMessage());
        }
    }
```

In the `main` method, call the `findDocuments` method and pass empty filter and appropriate projection

```java
// find all documents and print only the txnId and amount fields
System.out.println("\nAll transactions (txnId and amount only):");
findDocuments(collection, new Document(), Projections.include("txnid", "amount"));
```

---

## Step 8: Aggregate operations

Define a function to find sum total of all transactions

```java
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
```

In the `main` method, call the `sumOfTransactions` method and pass appropriate filter and projection

```java
// aggregate to find total amount of successful transactions
System.out.println("\nTotal amount of successful transactions:");
sumOfTransactions(collection);
```

---

## Step 9: Update documents

```java
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
```

In the `main` method, call the `updateDocument` method passing the filter and updated values

```java
// Update a document
System.out.println("\nUpdating remarks of transaction T2001:");
updateDocument(collection, Filters.eq("txnId", "T2001"), Updates.set("remarks", "Updated Salary credit"));

// Update the address field to include a new nested field (zipCode)
updateDocument(collection, Filters.eq("txnId", "T2001"), Updates.set("address.zipCode", "400001"));

// Add a new nested object (contact) to the document
updateDocument(collection, Filters.eq("txnId", "T2001"), Updates.set("contact",
               new Document("email", "user@example.com")
              .append("phone", "1234567890")));

```

## Step 10: Delete documents

```java
private static void deleteDocument(MongoCollection<Document> collection, Bson filter) {
        try {
            DeleteResult result = collection.deleteOne(filter);
            System.out.println("Deleted " + result.getDeletedCount() + " document(s)");
        } catch (MongoException e) {
            System.out.println("Error deleting document: " + e.getMessage());
        }
    }
```

In the `main` method, call the `deleteDocument` method passing the filter

```java
//         Delete a document
            System.out.println("\nDeleting transaction T2001:");
            deleteDocument(collection, Filters.eq("txnId", "T2001"));
```

## Step 11 : Close the connection

Always clean up by closing the connection

```java
mongoClient.close();
```
