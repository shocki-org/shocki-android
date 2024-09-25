package com.woojun.shocki.view.nav.explore

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.woojun.shocki.R
import com.woojun.shocki.databinding.MiddleBannerItemBinding
import com.woojun.shocki.dto.SimpleProductResponse
import com.woojun.shocki.util.Util.formatAmount
import com.woojun.shocki.view.main.MainActivity

class MiddleViewPagerAdapter(private val pages: List<Pair<MiddleBannerItemBinding, SimpleProductResponse>>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val pageBinding = pages[position].first
        val bannerItem = pages[position].second

        pageBinding.nameText.text = bannerItem.name
        pageBinding.priceText.text = formatAmount(bannerItem.currentAmount)
        Glide
            .with(pageBinding.root.context)
            .load(bannerItem.image)
            .override(300, 300)
            .centerCrop()
            .into(pageBinding.imageView)

        pageBinding.root.setOnClickListener {
            (pageBinding.root.context as MainActivity).animationNavigate(R.id.storeDetail, bannerItem.id)
        }

        when (position) {
            pages.size - 1 -> {
                pageBinding.endMargin.visibility = View.VISIBLE
            }
            0 -> {
                pageBinding.startMargin.visibility = View.VISIBLE
            }
            else -> {
                pageBinding.startMargin.visibility = View.GONE
                pageBinding.endMargin.visibility = View.GONE
            }
        }

        container.addView(pageBinding.root)
        return pageBinding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return pages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getPageWidth(position: Int): Float {
        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidthPx = displayMetrics.widthPixels

        val firstPageWidthPx = (152 * displayMetrics.density).toInt()
        val middlePageWidthPx = (132 * displayMetrics.density).toInt()
        val lastPageWidthPx = (152 * displayMetrics.density).toInt()

        return when (position) {
            0 -> firstPageWidthPx.toFloat() / screenWidthPx
            pages.size - 1 -> lastPageWidthPx.toFloat() / screenWidthPx
            else -> middlePageWidthPx.toFloat() / screenWidthPx
        }
    }
}
