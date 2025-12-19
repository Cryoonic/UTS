package com.example.uts.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.uts.R
import com.example.uts.databinding.FragmentStepAgeHeightWeightBinding
import com.example.uts.viewmodel.ProfileViewModel

class StepAgeHeightWeightFragment : Fragment() {

    private lateinit var b: FragmentStepAgeHeightWeightBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentStepAgeHeightWeightBinding.inflate(inflater, container, false)

        setupNumberPickers()

        b.btnNextStep1.setOnClickListener {

            val age = b.npAge.value
            val height = b.npHeight.value
            val weight = b.npWeight.value

            // Simpan ke ViewModel
            ProfileViewModel.instance.apply {
                this.age = age
                this.height = height.toFloat()
                this.weight = weight.toFloat()
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.containerSetup, StepGenderFragment())
                .addToBackStack(null)
                .commit()
        }

        return b.root
    }

    private fun setupNumberPickers() {

        // age
        b.npAge.minValue = 10
        b.npAge.maxValue = 100
        b.npAge.value = 20

        // height cm
        b.npHeight.minValue = 100
        b.npHeight.maxValue = 220
        b.npHeight.value = 170

        // weight kg
        b.npWeight.minValue = 30
        b.npWeight.maxValue = 200
        b.npWeight.value = 60
    }
}
