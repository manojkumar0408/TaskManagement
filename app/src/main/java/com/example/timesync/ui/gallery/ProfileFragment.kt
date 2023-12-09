package com.example.timesync.ui.gallery

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.timesync.R
import com.example.timesync.SharedPref
import com.example.timesync.TasksMainActivity
import com.example.timesync.databinding.FragmentProfileBinding
import com.example.timesync.ui.TaskActivityViewModel
import com.example.timesync.ui.home.HomeViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.UUID


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val IMAGE_PICK_CODE = 1000
    private val IMAGE_ADD_CODE = 100

    private val PERMISSION_CODE = 1001
    private var filePath: Uri? = null
    private val binding get() = _binding!!
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        val galleryViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        var user = Firebase.auth.currentUser
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

        storage = FirebaseStorage.getInstance();
        storageReference = storage?.getReference()
        Log.d("storafe", storageReference.toString())
        binding.camera.setOnClickListener {
            handleCameraButtonClick()
        }

        binding.create.setOnClickListener {
            val SharedPref = SharedPref()
            SharedPref.saveUserInfo(
                requireContext(),
                user?.uid!!,
                binding.user1.text.toString(),
                binding.fname.text.toString(),
                binding.lname.text.toString(),
                binding.email1.text.toString()
            );
            homeViewModel.setUserDetails("${binding.fname.text} ${binding.lname.text}")
            Toast.makeText(requireContext(), "Changes saved successfully!", Toast.LENGTH_SHORT)
                .show();
        }
        return root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = SharedPref()
        val user = sharedPref.getUserInfo(requireContext())

        binding.user1.setText(user.username)
        binding.fname.setText(user.firstName)
        binding.lname.setText(user.lastName)
        binding.email1.setText(user.email)

        if (checkGalleryPermission()) {
            var img = sharedPref.getImageUri(requireContext())
            if (img != null) {
                img = Uri.parse(img.toString()).toString()
                binding.profileIcon.setImageURI(Uri.parse(img))
            } else binding.profileIcon.setImageDrawable(getResources().getDrawable(R.drawable.person))

        } else {
            binding.profileIcon.setImageDrawable(getResources().getDrawable(R.drawable.person))
        }
    }

    private fun handleCameraButtonClick() {
        if (checkGalleryPermission()) {
            openGallery()
        } else {
            requestGalleryPermission()
        }
    }

    private fun checkGalleryPermission(): Boolean {
        Log.d("permissiom", "check Gall")
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestGalleryPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            val sharedPref = SharedPref()
            var img = sharedPref.getImageUri(requireContext())
            if (img != null) {
                img = Uri.parse(img).toString()
                binding.profileIcon.setImageURI(Uri.parse(img))
            } else {
                binding.profileIcon.setImageDrawable(getResources().getDrawable(R.drawable.person));
            }
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            handleImageSelection(data)
        }
    }

    private fun handleImageSelection(data: Intent?) {
        val imageUri: Uri? = data?.data
        filePath = data?.data
        binding.profileIcon.setImageURI(imageUri)
        val SharedPref = SharedPref()
        SharedPref.saveImageUri(requireContext(), imageUri.toString())
        Toast.makeText(context, "Profile picture updated successfully", Toast.LENGTH_SHORT).show()
        homeViewModel.setImageUri(filePath.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toast(string: String): Toast? = Toast.makeText(
        context, "Upload failed: ${string}", Toast.LENGTH_SHORT
    )

    private fun uploadImage() {
        if (filePath != null) {
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setTitle("Uploading...")
            progressDialog.show()
            val ref = storageReference!!.child("images/" + UUID.randomUUID().toString() + ".jpg")
            Toast.makeText(context, "$ref", Toast.LENGTH_SHORT).show()

            ref.putFile(filePath!!).addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(context, "Failed " + e.message, Toast.LENGTH_SHORT).show()
            }.addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
            }
        }
    }


}
