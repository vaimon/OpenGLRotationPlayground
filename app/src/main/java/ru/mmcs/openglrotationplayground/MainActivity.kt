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
    private lateinit var spinnerLight: Spinner
    private lateinit var sbAttenuation2: SeekBar
    private lateinit var sbAttenuation3: SeekBar
    private lateinit var sbMaterial: SeekBar
    private lateinit var sbTexture: SeekBar
    private lateinit var sbStrength: SeekBar
    private lateinit var switchShading: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        glView = findViewById(R.id.glView)
        spinner = findViewById(R.id.spinner)
        sbAttenuation2 = findViewById(R.id.seekAttenuation2)
        sbAttenuation3 = findViewById(R.id.seekAttenuation3)
        sbMaterial = findViewById(R.id.seekOriginMaterial)
        sbTexture = findViewById(R.id.seekOriginTexture)
        spinnerLight = findViewById(R.id.spinnerLight)
        sbStrength = findViewById(R.id.seekOriginStrength)
        switchShading = findViewById(R.id.switchShading)

        switchShading.setOnCheckedChangeListener { _, b ->
            GLRenderer.shadingMode = if (b) ShadingMode.Goureaux else ShadingMode.Phong
            glView.updateLightingModel()
        }

        spinnerLight.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("Ламберт", "Фонг", "Блинн-Фонг", "Туншейдинг", "Фонг + текстуры"));
        spinnerLight.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                (p0?.getChildAt(0) as TextView?)?.setTextColor(Color.WHITE)
                when(p0?.selectedItemPosition){
                    0 -> GLRenderer.lightingMode = LightingMode.Lambert
                    1 -> GLRenderer.lightingMode = LightingMode.Phong
                    2 -> GLRenderer.lightingMode = LightingMode.BlinnPhong
                    3 -> GLRenderer.lightingMode = LightingMode.ToonShading
                    4 -> GLRenderer.lightingMode = LightingMode.TexturePhong
                }
                glView.updateLightingModel()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                p0?.setSelection(0)
            }

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
                GLRenderer.lightAttenuation = floatArrayOf(GLRenderer.lightAttenuation[0], GLRenderer.lightAttenuation[1], (p0?.progress?.toFloat() ?: 0f) / 1000f)
                glView.requestRender()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

        sbTexture.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                GLRenderer.textureContribution = (p0?.progress?.toFloat() ?: 0f) / 100f
                glView.requestRender()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

        sbMaterial.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                GLRenderer.materialContribution = (p0?.progress?.toFloat() ?: 0f) / 100f
                glView.requestRender()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }
}