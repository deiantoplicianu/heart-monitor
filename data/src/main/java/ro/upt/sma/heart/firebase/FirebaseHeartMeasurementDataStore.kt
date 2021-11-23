package ro.upt.sma.heart.firebase

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList
import ro.upt.sma.heart.model.HeartMeasurement
import ro.upt.sma.heart.model.HeartMeasurementRepository

class FirebaseHeartMeasurementDataStore(userId: String) : HeartMeasurementRepository {

    private val reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child(userId)

    override fun post(heartMeasurement: HeartMeasurement) {
        //TODO("Post the new value to firebase")

        reference.child(heartMeasurement.timestamp.toString()).setValue(heartMeasurement.value)
    }

    override fun observe(listener: HeartMeasurementRepository.HeartChangedListener) {
        //TODO("Add a child event listener and pass the last value to the listener")

        reference.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                listener.onHeartChanged(toHeartMeasurement(p0))
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) { }

            override fun onChildRemoved(p0: DataSnapshot) { }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) { }

            override fun onCancelled(p0: DatabaseError) { }

        })

    }

    override fun retrieveAll(listener: HeartMeasurementRepository.HeartListLoadedListener) {
        //TODO("Retrieve all measurements and pass them to the listener")

        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val child = snapshot.children
                listener.onHeartListLoaded(toHeartMeasurements(child))
                reference.removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
                reference.removeEventListener(this)
            }
        })
    }


    private fun toHeartMeasurements(children: Iterable<DataSnapshot>): List<HeartMeasurement> {
        val heartMeasurementList = ArrayList<HeartMeasurement>()

        for (child in children) {
            heartMeasurementList.add(toHeartMeasurement(child))
        }

        return heartMeasurementList
    }

    private fun toHeartMeasurement(dataSnapshot: DataSnapshot): HeartMeasurement {
        return HeartMeasurement(
                java.lang.Long.valueOf(dataSnapshot.key),
                dataSnapshot.getValue(Int::class.java)!!)
    }

}
