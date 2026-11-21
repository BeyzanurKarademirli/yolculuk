package org.hikmetbahcesi.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class EkranTest {
    @get:Rule val kural = createAndroidComposeRule<MainActivity>()
    private val dizin = "build/ekranlar"

    @Test fun ekranlar() {
        kural.onRoot().captureRoboImage("$dizin/1-bugun.png")
        kural.onNodeWithText("Risale").performClick()
        kural.onRoot().captureRoboImage("$dizin/2-risale.png")
        kural.onNodeWithText("Sözler").performClick()
        kural.onRoot().captureRoboImage("$dizin/3-sozler.png")
        kural.onNodeWithText("Bismillah: Her hayrın başı").performClick()
        kural.onRoot().captureRoboImage("$dizin/4-bolum.png")
        kural.onNodeWithText("Düşünürler").performClick()
        kural.onRoot().captureRoboImage("$dizin/5-dusunurler.png")
        kural.onNodeWithText("İmam Gazâlî").performClick()
        kural.onRoot().captureRoboImage("$dizin/6-gazali.png")
        kural.onNodeWithText("Konular").performClick()
        kural.onRoot().captureRoboImage("$dizin/7-konular.png")
    }

    /** Bütün konuların ve bölümlerin hatasız açıldığını doğrular. */
    @Test fun icerikYuklenir() {
        val ic = Icerik.yukle(kural.activity)
        check(ic.konular.size >= 30)
        ic.konular.forEach { k -> k.fikirler.forEach { f -> checkNotNull(ic.dusunur(f.dusunurId)) { "${k.id}: ${f.dusunurId}" } } }
        check(ic.konular.all { it.fikirler.first().dusunurId == "gazali" })
    }
}
