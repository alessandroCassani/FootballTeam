package it.com.uninsubria.footballteam.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.com.uninsubria.footballteam.Atleta
import it.com.uninsubria.footballteam.Communicator
import it.com.uninsubria.footballteam.R
import it.com.uninsubria.footballteam.adapter.PlayerAdapter
import it.com.uninsubria.footballteam.adapter.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_atleti.*
import kotlinx.android.synthetic.main.fragment_atleti.view.*
import kotlinx.android.synthetic.main.giocatore.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AtletiFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class AtletiFragment : Fragment(){

    private val open : Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_open_anim) }
    private val close : Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_close_anim) }
    private val fromBottom : Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.from_bottom_anim) }
    private val toBottom : Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.to_bottom_anim) }
    private var clicked = false
    private lateinit var com : Communicator
    private lateinit var reg: RecyclerView
    private lateinit var list: ArrayList<Atleta>
    private lateinit var selezionati: ArrayList<String>
    private var db: DatabaseReference = FirebaseDatabase.getInstance("https://footballteam-d5795-default-rtdb.firebaseio.com/")
        .getReference("Users")
        .child(Firebase.auth.currentUser!!.uid)
        .child("Atleti")




    //callback simile a onCreate per le activity
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_atleti, container, false)
        return view
    }


    // callback chiamata quando sono pronti tutti gli elementi grafici
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Predisposizione recycler view
        setupRecyclerView(view)
        // Funzione per leggere i giocatori
        readPlayers()
        // Funzione per Eliminare i Giocatori
        deletePlayer()
        // Apertura registrazione di un'atleta con floating button
        openFunction(view)
    }

    private fun readPlayers() {
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

                    reg.adapter = PlayerAdapter(list) { position ->
                        val a: Atleta = list[position]
                        selezionati.add(a.telefono!!)
                    }





                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TEST",error.getMessage())
            }
        })

    }

    private fun deletePlayer() {
        val deleteElement = object: SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val a: Atleta = list.removeAt(position)

                // Rimozione Giocatore
                a.codiceFiscale?.let { db.child(it) }
                // Rimozione immagine
                var path = "/image/${a.nome}"
                Firebase.storage.reference.child(path).delete()
                // Rimozione effettiva player
                db.removeValue()
                reg.adapter?.notifyItemRemoved(position)


            }
        }
        // Sistema di gestione dello swipw
        val itemTouchHelper = ItemTouchHelper(deleteElement)
        itemTouchHelper.attachToRecyclerView(reg)

    }

    private fun setupRecyclerView(view: View) {
        reg = view.findViewById(R.id.recycler_view)
        list = arrayListOf<Atleta>()
        selezionati = arrayListOf<String>()
        reg.apply {
            layoutManager = LinearLayoutManager(view.context)
            reg.setHasFixedSize(true)
        }



    }

    private fun openFunction(view: View) {
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        var fabChat = view.findViewById<FloatingActionButton>(R.id.fab_chat)
        var fabAdd = view.findViewById<FloatingActionButton>(R.id.fab_add)
        fab.setOnClickListener{
            onAddButtonClicked()
        }
        fabAdd.setOnClickListener {
            val nuovo = register_player_fragment()
            creazioneFragment(nuovo)
        }
        fabChat.setOnClickListener {
            com = activity as Communicator
                com.passData(selezionati)

        }


    }

    private fun creazioneFragment(fragment: Fragment) =
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.mainContainer, fragment)
            addToBackStack(null)
            commit()
        }
    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
        val fab = view?.findViewById<FloatingActionButton>(R.id.fab)
        var fabChat = view?.findViewById<FloatingActionButton>(R.id.fab_chat)
        var fabAdd = view?.findViewById<FloatingActionButton>(R.id.fab_add)
        if(!clicked) {
            fabChat?.startAnimation(fromBottom)
            fabAdd?.startAnimation(fromBottom)
            fab?.startAnimation(open)
        } else {
            fabChat?.startAnimation(toBottom)
            fabAdd?.startAnimation(toBottom)
            fab?.startAnimation(close)
        }




    }

    private fun setVisibility(clicked: Boolean) {
        var fabChat = view?.findViewById<FloatingActionButton>(R.id.fab_chat)
        var fabAdd = view?.findViewById<FloatingActionButton>(R.id.fab_add)
        if(!clicked) {
            fabChat?.visibility = View.VISIBLE
            fabAdd?.visibility = View.VISIBLE
        }
        else {
            fabChat?.visibility = View.INVISIBLE
            fabAdd?.visibility = View.INVISIBLE
        }




    }


}

