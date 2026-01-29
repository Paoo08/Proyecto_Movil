package com.example.proyecto.ui.slideshow

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto.Comment
import com.example.proyecto.databinding.FragmentSlideshowBinding
import java.util.Calendar

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    private val binding get() = _binding!!

    //private val comments = mutableListOf<Comment>()

    private val comments = ArrayList<Comment>(30)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root


        binding.sendButton.setOnClickListener {
            val userName = binding.userName.text.toString()
            val visitDay = binding.visitDay.text.toString()
            val roomNumber = binding.roomNumber.text.toString()
            val hotelName = binding.hotelName.text.toString()
            val problemDescription = binding.problemDescription.text.toString()
            val rating = binding.ratingBar.rating
            setupDatePickers()

            if (comments.size < 30) {
                val comment = Comment(userName, visitDay, roomNumber, hotelName, problemDescription, rating)
                comments.add(comment)
                Toast.makeText(activity, "Gracias por tu comentario", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Has alcanzado el límite de comentarios", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }
    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()

        binding.visitDay.setOnClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->

                    binding.visitDay.setText("$dayOfMonth/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePicker.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}