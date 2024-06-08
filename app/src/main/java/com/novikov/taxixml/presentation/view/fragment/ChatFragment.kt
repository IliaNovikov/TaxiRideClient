package com.novikov.taxixml.presentation.view.fragment

import android.os.Bundle
import android.util.Log
import com.novikov.taxixml.domain.model.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.database
import com.novikov.taxixml.R
import com.novikov.taxixml.adapters.MessagesAdapter
import com.novikov.taxixml.databinding.FragmentChatBinding
import com.novikov.taxixml.singleton.NavigationController
import com.novikov.taxixml.singleton.UserInfo
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private val messages = ArrayList<Message>()

    private lateinit var adapter: MessagesAdapter

    private var refKey = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(layoutInflater)

        adapter = MessagesAdapter(requireContext(), messages)

        binding.rvMessages.adapter = adapter

        lifecycleScope.launch {
            Firebase.database.reference.child("chats").orderByChild("clientUid").equalTo(UserInfo.uid).limitToLast(1).get().addOnCompleteListener {
                refKey = it.result.children.elementAt(0).key.toString()
            }.await()
            Firebase.database.reference.child("chats").child(refKey).orderByKey().get().addOnCompleteListener{
                for (i in it.result.children){
                    val message = Message(i.child("senderUid").value.toString(), i.child("text").value.toString(), i.child("time").value.toString())
                    messages.add(message)
                }
//                binding.rvMessages.adapter = MessagesAdapter(requireContext(), messages)
            }.await()
        }.invokeOnCompletion {
            messages.removeIf { m -> m.text.isNullOrEmpty() || m.text == "null" }
            adapter.notifyDataSetChanged()
            Firebase.database.reference.child("chats").child(refKey).orderByKey().limitToLast(3).addChildEventListener(object: ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                    if (snapshot.key != "clientUid" && snapshot.key != "driverUid"){
                        val message = Message(
                            snapshot.child("senderUid").value.toString(),
                            snapshot.child("text").value.toString(),
                            snapshot.child("time").value.toString())

                        messages.add(message)
                    }
                    messages.removeIf { m -> m.text.isNullOrEmpty() || m.text == "null" }
                    binding.rvMessages.adapter = adapter
                    binding.tvNoMessages.isVisible = messages.size == 0
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.i("childChanged", snapshot.value.toString())
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    Log.i("childRemoved", snapshot.value.toString())
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.i("childMoved", snapshot.value.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("childCancelled", "cancelled")
                }

            })
        }

        binding.btnBack.setOnClickListener {
            NavigationController.navHost.navigate(R.id.mapFragment)
        }

        binding.tilMessage.setEndIconOnClickListener {
            try{
                lifecycleScope.launch {
                    val message = Message(
                        senderUid = UserInfo.uid,
                        text = binding.etMessage.text.toString(),
                        time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                    )
                    val map = mapOf("senderUid" to message.senderUid, "text" to message.text, "time" to message.time)

                    Firebase.database.reference.child("chats").child(refKey).push().setValue(map).await()
                }
            }
            catch (ex: Exception){
                Log.e("chat", ex.message.toString())
            }
        }

        return binding.root
    }
}