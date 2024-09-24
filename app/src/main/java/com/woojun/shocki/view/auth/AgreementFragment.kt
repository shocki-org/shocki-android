package com.woojun.shocki.view.auth

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.woojun.shocki.BuildConfig
import com.woojun.shocki.R
import com.woojun.shocki.databinding.FragmentAgreementBinding

class AgreementFragment : Fragment() {

    private var _binding: FragmentAgreementBinding? = null
    private val binding get() = _binding!!

    var isCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgreementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.agreedButton.setOnClickListener {
            isCheck = !isCheck

            if (isCheck) {
                val url = "https://shocki.seogaemo.com/privacy"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)

                binding.image1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
                binding.noneButton.visibility = View.GONE

                binding.mainButton.apply {
                    visibility = View.VISIBLE
                    setOnClickListener { (requireActivity() as AuthActivity).animationNavigate(R.id.authHome) }
                }
            } else {
                binding.image1.setColorFilter(Color.parseColor("#B2B2BD"))

                binding.mainButton.visibility = View.GONE
                binding.noneButton.visibility = View.VISIBLE
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}