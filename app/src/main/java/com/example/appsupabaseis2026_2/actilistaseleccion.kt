package com.example.appsupabaseis2026_2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.LruCache
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.appsupabaseis2026_2.data.SupabaseManager
import com.example.appsupabaseis2026_2.models.Alumno
import com.example.appsupabaseis2026_2.models.Materia
import com.example.appsupabaseis2026_2.utils.SupabaseErrorHandler
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch

class actilistaseleccion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actilistaseleccion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            // el encabezado verde cubre la barra de estado
            findViewById<View>(R.id.header).setPadding(
                dp(16), systemBars.top + dp(12), dp(16), dp(16)
            )
            insets
        }

        val actNiveles = findViewById<AutoCompleteTextView>(R.id.listaNiveles)

        actNiveles.setOnItemClickListener { parent, _, position, id->
            val nivelSeleccionado = parent.getItemAtPosition(position).toString()

            Toast.makeText(
                this,
                "Nivel seleccionado: $nivelSeleccionado",
                Toast.LENGTH_LONG).show()

            llenarmaterias(position + 1)
        }
        val lvAlumnos = findViewById<ListView>(R.id.lstAlumno)
        lvAlumnos.setOnItemClickListener { parent, _, position, _ ->
            val alumno = parent.getItemAtPosition(position) as? Map<*, *>
                ?: return@setOnItemClickListener
            Toast.makeText(this, "Item seleccionado: ${alumno["nombres"]}",
                Toast.LENGTH_SHORT).show()
        }
        cargarAlumnos()

    }
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    fun llenarmaterias (nivel :Int){
        val autoCompleteTextViewMaterias = findViewById<AutoCompleteTextView>(R.id.listaMaterias)
        val lstMaterias = ArrayList<String>()

        lifecycleScope.launch {
            try {
                val listaMaterias = ArrayList(
                    SupabaseManager.client.from("materias")
                        .select {
                            filter {
                                eq("nivel", nivel)
                            }
                            order("nombre", Order.ASCENDING)
                        }
                        .decodeList<Materia>()
                )
                for (materia in listaMaterias) {
                    lstMaterias.add(materia.nombre ?: "")
                }
            } catch (e: RestException) {
                SupabaseErrorHandler.show(this@actilistaseleccion, e)
                lstMaterias.clear()
            } finally {
                val adapter = ArrayAdapter(
                    this@actilistaseleccion,
                    android.R.layout.simple_spinner_dropdown_item,
                    lstMaterias
                )
                autoCompleteTextViewMaterias.setAdapter(adapter)
            }
        }
    }

    private val cacheFotos = LruCache<String, Bitmap>(50)

    private fun cargarFoto(img: ImageView, url: String?) {
        img.tag = url
        img.setImageBitmap(null)
        if (url.isNullOrBlank()) return
        cacheFotos.get(url)?.let { img.setImageBitmap(it); return }
        lifecycleScope.launch {
            val bmp = withContext(Dispatchers.IO) {
                try {
                    URL(url).openStream().use { BitmapFactory.decodeStream(it) }
                } catch (e: Exception) {
                    null
                }
            } ?: return@launch
            cacheFotos.put(url, bmp)
            // la fila pudo reciclarse para otro alumno mientras descargaba
            if (img.tag == url) img.setImageBitmap(bmp)
        }
    }

    private fun cargarAlumnos() {
        lifecycleScope.launch {
            try {
                val alumnos = SupabaseManager.client.from("alumnos")
                    .select { order("nombres", Order.ASCENDING) }
                    .decodeList<Alumno>()
                val datos = alumnos.map {
                    mapOf("id" to it.id,
                        "nombres" to it.nombres.orEmpty(),
                        "correo" to it.correo.orEmpty(),
                        "paralelo" to "Paralelo: ${it.paralelo}",
                        "telefono" to it.telefono.orEmpty(),
                        "foto" to it.foto.orEmpty())
                }
                val adapter = SimpleAdapter(
                    this@actilistaseleccion, datos, R.layout.lyalumno,
                    arrayOf("nombres", "paralelo", "correo", "telefono", "foto"),
                    intArrayOf(R.id.txtNombre, R.id.txtParalelo, R.id.txtCorreo,
                        R.id.txtTelefono, R.id.imgFoto)
                )
                adapter.viewBinder = SimpleAdapter.ViewBinder { view, data, _ ->
                    if (view is ImageView) {
                        cargarFoto(view, data as? String)
                        true
                    } else false
                }
                findViewById<ListView>(R.id.lstAlumno).adapter = adapter
            } catch (e: RestException) {
                SupabaseErrorHandler.show(this@actilistaseleccion, e)
            }
        }
    }


}