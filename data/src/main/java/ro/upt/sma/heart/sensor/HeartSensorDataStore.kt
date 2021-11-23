package ro.upt.sma.heart.sensor


import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import ro.upt.sma.heart.model.HeartSensorRepository
import java.util.*

class HeartSensorDataStore(context: Context) : HeartSensorRepository {

    // TODO ("Get sensor manager service")
    private val sensorMng: SensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager

    // TODO ("Get a handle for the sensor")
    private val rateSensor: Sensor = sensorMng.getDefaultSensor(Sensor.TYPE_PRESSURE)

    private val listeners = HashMap<HeartSensorRepository.HeartRateListener, SensorEventListener>()

    override fun registerHeartRateListener(listener: HeartSensorRepository.HeartRateListener) {
        if (listeners.containsKey(listener)) {
            return
        }

        val eventListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                listener.onValueChanged(sensorEvent.values[0].toInt())
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }

        //TODO("Register sensor event listener with the sensor manager API")

        sensorMng.registerListener(eventListener, rateSensor, SensorManager.SENSOR_DELAY_NORMAL)

        listeners[listener] = eventListener
    }

    override fun unregisterHeartRateListener(listener: HeartSensorRepository.HeartRateListener) {
        if (listeners.containsKey(listener)) {
            val eventListener = listeners[listener]
            sensorMng?.unregisterListener(eventListener)
            listeners.remove(listener)
        }
    }

}
