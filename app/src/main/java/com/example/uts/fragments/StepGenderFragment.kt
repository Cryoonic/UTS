package com.example.uts.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.uts.R
import com.example.uts.databinding.FragmentStepGenderBinding
import com.example.uts.viewmodel.ProfileViewModel
import android.widget.Toast
import android.widget.ArrayAdapter

class StepGenderFragment : Fragment() {

    private lateinit var b: FragmentStepGenderBinding
    private var selectedGender: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentStepGenderBinding.inflate(inflater, container, false)

        val cards = listOf(
            b.cardMale to "Male",
            b.cardFemale to "Female",
            b.cardUnknown to "Unknown"
        )

        fun updateSelection(selected: View) {
            cards.forEach { (card, _) ->
                card.setBackgroundResource(
                    if (card == selected) R.drawable.bg_option_selected
                    else R.drawable.bg_option_unselected
                )
            }
        }

        cards.forEach { (card, gender) ->
            card.setOnClickListener {
                selectedGender = gender
                updateSelection(card)
            }
        }

        b.btnNext.setOnClickListener {
            if (selectedGender == null) {
                Toast.makeText(requireContext(), "Pilih gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ProfileViewModel.instance.gender = selectedGender!!

            parentFragmentManager.beginTransaction()
                .replace(R.id.containerSetup, StepActivityFragment())
                .addToBackStack(null)
                .commit()
        }

        return b.root
    }
}
