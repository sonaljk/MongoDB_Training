---

````markdown
# Lab: Java Application to Connect to MongoDB (`finance_db.transactions`)

In this lab, we will create a simple Java application that 
- connects to MongoDB
- inserts a documents into the `transactions` collection
- reads and filters documents,
- updates documment
- deletes document

---

## Step 1: Add Mongodb Maven dependency 

1. Open mongo-java-starter project and add this dependency in `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver-sync</artifactId>
        <version>4.11.1</version>
    </dependency>
</dependencies>
````
Reload maven project to download the dependency.

## Step 2: Add code to connect to MongoDB server 

Open file **`MongoJavaApp.java`** and write below code

### 1. Connect to finance_db database and transactions collection 

Define static variables to store the uri, database name and collection name
Create a method to get the collection object for `transactions` collection to demonstrate CRUD operations

```java
public class Main {

    private static final String URI = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "finance_db";
    private static final String COLLECTION_NAME = "transactions";

    public static void main(String[] args) {
        MongoCollection<Document> collection = getCollection(URI, DATABASE_NAME, COLLECTION_NAME);
        System.out.println("Initial document count: " + collection.countDocuments());
    }

    private static MongoCollection<Document> getCollection(String uri, String databaseName, String collectionName) {
        MongoClient mongoClient = MongoClients.create(uri);
        

        MongoDatabase database = mongoClient.getDatabase(databaseName);
        System.out.println("\nDatabases:");
        mongoClient.listDatabaseNames().forEach(System.out::println);

        MongoCollection<Document> collection = database.getCollection(collectionName);
        System.out.println("\nCollections in " + databaseName + ":");
        database.listCollectionNames().forEach(System.out::println);

        return collection;
    }
    
}
```

* Create a MongoClient object to connect to the `URI` specified
* MongoClients.create(URI) is a static factory method that returns a MongoClient instance.
* MongoClient object is used to get MongoDatabase object (finance_db)
* Inside the database, data is stored in **collections** (similar to SQL tables).
* MongoDatabase object is used to get MongoCollection object (transactions)
---


## Step 3: Insert a Document

Next, we construct a Document object with the fields and values that we want to store and insert into collection

Each document must contain a unique _id value and we can let the driver generate it automatically.

Code snippet 
```java
// Create a sample transaction document
public static void main(String[] args) {
Document txn = new Document("txnId", "T2001")
        .append("accountId", "A5001")
        .append("type", "Credit")
        .append("amount", 3000.75)
        .append("currency", "INR")
        .append("date", new Date())
        .append("status", "SUCCESS")
        .append("channel", "MobileBanking")
        .append("remarks", "Salary credit");
insertDocument(collection, txn);
}

private static void insertDocument(MongoCollection<Document> collection, Document document) {
    InsertOneResult result = collection.insertOne(document);
    System.out.println("Inserted a document with the following id: " + result.getInsertedId().asObjectId().getValue());
}
```

✅ This inserts a new document into the collection and prints the generated _id value.

---

## Step 4: Insert multiple documents
You can also insert multiple documents at once using `insertMany` method.

```java
public static void main(String[] args) {
    // Insert multiple documents
    // Insert multiple documents
    Document txn2 = new Document("txnId", "T2002")
            .append("accountId", "A5002")
            .append("type", "Debit")
            .append("amount", 1500.00)
            .append("currency", "INR")
            .append("date", Date.from(Instant.now()))
            .append("status", "PENDING")
            .append("channel", "ATM")
            .append("remarks", "ATM Withdrawal");

    Document txn3 = new Document("txnId", "T2003")
            .append("accountId", "A5003")
            .append("type", "Credit")
            .append("amount", 5000.00)
            .append("currency", "INR")
            .append("date", Date.from(Instant.now()))
            .append("status", "FAILED")
            .append("channel", "OnlineTransfer")
            .append("remarks", "Project payment");

    Document txn4 = new Document("txnId", "T2003")
            .append("accountId", "A5003")
            .append("type", "Debit")
            .append("amount", 5000.00)
            .append("currency", "INR")
            .append("date", Date.from(Instant.now()))
            .append("status", "SUCCESS")
            .append("channel", "OnlineTransfer")
            .append("remarks", "Project payment");

    insertManyDocuments(collection, Arrays.asList(txn2, txn3, txn4));
}

private static void insertManyDocuments(MongoCollection<Document> collection, java.util.List<Document> documents) {
    InsertManyResult result = collection.insertMany(documents);
    System.out.println("Inserted count: " + result.getInsertedIds().size());
}
```

## Step 5: Find All Documents

Add this code next:

```java
public static void main(String[] args) {
    // Find all documents
    System.out.println("\nAll transactions:");
    findDocuments(collection, new Document());
}

private static void findDocuments(MongoCollection<Document> collection, Bson filter) {
    collection.find(filter).forEach(doc -> System.out.println(doc.toJson()));
}
```

---

## Step 6: Find all documents with a filter

```java
public static void main(String[] args) {
    // Find transactions above 2500
    System.out.println("\nTransactions above 2500:");
    findDocuments(collection, Filters.gt("amount", 2500));

    // Find transactions of type Credit and status SUCCESS
    System.out.println("\nTransactions of type Credit and status SUCCESS:");
    findDocuments(collection, Filters.and(Filters.eq("type", "Credit"), Filters.eq("status", "SUCCESS")));
}
```

This uses Filters class to create filter conditions.


---

## Step 7: Find all documents and choose specific fields

```java
public static void main(String[] args) {
    // find all documents and print only the txnId and amount fields
    System.out.println("\nAll transactions (txnId and amount only):");
    findDocuments(collection, new Document(), Projections.include("txnid", "amount"));
}

private static void findDocuments(MongoCollection<Document> collection, Bson filter, Bson projection) {
    collection.find(filter).projection(projection).forEach(doc -> System.out.println(doc.toJson()));
}
```

This uses Projections class to specify which fields to include in the output.

---

## Step 9 - Aggregation using MongoDB

```java
public static void main(String[] args) {
    // Aggregation: Total amount of successful credits
    System.out.println("\nTotal amount of successful credits:");
    sumOfTransactions(collection);
}
private static void sumOfTransactions(MongoCollection<Document> collection) {
    double totalSuccessAmount = collection.aggregate(Arrays.asList(
            new Document("$match", new Document("status", "SUCCESS")),
            new Document("$group", new Document("_id", null).append("total", new Document("$sum", "$amount")))
    )).first().getDouble("total");
    System.out.println("Total successful amount: " + totalSuccessAmount);
}
```

---
## Step 8: Update a Document

Update the status of the transaction we inserted:

Define the filter condition and the update operation

```java
public static void main(String[] args) {
    // Update a document
    System.out.println("\nUpdating remarks of transaction T2001:");
    updateDocument(collection, Filters.eq("txnId", "T2001"), Updates.set("remarks", "Updated Salary credit"));
}

private static void updateDocument(MongoCollection<Document> collection, Bson filter, Bson update) {
    UpdateResult result = collection.updateOne(filter, update);
    System.out.println("Matched " + result.getMatchedCount() + " document(s)");
    if (result.getMatchedCount() > 0) {
        System.out.println("After update the document is: " + collection.find(filter).first().toJson());
    }
}
```

✅ This modifies the inserted transaction’s remarks.

---

## Step 9: Delete a Document

Finally, delete the transaction we created:

```java
public static void main(String[] args) {
    // Delete a document
    System.out.println("\nDeleting transaction T2001:");
    deleteDocument(collection, Filters.eq("txnId", "T2001"));
}

private static void deleteDocument(MongoCollection<Document> collection, Bson filter) {
    DeleteResult result = collection.deleteOne(filter);
    System.out.println("Deleted " + result.getDeletedCount() + " document(s)");
}
```

---

Expected output includes:

* Confirmation of insertion
* 
* All documents printed
* Filtered transactions (`amount > 2500`)
* Filtered transactions (`type = Credit` and `status = SUCCESS`)
* Updated document
* Confirmation of deletion

---
