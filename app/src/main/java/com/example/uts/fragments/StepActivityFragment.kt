package com.example.uts.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.uts.R
import com.example.uts.viewmodel.ProfileViewModel
import com.example.uts.databinding.FragmentStepActivityBinding
import android.widget.Toast


class StepActivityFragment : Fragment() {

    private lateinit var b: FragmentStepActivityBinding
    private var selectedActivity: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentStepActivityBinding.inflate(inflater, container, false)

        val cards = listOf(
            b.cardSedentary to "Sedentary",
            b.cardLight to "Light Activity",
            b.cardModerate to "Moderate Activity",
            b.cardActive to "Very Active"
        )

        fun updateSelection(selected: View) {
            cards.forEach { (card, _) ->
                card.setBackgroundResource(
                    if (card == selected) R.drawable.bg_option_selected
                    else R.drawable.bg_option_unselected
                )
            }
        }

        cards.forEach { (card, activity) ->
            card.setOnClickListener {
                selectedActivity = activity
                updateSelection(card)
            }
        }

        b.btnNext.setOnClickListener {
            if (selectedActivity == null) {
                Toast.makeText(requireContext(), "Pilih activity level", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ProfileViewModel.instance.activity = selectedActivity!!

            parentFragmentManager.beginTransaction()
                .replace(R.id.containerSetup, StepConfirmFragment())
                .addToBackStack(null)
                .commit()
        }

        return b.root
    }
}
