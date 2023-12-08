package com.example.timesync.ui.gallery

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.timesync.SharedPref
import com.example.timesync.databinding.FragmentProfileBinding
import com.google.firebase.auth.UserInfo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.common.io.Files.getFileExtension
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val IMAGE_PICK_CODE = 1000
    private val IMAGE_ADD_CODE = 100

    private val PERMISSION_CODE = 1001
    var storage: FirebaseStorage? = null
    private var filePath: Uri? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        val galleryViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        storage = FirebaseStorage.getInstance();
        var user = Firebase.auth.currentUser

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
            Toast.makeText(requireContext(), "Changes saved successfully!", Toast.LENGTH_SHORT)
                .show();
        }
        return root
    }

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
            img = Uri.parse(img.toString()).toString()
            binding.profileIcon.setImageURI(Uri.parse(img))
        }
    }

    private fun handleCameraButtonClick() {
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//            openGallery()
//        } else {
        if (checkGalleryPermission()) {
            openGallery()
        } else {
            requestGalleryPermission()
        }
        // }
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
                img = Uri.parse(img.toString()).toString()
                binding.profileIcon.setImageURI(Uri.parse(img))
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
//        storeInFirebase(requireContext(), imageUri!!, "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toast(string: String): Toast? = Toast.makeText(
        context, "Upload failed: ${string}", Toast.LENGTH_SHORT
    )

    fun storeInFirebase(context: Context, uri: Uri, type: String) {
        var riversRef: StorageReference? = null
        val mStorageRef = FirebaseStorage.getInstance().reference

        riversRef = mStorageRef.child("pictures/" + "${Firebase.auth.currentUser?.uid}.jpg")
        val uploadTask = riversRef.putFile(uri)
        uploadTask.addOnFailureListener { exception ->
            toast("failed")?.show()

            Log.d("downloadUrl", "failed")
        }.addOnSuccessListener { taskSnapshot ->
            toast("done")?.show()

            val downloadUrl = taskSnapshot.storage.downloadUrl
            Log.d("downloadUrl", "$downloadUrl")

        }
    }
}
