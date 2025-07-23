package com.frank.weartictactoe

import androidx.wear.tiles.TileService
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.ResourceBuilders
import androidx.wear.tiles.TimelineBuilders
import androidx.wear.tiles.DimensionBuilders
import androidx.wear.tiles.ColorBuilders
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class ScoreboardTileService : TileService() {
    override fun onTileRequest(requestParams: TileRequest): ListenableFuture<Tile> {
        val prefs = applicationContext.getSharedPreferences("tictactoe_scores", MODE_PRIVATE)
        val xScore = prefs.getInt("xScore", 0)
        val oScore = prefs.getInt("oScore", 0)

        val title = LayoutElementBuilders.Text.Builder()
            .setText("Scoreboard")
            .setFontStyle(
                LayoutElementBuilders.FontStyle.Builder()
                    .setSize(DimensionBuilders.SpProp.Builder().setValue(18f).build())
                    .build()
            )
            .build()

        val spacer = LayoutElementBuilders.Spacer.Builder()
            .setHeight(DimensionBuilders.dp(8f))
            .build()

        val playerX = LayoutElementBuilders.Text.Builder()
            .setText("Player X: $xScore")
            .setFontStyle(
                LayoutElementBuilders.FontStyle.Builder()
                    .setSize(DimensionBuilders.SpProp.Builder().setValue(16f).build())
                    .setColor(ColorBuilders.ColorProp.Builder().setArgb(0xFF1976D2.toInt()).build()) // Blue
                    .build()
            )
            .build()

        val playerO = LayoutElementBuilders.Text.Builder()
            .setText("Player O: $oScore")
            .setFontStyle(
                LayoutElementBuilders.FontStyle.Builder()
                    .setSize(DimensionBuilders.SpProp.Builder().setValue(16f).build())
                    .setColor(ColorBuilders.ColorProp.Builder().setArgb(0xFFD32F2F.toInt()).build()) // Red
                    .build()
            )
            .build()

        val column = LayoutElementBuilders.Column.Builder()
            .addContent(title)
            .addContent(spacer)
            .addContent(playerX)
            .addContent(spacer)
            .addContent(playerO)
            .build()

        val layout = LayoutElementBuilders.Layout.Builder()
            .setRoot(column)
            .build()

        val tile = TileBuilders.Tile.Builder()
            .setResourcesVersion("1")
            .setTimeline(
                TimelineBuilders.Timeline.Builder()
                    .addTimelineEntry(
                        TimelineBuilders.TimelineEntry.Builder()
                            .setLayout(layout)
                            .build()
                    )
                    .build()
            )
            .build()

        return Futures.immediateFuture(tile)
    }

    override fun onResourcesRequest(requestParams: androidx.wear.tiles.RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> {
        val resources = ResourceBuilders.Resources.Builder()
            .setVersion("1")
            .build()
        return Futures.immediateFuture(resources)
    }
}