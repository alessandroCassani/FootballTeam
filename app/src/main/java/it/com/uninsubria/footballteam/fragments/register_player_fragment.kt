package it.com.uninsubria.footballteam.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import it.com.uninsubria.footballteam.R
import kotlinx.android.synthetic.main.register_player_fragment.*
import kotlinx.android.synthetic.main.register_player_fragment.view.*
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap
import kotlin.concurrent.thread

class register_player_fragment : Fragment() {

    private val FOTO = 1
    private val ref =
        FirebaseDatabase.getInstance("https://footballteam-d5795-default-rtdb.firebaseio.com/")
            .getReference("Users")
    private lateinit var auth: FirebaseAuth
    private lateinit var img: Uri
    private lateinit var name: EditText
    private lateinit var surname: EditText
    private lateinit var codiceFiscale: EditText
    private lateinit var birthDate: TextView
    private lateinit var phoneNumber: EditText
    private lateinit var immagine: ImageView
    private lateinit var role: EditText
    private lateinit var certification: EditText
    private lateinit var results: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.register_player_fragment, container, false)
        auth = Firebase.auth
        immagine = view.findViewById<ImageView>(R.id.immagine)
        name = view.findViewById<EditText>(R.id.nome)
        surname = view.findViewById<EditText>(R.id.cognome)
        codiceFiscale = view.findViewById<EditText>(R.id.cf)
        phoneNumber = view.findViewById<EditText>(R.id.phone)
        role = view.findViewById<EditText>(R.id.ruolo)
        certification = view.findViewById<EditText>(R.id.certificazione)
        birthDate = view.findViewById<TextView>(R.id.dataNascita)
        results = view.findViewById<EditText>(R.id.risultati)


        val main = AtletiFragment()

        view.immagine.setOnClickListener {
            openGalleryForImage()
        }
        birthDate.setOnClickListener {
            dataPicker()
        }

        view.register.setOnClickListener {
            onRegisterClick()
            //creazioneFragment(main)
        }
        return view
    }

    private fun onRegisterClick() {
        var check = true
        if(!checkImage()) {

            check = false
        }
        if (!checkName()) {
            name.error = "inserire nome"
            check = false
        }
        if (!checkSurname()) {
            cognome.error = "inserire cognome"
            check = false
        }
        if (!checkRole()) {
            ruolo.error = "inserire ruolo"
            check = false
        }

        if(codiceFiscale.text.isEmpty()||codiceFiscale.text.length!=16) {
            cf.error = "codice fiscale non corretto o inesistente"
            check = false
        }

        if (!checkPhone()) {
            phone.error = "inserire numero di telefono"
            check = false
        }

        if(!checkCertficazione()) {
            certificazione.error = "inserire certificazione"
            check = false
        }

        if(!checkResults()) {
            risultati.error = "inserire risultati ottenuti"
            check = false
        }


        val TAG = "FirebaseStorageManager"
        val ref =
            FirebaseStorage.getInstance().reference.child("/image/${name}")


        // caricamento dell'immagine


        ref.putFile(img).addOnSuccessListener {
            Log.e(TAG, "OK")
            thread(start=true){
                ref.downloadUrl.addOnSuccessListener {
                    Log.e(TAG,"$it")
                    if(check) {
                        Toast.makeText(view?.context,"Aggiunto",Toast.LENGTH_SHORT).show()
                        saveData(
                            name.text.toString(), surname.text.toString(),
                            birthDate.text.toString(), codiceFiscale.text.toString(),
                            role.text.toString(), phoneNumber.text.toString(),
                            certification.text.toString(),
                            results.text.toString(), it.toString()
                        )
                    }
                }}
        }.addOnFailureListener {
            Log.e(TAG, "KO")
        }
    }

    private fun checkImage(): Boolean {
        if(img == null) {

        }

    }

    private fun saveData(name: String, cogn: String, dataN: String, codFisc: String, rol: String,
                         cel: String, cert: String, ris: String, data: String
    ) {

        val currentUser = auth.currentUser
        val uid = currentUser!!.uid
        val atletiMap = HashMap<String, String>()
        atletiMap["immagine"] = data
        atletiMap["nome"] = name
        atletiMap["cognome"] = cogn
        atletiMap["dataNascita"] = dataN
        atletiMap["codiceFiscale"] = codFisc
        atletiMap["telefono"] = cel
        atletiMap["ruolo"] = rol
        atletiMap["certificazioni"] = cert
        atletiMap["risultati"] = ris

        ref.child(uid).child("Atleti").child(codFisc).setValue(atletiMap)
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, FOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == FOTO) {
            img = data?.data!!
            immagine.setImageURI(img)
        }
    }

    private fun dataPicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(view?.context!!,{
                view, y, m, d ->
            val a = "$d/${m+1}/$y"
            dataNascita.text = a

        },year,month,day).show()


    }

    private fun checkPhone(): Boolean {
        if(phoneNumber.text.isEmpty()){
            return false
        } else return phoneNumber.text.length == 10
    }

    private fun checkRole(): Boolean {
        if(role.text.isEmpty()){
            return false
        }else return role.text.equals("attaccante") || role.text.equals("centrocampista")
                || role.text.equals("difensore") || role.text.equals("portiere")
    }

    private fun checkSurname(): Boolean {
        return !surname.text.isEmpty()
    }

    private fun checkName(): Boolean {
        val name: String = nome.text.toString()
        return !name.isEmpty()
    }
    private fun checkResults(): Boolean {
        return !results.text.isEmpty()
    }
    private fun checkCertficazione(): Boolean {
        return !certification.text.isEmpty()
    }

}
