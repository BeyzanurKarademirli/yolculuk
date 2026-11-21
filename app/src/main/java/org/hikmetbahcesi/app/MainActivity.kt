package org.hikmetbahcesi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val icerik = Icerik.yukle(this)
        val tercihler = Tercihler(this)
        setContent {
            var olcek by remember { mutableFloatStateOf(tercihler.yaziOlcegi) }
            HikmetTema(olcek) {
                Uygulama(icerik, tercihler, olcek) { olcek = it; tercihler.yaziOlcegi = it }
            }
        }
    }
}

enum class Sekme(val etiket: String) { BUGUN("Bugün"), KONULAR("Konular"), DUSUNURLER("Düşünürler"), RISALE("Risale"), FAVORI("Favoriler") }

/** Basit, kütüphanesiz gezinme: her sekmenin kendi rota yığını vardır. Rota örnekleri: "konu/sabir", "dusunur/gazali". */
@Composable
fun Uygulama(icerik: Icerik, tercihler: Tercihler, olcek: Float, olcekDegis: (Float) -> Unit) {
    var sekme by rememberSaveable { mutableStateOf(Sekme.BUGUN) }
    var yiginlar by rememberSaveable { mutableStateOf(Sekme.entries.associate { it.name to "" }) }
    var favoriler by remember { mutableStateOf(tercihler.favoriler()) }

    val yigin = yiginlar[sekme.name]!!.split('|').filter { it.isNotEmpty() }
    fun yiginAyarla(y: List<String>) { yiginlar = yiginlar + (sekme.name to y.joinToString("|")) }
    val git: (String) -> Unit = { yiginAyarla(yigin + it) }
    val geri: () -> Unit = { yiginAyarla(yigin.dropLast(1)) }
    val favori: (String) -> Unit = { favoriler = tercihler.favoriDegistir(it) }

    BackHandler(enabled = yigin.isNotEmpty() || sekme != Sekme.BUGUN) {
        if (yigin.isNotEmpty()) geri() else sekme = Sekme.BUGUN
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                Sekme.entries.forEach { s ->
                    NavigationBarItem(
                        selected = s == sekme,
                        onClick = {
                            if (s == sekme) yiginlar = yiginlar + (s.name to "") else sekme = s
                        },
                        icon = { SekmeIkon(s) },
                        label = { Text(s.etiket, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    )
                }
            }
        },
    ) { ic ->
        val m = Modifier.padding(ic)
        val ust = yigin.lastOrNull()
        val ortak = Ortak(icerik, favoriler, favori, git, geri, olcek, olcekDegis)
        when {
            ust == null -> when (sekme) {
                Sekme.BUGUN -> BugunEkrani(ortak, m)
                Sekme.KONULAR -> KonularEkrani(ortak, m)
                Sekme.DUSUNURLER -> DusunurlerEkrani(ortak, m)
                Sekme.RISALE -> RisaleEkrani(ortak, m)
                Sekme.FAVORI -> FavorilerEkrani(ortak, m)
            }
            ust.startsWith("konu/") -> KonuDetayEkrani(ortak, ust.removePrefix("konu/"), m)
            ust.startsWith("dusunur/") -> DusunurDetayEkrani(ortak, ust.removePrefix("dusunur/"), m)
            ust.startsWith("kitap/") -> KitapEkrani(ortak, ust.removePrefix("kitap/"), m)
            ust.startsWith("bolum/") -> BolumEkrani(ortak, ust.removePrefix("bolum/"), m)
            ust == "hakkinda" -> HakkindaEkrani(ortak, m)
        }
    }
}

@Composable
private fun SekmeIkon(s: Sekme) {
    val v: ImageVector? = when (s) {
        Sekme.BUGUN -> Icons.Filled.Home
        Sekme.KONULAR -> Icons.Filled.List
        Sekme.DUSUNURLER -> Icons.Filled.Person
        Sekme.FAVORI -> Icons.Filled.Favorite
        Sekme.RISALE -> null
    }
    if (v != null) Icon(v, contentDescription = null)
    else Icon(painterResource(R.drawable.ic_book), contentDescription = null)
}

class Ortak(
    val icerik: Icerik,
    val favoriler: Set<String>,
    val favoriDegistir: (String) -> Unit,
    val git: (String) -> Unit,
    val geri: () -> Unit,
    val olcek: Float,
    val olcekDegis: (Float) -> Unit,
)
