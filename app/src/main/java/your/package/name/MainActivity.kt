package your.package.name

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // You need a layout file named activity_main.xml in app/src/main/res/layout/
        // This layout should contain a button with id 'btnStartStopwatch'
        // setContentView(R.layout.activity_main)

        // Example: Manually creating a button for demonstration if activity_main.xml is not available yet
        val button = Button(this)
        button.id = R.id.btnStartStopwatch
        button.text = "Start Stopwatch Service"
        setContentView(button)

        findViewById<Button>(R.id.btnStartStopwatch).setOnClickListener {
            val serviceIntent = Intent(this, StopwatchService::class.java).apply {
                action = StopwatchService.ACTION_START
            }
            startService(serviceIntent)
        }
    }
}
