package com.example.uts

import com.example.uts.model.FoodHistory
import com.example.uts.model.User

object FirestoreRepository {

    private val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

    // ================= USER =================
    fun getUser(username: String, onResult: (User?) -> Unit) {
        db.collection("users")
            .document(username)
            .get()
            .addOnSuccessListener {
                onResult(it.toObject(User::class.java))
            }
    }

    fun saveUser(user: User) {
        db.collection("users")
            .document(user.username)
            .set(user)
    }

    // ================= FOOD =================
    fun insertFood(history: FoodHistory) {
        db.collection("food_history")
            .add(history)
    }

    fun getFoodByDate(
        username: String,
        start: Long,
        end: Long,
        onResult: (List<FoodHistory>) -> Unit
    ) {
        db.collection("food_history")
            .whereEqualTo("username", username)
            .whereGreaterThanOrEqualTo("timestamp", start)
            .whereLessThanOrEqualTo("timestamp", end)
            .get()
            .addOnSuccessListener {
                onResult(it.toObjects(FoodHistory::class.java))
            }
    }
}
