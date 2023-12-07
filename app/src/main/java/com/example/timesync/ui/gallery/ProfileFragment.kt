package com.example.timesync.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.timesync.SharedPref
import com.example.timesync.databinding.FragmentProfileBinding
import com.google.firebase.auth.UserInfo

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textGallery
        //galleryViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var userInfo = SharedPref.UserInfo("","","","")
        val SharedPref = SharedPref()
        userInfo = SharedPref.getUserInfo(requireContext())
        binding.user1.setText(userInfo.username)
        binding.fname.setText(userInfo.firstName)
        binding.lname.setText(userInfo.lastName)
        binding.email1.setText(userInfo.email)
        binding.create.setOnClickListener{
            val SharedPref = SharedPref()
            SharedPref.saveUserInfo(requireContext(), binding.user1.text.toString(), binding.fname.text.toString(), binding.lname.text.toString(), binding.email1.text.toString());
            Toast.makeText(requireContext(), "Changes saved successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}