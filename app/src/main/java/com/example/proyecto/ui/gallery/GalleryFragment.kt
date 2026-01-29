package com.example.proyecto.ui.gallery

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyecto.R
import com.example.proyecto.Reservation
import android.app.AlertDialog
import android.widget.EditText
import com.example.proyecto.Hotel
import com.example.proyecto.SessionManager
import java.util.*
import com.example.proyecto.databinding.FragmentGalleryBinding


class GalleryFragment : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private val hotels = mutableListOf(
        Hotel("Hotel A", "Ubicación: Zona Hotelera, Cancún.\n" +
                "Descripción: El Hotel Riu Cancún es un resort " +
                "todo incluido situado en la hermosa playa de Cancún. " +
                "Este hotel ofrece una amplia variedad de comodidades, " +
                "incluyendo varias piscinas, restaurantes temáticos, bares, " +
                "y actividades diarias. Es conocido por su excelente servicio " +
                "y sus instalaciones modernas. Ideal para familias, parejas y " +
                "grupos de amigos, el hotel también cuenta con entretenimiento " +
                "nocturno y un spa para mayor relajación.", R.drawable.hotel_a),

        Hotel("Hotel B", "Ubicación: Playa del Carmen, cerca de Cancún.\n" +
                "Descripción: El Hotel Xcaret México es un resort todo incluido " +
                "que combina lujo y naturaleza. Este hotel es famoso por su " +
                "concepto \"All-Fun Inclusive\", que incluye acceso ilimitado " +
                "a los parques temáticos de Grupo Xcaret. Rodeado de ríos, cenotes " +
                "y la selva maya, el resort ofrece experiencias únicas de aventura " +
                "y eco-turismo. Las habitaciones están diseñadas con un enfoque " +
                "en la sostenibilidad y el confort, y el hotel cuenta con " +
                "múltiples restaurantes, piscinas y un spa de clase mundial.", R.drawable.hotel_b),
        Hotel("Hotel C", "Ubicación: Zona Hotelera, Cancún.\n" +
                "Descripción: El Hard Rock Hotel Cancún es un resort todo " +
                "incluido con un tema de rock and roll. Este hotel ofrece " +
                "una experiencia vibrante con una decoración que rinde " +
                "homenaje a leyendas de la música. Los huéspedes pueden " +
                "disfrutar de numerosas piscinas, un spa, varios restaurantes " +
                "que ofrecen cocina internacional, y entretenimiento en " +
                "vivo. Las habitaciones están lujosamente equipadas y " +
                "muchas ofrecen vistas espectaculares del océano. El " +
                "ambiente es ideal tanto para familias como para adultos " +
                "que buscan diversión y relajación.", R.drawable.hotel_c)
    )
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sessionManager = SessionManager(requireContext())

        setupUI()
        setupListeners()

        return root
    }

    private fun setupUI() {
        // Configurar el spinner de hoteles con los nombres
        val hotelNames = hotels.map { it.name }
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, hotelNames)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.hotelSpinner.adapter = adapter
            }

        // Configurar otros spinners
        setupSpinners()

        // Configurar botones visibles solo para administradores
        if (sessionManager.isAdmin()) {
            binding.reserveButton.visibility = View.INVISIBLE
            binding.viewReservationButton.visibility = View.INVISIBLE
            binding.deleteReservationButton.visibility = View.INVISIBLE
            binding.modifyReservationButton.visibility= View.INVISIBLE
            binding.addHotelButton.visibility = View.VISIBLE
            binding.editHotelButton.visibility = View.VISIBLE
            binding.deleteHotelButton.visibility = View.VISIBLE
        }
    }

    private fun setupSpinners() {
        ArrayAdapter.createFromResource(
            requireContext(), R.array.rooms_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.roomSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(), R.array.persons_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.personSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(), R.array.beds_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.bedSpinner.adapter = adapter
        }
    }

    private fun setupListeners() {
        binding.hotelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedHotel = hotels[position]
                updateHotelInfoAndImage(selectedHotel)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.reserveButton.setOnClickListener { reserveRoom() }
        binding.viewReservationButton.setOnClickListener { viewReservation() }
        binding.modifyReservationButton.setOnClickListener { modifyReservation() }
        binding.deleteReservationButton.setOnClickListener { deleteReservation() }
        binding.addHotelButton.setOnClickListener { addHotel() }
        binding.editHotelButton.setOnClickListener { editHotel() }
        binding.deleteHotelButton.setOnClickListener { deleteHotel() }

        setupDatePickers()
    }

    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()

        binding.checkInDate.setOnClickListener {
            val datePicker = DatePickerDialog(requireContext(), { _, year, month, day ->
                binding.checkInDate.setText("$day/${month + 1}/$year")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        binding.checkOutDate.setOnClickListener {
            val datePicker = DatePickerDialog(requireContext(), { _, year, month, day ->
                binding.checkOutDate.setText("$day/${month + 1}/$year")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }
    }

    private fun updateHotelInfoAndImage(hotel: Hotel) {
        binding.hotelInfo.text = hotel.description
        binding.hotelImage.setImageResource(hotel.imageResource)
    }

    private fun reserveRoom() {
        val hotel = hotels[binding.hotelSpinner.selectedItemPosition]
        val checkIn = binding.checkInDate.text.toString()
        val checkOut = binding.checkOutDate.text.toString()
        val room = binding.roomSpinner.selectedItem.toString()
        val persons = binding.personSpinner.selectedItem.toString()
        val bed = binding.bedSpinner.selectedItem.toString()

        // Validación de reserva
        if (checkIn.isEmpty() || checkOut.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor seleccione las fechas", Toast.LENGTH_SHORT).show()
            return
        }

        val reservation = Reservation(hotel.name, checkIn, checkOut, room, persons, bed)
        sessionManager.saveReservation(reservation)

        Toast.makeText(requireContext(), "Reservación exitosa", Toast.LENGTH_SHORT).show()
    }

    private fun viewReservation() {
        val reservation = sessionManager.getReservation()
        if (reservation != null) {
            AlertDialog.Builder(requireContext())
                .setTitle("Detalles de la Reservación")
                .setMessage("Hotel: ${reservation.hotel}\nFecha de ida: ${reservation.checkIn}\nFecha de regreso: ${reservation.checkOut}\nHabitación: ${reservation.room}\nPersonas: ${reservation.person}\nCamas: ${reservation.bed}")
                .setPositiveButton("OK", null)
                .show()
        } else {
            Toast.makeText(requireContext(), "No hay reservaciones guardadas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun modifyReservation() {
        val reservation = sessionManager.getReservation()
        if (reservation != null) {
            binding.hotelSpinner.setSelection(hotels.indexOfFirst { it.name == reservation.hotel })
            binding.checkInDate.setText(reservation.checkIn)
            binding.checkOutDate.setText(reservation.checkOut)
            binding.roomSpinner.setSelection((binding.roomSpinner.adapter as ArrayAdapter<String>).getPosition(reservation.room))
            binding.personSpinner.setSelection((binding.personSpinner.adapter as ArrayAdapter<String>).getPosition(reservation.person))
            binding.bedSpinner.setSelection((binding.bedSpinner.adapter as ArrayAdapter<String>).getPosition(reservation.bed))
            Toast.makeText(requireContext(), "Modificar los detalles y presione reservar para actualizar", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No hay reservaciones guardadas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteReservation() {
        sessionManager.clearReservation()
        Toast.makeText(requireContext(), "Reservación eliminada", Toast.LENGTH_SHORT).show()
    }

    private fun addHotel() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_hotel, null)
        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Agregar Hotel")
            .setPositiveButton("Agregar") { dialog, _ ->
                val hotelName = dialogView.findViewById<EditText>(R.id.hotelNameEditText).text.toString()
                val hotelDescription = dialogView.findViewById<EditText>(R.id.hotelDescriptionEditText).text.toString()

                if (hotelName.isNotEmpty() && hotelDescription.isNotEmpty()) {
                    val newHotel = Hotel(hotelName, hotelDescription, R.drawable.hotel_d)
                    hotels.add(newHotel)
                    updateHotelSpinner()
                    Toast.makeText(requireContext(), "Hotel añadido", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                }

                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

        builder.show()
    }

    private fun editHotel() {
        val selectedHotel = hotels[binding.hotelSpinner.selectedItemPosition]
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_hotel, null)
        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Editar Hotel")
            .setPositiveButton("Editar") { dialog, _ ->
                val hotelName = dialogView.findViewById<EditText>(R.id.hotelNameEditText).text.toString()
                val hotelDescription = dialogView.findViewById<EditText>(R.id.hotelDescriptionEditText).text.toString()

                if (hotelName == selectedHotel.name ) {
                    selectedHotel.name = hotelName
                    selectedHotel.description = hotelDescription
                    updateHotelSpinner()
                    Toast.makeText(requireContext(), "Hotel editado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                }

                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

        builder.show()
    }

    private fun deleteHotel() {
        val selectedHotel = hotels[binding.hotelSpinner.selectedItemPosition]
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro que deseas eliminar el hotel: ${selectedHotel.name}?")
            .setPositiveButton("Eliminar") { dialog, _ ->
                hotels.remove(selectedHotel)
                updateHotelSpinner()
                Toast.makeText(requireContext(), "Hotel eliminado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateHotelSpinner() {
        val hotelNames = hotels.map { it.name }
        (binding.hotelSpinner.adapter as ArrayAdapter<String>).apply {
            clear()
            addAll(hotelNames)
            notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}