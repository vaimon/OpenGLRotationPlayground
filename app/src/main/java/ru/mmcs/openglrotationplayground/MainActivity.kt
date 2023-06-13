package ru.mmcs.openglrotationplayground

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import ru.mmcs.openglnextplayground.*

class MainActivity : AppCompatActivity() {
    private lateinit var glView: OpenGLView
    private lateinit var spinner: Spinner
    private lateinit var sbAttenuation: SeekBar
    private lateinit var sbAttenuation2: SeekBar
    private lateinit var sbAttenuation3: SeekBar
    private lateinit var sbStrength: SeekBar
    private lateinit var switchShading: SwitchCompat
    private lateinit var switchLighting: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        glView = findViewById(R.id.glView)
        spinner = findViewById(R.id.spinner)
        sbAttenuation = findViewById(R.id.seekAttenuation)
        sbAttenuation2 = findViewById(R.id.seekAttenuation2)
        sbAttenuation3 = findViewById(R.id.seekAttenuation3)
        sbStrength = findViewById(R.id.seekOriginStrength)
        switchShading = findViewById(R.id.switchShading)
        switchLighting = findViewById(R.id.switchLighting)

        switchShading.setOnCheckedChangeListener { _, b ->
            GLRenderer.shadingMode = if (b) ShadingMode.Goureaux else ShadingMode.Phong
            glView.updateLightingModel()
        }

        switchLighting.setOnCheckedChangeListener { _, b ->
            GLRenderer.lightingMode = if (b) LightingMode.Phong else LightingMode.Lambert
            glView.updateLightingModel()
        }

        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("Все объекты", "Каждый объект", "Мир"));
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                (p0?.getChildAt(0) as TextView?)?.setTextColor(Color.WHITE)
                when(p0?.selectedItemPosition){
                    0 -> GLRenderer.rotationCenter = RotationCenter.ComplexObject
                    1 -> GLRenderer.rotationCenter = RotationCenter.SingleObject
                    2 -> GLRenderer.rotationCenter = RotationCenter.World
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                p0?.setSelection(0)
            }

        }

        sbStrength.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val ambientStrength = (p0?.progress?.toFloat() ?: 0f) / 100f
                GLRenderer.lightAmbient = floatArrayOf(ambientStrength, ambientStrength, ambientStrength, 1f)
                glView.requestRender()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

        sbAttenuation.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                GLRenderer.lightAttenuation = floatArrayOf((p0?.progress?.toFloat() ?: 0f) / 100f, GLRenderer.lightAttenuation[1], GLRenderer.lightAttenuation[2])
                glView.requestRender()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

        sbAttenuation2.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                GLRenderer.lightAttenuation = floatArrayOf(GLRenderer.lightAttenuation[0], (p0?.progress?.toFloat() ?: 0f) / 100f, GLRenderer.lightAttenuation[2])
                glView.requestRender()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

        sbAttenuation3.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                GLRenderer.lightAttenuation = floatArrayOf(GLRenderer.lightAttenuation[0], GLRenderer.lightAttenuation[1], (p0?.progress?.toFloat() ?: 0f) / 100f)
                glView.requestRender()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }
}