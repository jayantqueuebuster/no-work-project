package com.locosub.focuswork.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import com.locosub.focus_work.common.Result
import com.locosub.focuswork.data.models.Questions
import com.locosub.focuswork.data.models.Task
import com.locosub.focuswork.utils.QUESTIONS
import com.locosub.focuswork.utils.TASK
import kotlinx.coroutines.channels.awaitClose

class MainRepository @Inject constructor(
    private val db: DatabaseReference
) {


    fun addTask(task: Task): Flow<Result<String>> = callbackFlow {
        trySend(Result.Loading)

        db.child(TASK).push().setValue(task)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    trySend(Result.Success("Task Added!"))
            }.addOnFailureListener {
                trySend(Result.Failure(it))
            }

        awaitClose {
            close()
        }
    }

    suspend fun getQuestions(): Flow<Result<List<List<Questions.QuestionResponse>>>> =
        callbackFlow {
            trySend(Result.Loading)

            val valueEvent = object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = snapshot.children.map {
                        //  Log.d("main", "onDataChange:${it.getValue(Questions::class.java)} ")

                        it.children.map { res ->
                            Log.d("main", "onDataChange: ${res} ")
                            Questions.QuestionResponse(
                                res.getValue(Questions::class.java) ?: Questions(),
                                it.key ?: "0"
                            )
                        }
                    }
                    trySend(Result.Success(items))
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("main", "onDataChange: $error")
                    trySend(Result.Failure(error.toException()))
                }

            }
            db.child(QUESTIONS).addValueEventListener(valueEvent)
            awaitClose {
                db.child(QUESTIONS).removeEventListener(valueEvent)

            }
        }


    suspend fun getTask(): Flow<Result<List<Task.TaskResponse>>> = callbackFlow {

        trySend(Result.Loading)

        val valueEvent = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.map {
                    Task.TaskResponse(
                        it.getValue(Task::class.java),
                        key = it.key ?: "0"
                    )
                }
                trySend(Result.Success(items))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.Failure(error.toException()))
            }

        }

        db.child(TASK).addValueEventListener(valueEvent)
        awaitClose {
            db.child(TASK).removeEventListener(valueEvent)
            close()
        }
    }

    suspend fun deleteTask(key: String): Flow<Result<String>> = callbackFlow {

        trySend(Result.Loading)

        db.child(TASK).child(key).removeValue()
            .addOnCompleteListener {
                trySend(Result.Success("Task Deleted!!"))
            }.addOnFailureListener {
                trySend(Result.Failure(it))
            }
        awaitClose {
            close()
        }
    }

    suspend fun deleteQuestions(key: String): Flow<Result<String>> = callbackFlow {
        trySend(Result.Loading)

        db.child(QUESTIONS).child(key).removeValue()
            .addOnCompleteListener {
                trySend(Result.Success("Deleted!!"))
            }.addOnFailureListener {
                trySend(Result.Failure(it))
            }
        awaitClose {
            close()
        }
    }

    suspend fun updateTask(task: Task.TaskResponse): Flow<Result<String>> = callbackFlow {
        trySend(Result.Loading)
        val map = HashMap<String, Any>()
        map["title"] = task.task?.title!!
        map["description"] = task.task.description
        map["completed"] = task.task.completed

        db.child(TASK).child(task.key).updateChildren(
            map
        ).addOnCompleteListener {
            trySend(Result.Success("Task Update!!"))
        }.addOnFailureListener {
            trySend(Result.Failure(it))
        }
        awaitClose {
            close()
        }
    }

}

