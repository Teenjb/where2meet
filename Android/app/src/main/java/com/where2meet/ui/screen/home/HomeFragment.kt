package com.where2meet.ui.screen.home

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.where2meet.R
import com.where2meet.databinding.FragmentHomeBinding
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.ext.toast
import com.where2meet.ui.ext.viewBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import reactivecircus.flowbinding.android.view.clicks

class HomeFragment : BaseFragment(R.layout.fragment_home) {
    private val binding by viewBinding<FragmentHomeBinding>()
    private val viewModel by viewModels<HomeViewModel>()
    override fun bindView() {
        with(binding) {
            bottomAppBar.apply {
                this.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_logout -> {
                            viewModel.onLogout()
                            toast(
                                getString(R.string.msg_logout),
                            )
                            navigateTo(HomeFragmentDirections.actionToOnboarding())
                            true
                        }

                        else -> true
                    }
                }
            }

            fab.clicks().onEach {
                navigateTo(HomeFragmentDirections.actionToPickMood())
            }.launchIn(lifecycleScope)

            with(home) {
                with(content) {
                    ctaSeeAll.clicks().onEach {
                        navigateTo(HomeFragmentDirections.actionToListGroup())
                    }.launchIn(lifecycleScope)
                }
            }
        }

        // observers
        observeSession()
    }

    private fun observeSession() = lifecycleScope.launch {
        viewModel.session.collectLatest { session ->
            binding.home.apply {
                tvGreeting.text = getString(R.string.lbl_greeting, session.username)
                ivAvatar.apply {
                    val imgData = ImageRequest.Builder(this.context)
                        .data("https://ui-avatars.com/api/?name=${session.username}&length=1")
                        .target(this)
                        .transformations(RoundedCornersTransformation(16f))
                        .allowHardware(true)
                        .build()
                    imageLoader.enqueue(imgData)
                }
            }
        }
    }
}
