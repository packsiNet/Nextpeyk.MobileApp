package com.nextpeyk.mobileapp.core.map

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.MapTileIndex

object MapDefaults {
    const val DEFAULT_LAT = 35.6892
    const val DEFAULT_LNG = 51.3890
    const val DEFAULT_ZOOM = 14f
}

internal object SnappTileSource : OnlineTileSourceBase(
    "SnappMaps", 0, 18, 256, ".png",
    arrayOf("https://raster.snappmaps.ir"),
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        val z = MapTileIndex.getZoom(pMapTileIndex)
        val x = MapTileIndex.getX(pMapTileIndex)
        val y = MapTileIndex.getY(pMapTileIndex)
        return "https://raster.snappmaps.ir/styles/snapp-style/$z/$x/$y.png"
    }
}
