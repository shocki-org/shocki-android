package com.woojun.shocki.view.nav.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.woojun.shocki.data.Notification
import com.woojun.shocki.data.NotificationColor
import com.woojun.shocki.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.notificationList.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = NotificationAdapter(testNotification())
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun testNotification(): List<Notification> {
        return listOf(
            Notification(
                NotificationColor.Green,
                "마켓 출시",
                "‘아이템아이템아이템아이템아이아이템아이템아이템’상품이 마켓에 출시되었어요!",
                "2024년 07월 15일"
            ),
            Notification(
                NotificationColor.Green,
                "관심 품목 알림",
                "‘아이템아이템아이템아이템아이아이템아이템아이템’펀딩 마감까지 NN일 남았어요!",
                "2024년 07월 15일"
            ),
            Notification(
                NotificationColor.Blue,
                "펀딩 정산",
                "‘아이템아이템아이템아이템아이아이템아이템아이템’정산금이 N일 후 들어올 예정이에요!",
                "2024년 07월 15일"
            ),
            Notification(
                NotificationColor.Red,
                "펀딩 실패",
                "‘아이템아이템아이템아이템아이아이템아이템아이템'펀딩에 실패하여 NNNNNN크레딧을 환급했어요",
                "2024년 07월 15일"
            ),
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}