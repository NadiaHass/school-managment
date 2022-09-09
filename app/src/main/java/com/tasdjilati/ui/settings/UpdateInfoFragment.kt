package com.tasdjilati.ui.settings

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.tasdjilati.data.entities.SplashImage
import com.tasdjilati.databinding.FragmentUpdateInfoBinding


class UpdateInfoFragment : Fragment() {
    private var _binding: FragmentUpdateInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var splashImageViewModel: SplashImageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUpdateInfoBinding.inflate(inflater, container, false)

        splashImageViewModel = ViewModelProvider(this)[SplashImageViewModel::class.java]

        binding.ivAdd.setOnClickListener {
            openFileChooser()
        }

        splashImageViewModel.getImage.observe(viewLifecycleOwner , {
            val list = it
            if(list.isNotEmpty()){
               binding.ivImage.setImageBitmap(list[0].image)
                Glide.with(requireContext())
                    .load(list[0].image)
                    .into(binding.ivImage)

                binding.etName.setText(list[0].name)
            }
        })

        binding.btnUpdate.setOnClickListener {
            if (binding.etName.text.isNotEmpty()){
                Toast.makeText(requireContext() , "Les informations sont bien mise a jour" , Toast.LENGTH_SHORT).show()
                splashImageViewModel.updateName(binding.etName.text.toString())
            }
        }

        return binding.root
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK ){
            val data : Intent? = result.data
            if (data?.data != null) {
                val uriImage = data.data
                Toast.makeText(requireContext() , uriImage.toString() , Toast.LENGTH_SHORT).show()

                val image = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver , uriImage!!))
                }else{
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver , uriImage)
                }

                splashImageViewModel.addImage(SplashImage(0 , getResizedBitmap(image ,480 ,640 )!! , binding.etName.text.toString()))

                Glide.with(requireContext())
                    .load(uriImage)
                    .into(binding.ivImage)
            }
        }
    }

    private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false)
    }

}