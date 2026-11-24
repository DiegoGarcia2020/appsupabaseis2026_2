package com.example.appsupabaseis2026_2

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.appsupabaseis2026_2.data.SupabaseManager
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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
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
    }
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

}