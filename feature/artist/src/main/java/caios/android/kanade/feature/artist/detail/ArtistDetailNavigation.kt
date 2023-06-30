package caios.android.kanade.feature.artist.detail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import caios.android.kanade.core.design.animation.NavigateAnimation
import caios.android.kanade.core.model.music.Album
import caios.android.kanade.core.model.music.Artist
import caios.android.kanade.core.model.music.Song

const val ArtistDetailId = "artistDetailId"
const val ArtistDetailRoute = "artistDetail/{$ArtistDetailId}"

fun NavController.navigateToArtistDetail(artistId: Long) {
    this.navigate("artistDetail/$artistId")
}

fun NavGraphBuilder.artistDetailScreen(
    navigateToSongDetail: (String, List<Long>) -> Unit,
    navigateToArtistMenu: (Artist) -> Unit,
    navigateToSongMenu: (Song) -> Unit,
    navigateToAlbumMenu: (Album) -> Unit,
    terminate: () -> Unit,
) {
    composable(
        route = ArtistDetailRoute,
        arguments = listOf(
            navArgument(ArtistDetailId) { type = NavType.LongType },
        ),
        enterTransition = { NavigateAnimation.Detail.enter },
        exitTransition = { NavigateAnimation.Detail.exit },
        popEnterTransition = { NavigateAnimation.Detail.popEnter },
        popExitTransition = { NavigateAnimation.Detail.popExit },
    ) {
        ArtistDetailRoute(
            artistId = it.arguments?.getLong(ArtistDetailId) ?: -1L,
            navigateToSongDetail = navigateToSongDetail,
            navigateToArtistMenu = navigateToArtistMenu,
            navigateToSongMenu = navigateToSongMenu,
            navigateToAlbumMenu = navigateToAlbumMenu,
            terminate = terminate,
        )
    }
}
