package it.com.uninsubria.footballteam.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import it.com.uninsubria.footballteam.Atleta
import it.com.uninsubria.footballteam.R
import it.com.uninsubria.footballteam.adapter.PlayerAdapter
import it.com.uninsubria.footballteam.adapter.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.fragment_atleti.*
import kotlinx.android.synthetic.main.giocatore.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AtletiFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class AtletiFragment : Fragment(){

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var reg: RecyclerView
    private lateinit var list: ArrayList<Atleta>
    private  lateinit var db: DatabaseReference
    private  lateinit var sd: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_atleti, container, false)
        reg = view.findViewById(R.id.recycler_view)
        reg.layoutManager = LinearLayoutManager(view.context)
        reg.setHasFixedSize(true)
        list = arrayListOf<Atleta>()
        readAtlethData()

        val deleteElement = object: SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val a: Atleta = list.removeAt(position)
                // Rimozione Giocatore
                db = a.codiceFiscale?.let {
                    FirebaseDatabase.getInstance("https://footballteam-d5795-default-rtdb.firebaseio.com/")
                        .getReference("Users")
                        .child(Firebase.auth.currentUser!!.uid)
                        .child("Atleti")
                        .child(it)


                }!!
                // Rimozione immagine
                var path = "/image/${a.nome}"
                Firebase.storage.reference.child(path).delete()
                db.removeValue()

                reg.adapter?.notifyItemRemoved(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(deleteElement)
        itemTouchHelper.attachToRecyclerView(reg)

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener{
            val nuovo = register_player_fragment()
            val fragmentManager = parentFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.mainContainer,
                nuovo
            )
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }
        return view
    }


    private fun readAtlethData() {
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid
        db = FirebaseDatabase.getInstance("https://footballteam-d5795-default-rtdb.firebaseio.com/")
            .getReference("Users")
            .child(uid)
            .child("Atleti")
        db.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                if(snapshot.exists()) {
                    for(data in snapshot.children) {
                        val atleta = data.getValue(Atleta::class.java)
                        list.add(atleta!!)
                       // Log.e("Atleta","${atleta.immagine}")
                        //Log.e("Atleta","${atleta.nome}")
                        //Log.e("Atleta","${atleta.dataNascita}")
                    }
                    reg.adapter = PlayerAdapter(list)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TEST",error.getMessage())
            }
        })

    }
}

