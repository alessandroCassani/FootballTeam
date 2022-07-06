package it.com.uninsubria.footballteam

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_visualize_players_details.*
import kotlin.concurrent.thread

class Visualize_players_details : Fragment(){
    private  lateinit var db: DatabaseReference
    private var cf: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cf = it.getString("cf")
        }
        insertData(cf!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showEditTextDialog()
    }

    fun dataChange(info:String, update :String){
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid

        val map: MutableMap<String,Any> = HashMap()
        map[info] = update
        db = FirebaseDatabase.getInstance("https://footballteam-d5795-default-rtdb.firebaseio.com/")
            .getReference("Users")
            .child(uid)
            .child("Atleti")
            .child(cf!!)
        db.updateChildren(map)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_visualize_players_details,container, false)
    }

    private fun insertData(cf: String){
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid
        var atleta :Atleta
        db = FirebaseDatabase.getInstance("https://footballteam-d5795-default-rtdb.firebaseio.com/")
            .getReference("Users")
            .child(uid)
            .child("Atleti")
            .child(cf)
        db.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    atleta = snapshot.getValue(Atleta::class.java)!!
                    nome2.text = atleta.nome!!.toUpperCase()
                    surname2.text = atleta.cognome!!.toUpperCase()
                    cf2.text = atleta.codiceFiscale!!.toUpperCase()
                    dataN2.text = atleta.dataNascita!!
                    ruolo2.text = atleta.ruolo!!.toUpperCase()
                    num2.text = atleta.telefono!!.toUpperCase()
                    ris2.text = atleta.risultati!!.toUpperCase()
                    cert2.text = atleta.certificazioni!!.toUpperCase()

                    Glide.with(imagine.context)
                        .load(atleta!!.immagine)
                        .into(imagine)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TEST","Failed to read value")
            }
        })
    }

    private fun showEditTextDialog(){
        nome2.setOnClickListener{
            val builder = AlertDialog.Builder(this.requireContext())
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text,null)
            val editText = dialogLayout.findViewById<EditText>(R.id.et_edtex)

            with(builder){
                setTitle("Modifica nome")
                setPositiveButton("modifica"){dialog, which ->
                    var str = editText.text.toString()
                    nome2.text = str
                    dataChange("nome",str)
                }
                setNegativeButton("elimina"){dialog,which->
                    Log.d("negativeButton", "negative button clicked")
                }
                setView(dialogLayout)
                show()
            }
        }

        surname2.setOnClickListener{
            val builder = AlertDialog.Builder(this.requireContext())
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text,null)
            val editText = dialogLayout.findViewById<EditText>(R.id.et_edtex)

            with(builder){
                setTitle("Modifica cognome")
                setPositiveButton("modifica"){dialog, which ->
                    val str = editText.text.toString()
                    surname2.text = str
                    dataChange("cognome",str)
                }
                setNegativeButton("elimina"){dialog,which->
                    Log.d("negativeButton", "negative button clicked")
                }
                setView(dialogLayout)
                show()
            }

            dataN2.setOnClickListener{
                val builder = AlertDialog.Builder(this.requireContext())
                val inflater = layoutInflater
                val dialogLayout = inflater.inflate(R.layout.edit_text,null)
                val editText = dialogLayout.findViewById<EditText>(R.id.et_edtex)

                with(builder){
                    setTitle("Modifica data di nascita")
                    setPositiveButton("modifica"){dialog, which ->
                        val str = editText.text.toString()
                        dataN2.text = str
                        dataChange("dataNascita",str)
                    }
                    setNegativeButton("elimina"){dialog,which->
                        Log.d("negativeButton", "negative button clicked")
                    }
                    setView(dialogLayout)
                    show()
                }
            }

            cf2.setOnClickListener{
                val builder = AlertDialog.Builder(this.requireContext())
                val inflater = layoutInflater
                val dialogLayout = inflater.inflate(R.layout.edit_text,null)
                val editText = dialogLayout.findViewById<EditText>(R.id.et_edtex)

                with(builder){
                    setTitle("Modifica codice fiscale")
                    setPositiveButton("modifica"){dialog, which ->
                        val str = editText.text.toString()
                        cf2.text = str
                        dataChange("codiceFiscale",str)
                    }
                    setNegativeButton("elimina"){dialog,which->
                        Log.d("negativeButton", "negative button clicked")
                    }
                    setView(dialogLayout)
                    show()
                }
            }

            ruolo2.setOnClickListener{
                val builder = AlertDialog.Builder(this.requireContext())
                val inflater = layoutInflater
                val dialogLayout = inflater.inflate(R.layout.edit_text,null)
                val editText = dialogLayout.findViewById<EditText>(R.id.et_edtex)

                with(builder){
                    setTitle("Modifica ruolo")
                    setPositiveButton("modifica"){dialog, which ->
                        val str = editText.text.toString()
                        ruolo2.text = str
                        dataChange("ruolo",str)
                    }
                    setNegativeButton("elimina"){dialog,which->
                        Log.d("negativeButton", "negative button clicked")
                    }
                    setView(dialogLayout)
                    show()
                }
            }

            num2.setOnClickListener{
                val builder = AlertDialog.Builder(this.requireContext())
                val inflater = layoutInflater
                val dialogLayout = inflater.inflate(R.layout.edit_text,null)
                val editText = dialogLayout.findViewById<EditText>(R.id.et_edtex)

                with(builder){
                    setTitle("Modifica numero di telefono")
                    setPositiveButton("modifica"){dialog, which ->
                        val str = editText.text.toString()
                        num2.text = str
                        dataChange("telefono",str)
                    }
                    setNegativeButton("elimina"){dialog,which->
                        Log.d("negativeButton", "negative button clicked")
                    }
                    setView(dialogLayout)
                    show()
                }
            }
            cert2.setOnClickListener{
                val builder = AlertDialog.Builder(this.requireContext())
                val inflater = layoutInflater
                val dialogLayout = inflater.inflate(R.layout.edit_text,null)
                val editText = dialogLayout.findViewById<EditText>(R.id.et_edtex)

                with(builder){
                    setTitle("Modifica certificazioni")
                    setPositiveButton("modifica"){dialog, which ->
                        val str = editText.text.toString()
                        cert2.text = str
                        dataChange("certificazioni",str)
                    }
                    setNegativeButton("elimina"){dialog,which->
                        Log.d("negativeButton", "negative button clicked")
                    }
                    setView(dialogLayout)
                    show()
                }
            }

            ris2.setOnClickListener{
                val builder = AlertDialog.Builder(this.requireContext())
                val inflater = layoutInflater
                val dialogLayout = inflater.inflate(R.layout.edit_text,null)
                val editText = dialogLayout.findViewById<EditText>(R.id.et_edtex)

                with(builder){
                    setTitle("Modifica risultati")
                    setPositiveButton("modifica"){dialog, which ->
                        val str = editText.text.toString()
                        ris2.text = str
                        dataChange("risultati",str)
                    }
                    setNegativeButton("elimina"){dialog,which->
                        Log.d("negativeButton", "negative button clicked")
                    }
                    setView(dialogLayout)
                    show()
                }
            }
        }
    }
}