import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlist_maker.presentation.ui.media.FavoriteTracksFragment
import com.example.playlist_maker.presentation.ui.media.PlaylistFragment

class MediaPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteTracksFragment()
            1 -> PlaylistFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}