**MongoDB user interface (Compass/Atlas)** to create a collection and perform basic CRUD operations.

---

# MongoDB Lab Exercises – Using MongoDB Compass

## Objective

In this lab, you will:

- Connect to MongoDB instance
- Create a new database and collection.
- Insert, read, update, and delete documents using the MongoDB user interface.
- Understand how MongoDB documents are structured.

---

## Lab 1 - Connecting to MongoDB Compass using a Connection String (URI)

1. Launch **MongoDB Compass**.
2. Click on **Add new connection**.
3. On the **Welcome / New Connection** screen, you will see a URI field. For local MongoDB instance, paste

```text
mongodb://localhost:27017
```

---

## Lab 2: Create a Database and Collection

1. Click **+** sign to Create Database
   - Database Name: `finance_db`
   - Collection Name: `transactions`
2. Click **Create Database** to confirm.

---

## Lab 3: Insert Documents

1. Select `finance_db` database → `transactions` collection.
2. Click **Add Data**, **Insert Document**
3. Add the following document:

```json
{
  "txnId": "T1001",
  "accountId": "A5001",
  "type": "Credit",
  "amount": 2500.75,
  "currency": "INR",
  "date": "2025-09-29T10:15:30Z",
  "status": "SUCCESS",
  "channel": "MobileBanking",
  "remarks": "Salary credit",
  "address": {
    "city": "Mumbai",
    "state": "Maharashtra",
    "country": "India"
  },
  "tags": ["monthly", "salary", "priority"]
}
```

## Lab 4: Import transactions from JSON document

1. Click on the ADD DATA button
2. Select Import JSON or CSV
3. In the pop-up window: Browse and choose transactions.json

Click Import

---

## Lab 5: Read (Query) Documents

1. In the search bar, write queries to filter results:

   - Find all `Credit` transactions:

     ```json
     { "type": "Credit" }
     ```

     or

     ```json
     { "type": { "$eq": "Credit" } }
     ```

   - Find all transactions where `amount > 2000`:

     ```json
     { "amount": { "$gt": 2000 } }
     ```

   - Find all transactions based on embedded documnets. Find transactions done in `Mumbai`:

```json
{ "address.city": "Mumbai" }
```

- Find all transactions tagged as `"salary"`

```json
{ "tags": "salary" }
```

- Find all transactions that have only `"food"` and `"shopping"` tags

```json
{ "tags": { "$all": ["food", "shopping"] } }
```

- Find transactions that contain any of these tags (`"food"` or `"shopping"`)

```json
{ "tags": { "$in": ["food", "shopping"] } }
```

- Find transactions where the array contains exactly **3 elements**

```json
{ "tags": { "$size": 3 } }
```

---

## Lab 6: Update Documents

1. Select one of your documents in the collection.
2. Click **Edit icon**.
3. Change:

   - `status` from `"SUCCESS"` to `"FAILED"`.
   - `remarks` from `"Travel booking"` to `"Hotel confirmation"`.
   - Add new element to `tags` of `bonus`

4. Save changes.

---

## Lab 7: Delete Documents

1. Select a document you want to remove.
2. Click **Delete icon**.
3. Confirm deletion.
4. Verify by running:

---

## Lab 8: Bonus – Aggregation

1. Go to the **Aggregation** tab in Compass.
2. Build a pipeline:

   - **Stage 1 (Match):**

     ```json
     { "type": "Debit" }
     ```

   - **Stage 2 (Group):**

     ```json
     { "_id": "$accountId", "totalDebits": { "$sum": "$amount" } }
     ```

3. Run pipeline → See total debit amount per account.

---

## Summary

We have learnt :

- Create a MongoDB database and collection.
- Perform **CRUD** operations (Create, Read, Update, Delete).
- Run basic queries and an aggregation.

---
