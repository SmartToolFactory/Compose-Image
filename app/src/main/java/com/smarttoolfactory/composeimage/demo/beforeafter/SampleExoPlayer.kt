package com.smarttoolfactory.composeimage.demo.beforeafter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView

@Composable
fun MyPlayer(modifier: Modifier, uri: String) {
    val context = LocalContext.current
    val player = SimpleExoPlayer.Builder(context).build()
    val playerView = remember {
        PlayerView(context)
    }


    println("🚀 MyPlayer URI $uri, player: $player, playerView: $playerView")

    LaunchedEffect(player, uri) {
        playerView.useController = false
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
        val mediaItem = MediaItem.fromUri(uri)

        player.setMediaItem(mediaItem)
        playerView.player = player
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.prepare()
        player.playWhenReady = true
    }

    AndroidView(
        modifier = modifier,
        factory = {
            playerView
        }
    )
}