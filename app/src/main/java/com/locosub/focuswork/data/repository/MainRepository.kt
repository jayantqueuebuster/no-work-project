package com.locosub.focus_work.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import com.locosub.focus_work.common.Result
import com.locosub.focuswork.data.models.Task
import kotlinx.coroutines.channels.awaitClose

class MainRepository @Inject constructor(
    private val db: DatabaseReference
) {


    fun addTask(task: Task): Flow<Result<String>> = callbackFlow {
        trySend(Result.Loading)

        db.push().setValue(task)
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


    fun getTask(): Flow<Result<List<Task.TaskResponse>>> = callbackFlow {

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

        db.addValueEventListener(valueEvent)
        awaitClose {
            db.removeEventListener(valueEvent)
            close()
        }
    }

    fun deleteTask(key: String): Flow<Result<String>> = callbackFlow {

        trySend(Result.Loading)

        db.child(key).removeValue()
            .addOnCompleteListener {
                trySend(Result.Success("Task Deleted!!"))
            }.addOnFailureListener {
                trySend(Result.Failure(it))
            }
        awaitClose {
            close()
        }
    }

    fun updateTask(task: Task.TaskResponse): Flow<Result<String>> = callbackFlow {
        trySend(Result.Loading)
        val map = HashMap<String, Any>()
        map["title"] = task.task?.title!!
        map["description"] = task.task.description
        map["completed"] = task.task.completed

        db.child(task.key).updateChildren(
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

