package org.hikmetbahcesi.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val TR = Locale.forLanguageTag("tr-TR")
private val tarihBicimi = DateTimeFormatter.ofPattern("d MMMM yyyy, EEEE", TR)
private val kenar = PaddingValues(horizontal = 16.dp, vertical = 12.dp)

// ───────────────────────── Ortak parçalar ─────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UstCubuk(baslik: String, geri: (() -> Unit)? = null, eylemler: @Composable () -> Unit = {}) {
    TopAppBar(
        title = { Text(baslik, style = MaterialTheme.typography.titleLarge, maxLines = 1) },
        navigationIcon = {
            if (geri != null) IconButton(onClick = geri) { Icon(Icons.Filled.ArrowBack, contentDescription = "Geri") }
        },
        actions = { eylemler() },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
    )
}

@Composable
private fun FavoriDugmesi(o: Ortak, anahtar: String) {
    val secili = anahtar in o.favoriler
    IconButton(onClick = { o.favoriDegistir(anahtar) }) {
        Icon(
            if (secili) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = if (secili) "Favorilerden çıkar" else "Favorilere ekle",
            tint = if (secili) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun Rozet(metin: String) {
    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp)) {
        Box(contentAlignment = Alignment.Center) {
            Text(metin, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleMedium)
        }
    }
}

private fun basHarf(ad: String): String =
    ad.split(' ', '-').filter { it.isNotBlank() && it[0].isUpperCase() }.take(2).joinToString("") { it.take(1) }

@Composable
private fun AyetKarti(ayet: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            ayet,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun FikirKarti(o: Ortak, f: Fikir) {
    val d = o.icerik.dusunur(f.dusunurId)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().clickable(enabled = d != null) { o.git("dusunur/${f.dusunurId}") },
            ) {
                Rozet(basHarf(d?.ad ?: "?"))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(d?.ad ?: f.dusunurId, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    if (d != null) Text(d.donem, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(f.metin, style = MaterialTheme.typography.bodyLarge)
            f.alinti?.let {
                Spacer(Modifier.height(12.dp))
                Row(Modifier.height(IntrinsicSize.Min)) {
                    Surface(color = MaterialTheme.colorScheme.secondary, modifier = Modifier.width(3.dp).fillMaxHeight()) {}
                    Spacer(Modifier.width(12.dp))
                    Text(it, style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Kaynak: ${f.kaynak}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
            f.not?.let {
                Spacer(Modifier.height(4.dp))
                Text("Not: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun TefekkurKarti(soru: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Günün tefekkürü", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(Modifier.height(6.dp))
            Text(soru, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

/** Bir konunun tüm içeriği (Bugün ve Konu detayında ortak). */
private fun androidx.compose.foundation.lazy.LazyListScope.konuGovdesi(o: Ortak, k: Konu) {
    item {
        Text(k.giris, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    k.ayet?.let { item { AyetKarti(it) } }
    items(k.fikirler) { FikirKarti(o, it) }
    item { TefekkurKarti(k.tefekkur) }
    item { KaynakUyarisi() }
}

@Composable
private fun KaynakUyarisi() {
    Text(
        "Metinler, eserlerin mealen (anlam olarak) aktarımıdır. Tırnak içindeki ifadeler meşhur sözlerdir; " +
            "kesin lafız için eserlerin aslına ve güvenilir tercümelerine başvurunuz.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 8.dp),
    )
}

@Composable
private fun ListeSatiri(baslik: String, alt: String?, onClick: () -> Unit, sag: @Composable () -> Unit = {}) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(baslik, style = MaterialTheme.typography.titleMedium)
            if (!alt.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(alt, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
            }
        }
        sag()
    }
}

@Composable
private fun BolumBasligi(metin: String) {
    Text(
        metin,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.secondary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 4.dp),
    )
}

// ───────────────────────── Bugün ─────────────────────────

@Composable
fun BugunEkrani(o: Ortak, m: Modifier) {
    var gun by rememberSaveable { mutableLongStateOf(LocalDate.now().toEpochDay()) }
    val tarih = LocalDate.ofEpochDay(gun)
    val k = o.icerik.gununKonusu(tarih)
    val bugunMu = gun == LocalDate.now().toEpochDay()
    Column(m.fillMaxSize()) {
        UstCubuk("Hikmet Bahçesi") {
            FavoriDugmesi(o, "konu/${k.id}")
            IconButton(onClick = { o.git("hakkinda") }) { Icon(Icons.Filled.Info, contentDescription = "Hakkında ve ayarlar") }
        }
        LazyColumn(contentPadding = kenar, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { gun-- }) { Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Önceki gün") }
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (bugunMu) "Günün konusu" else "Seçili gün", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                        Text(tarih.format(tarihBicimi), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { gun++ }) { Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "Sonraki gün") }
                }
                if (!bugunMu) {
                    TextButton(onClick = { gun = LocalDate.now().toEpochDay() }, modifier = Modifier.fillMaxWidth()) { Text("Bugüne dön") }
                }
            }
            item {
                Text(k.baslik, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            }
            konuGovdesi(o, k)
        }
    }
}

// ───────────────────────── Konular ─────────────────────────

@Composable
fun KonularEkrani(o: Ortak, m: Modifier) {
    var arama by rememberSaveable { mutableStateOf("") }
    val q = arama.trim().lowercase(TR)
    val liste = o.icerik.konular.filter {
        q.isEmpty() || it.baslik.lowercase(TR).contains(q) || it.giris.lowercase(TR).contains(q) ||
            it.fikirler.any { f -> f.metin.lowercase(TR).contains(q) }
    }
    Column(m.fillMaxSize()) {
        UstCubuk("Konular")
        OutlinedTextField(
            value = arama, onValueChange = { arama = it },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            placeholder = { Text("Konu ya da kelime ara") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        )
        LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
            items(liste, key = { it.id }) { k ->
                ListeSatiri(k.baslik, k.giris, { o.git("konu/${k.id}") }) { FavoriDugmesi(o, "konu/${k.id}") }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            if (liste.isEmpty()) item {
                Text("Sonuç bulunamadı.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun KonuDetayEkrani(o: Ortak, id: String, m: Modifier) {
    val k = o.icerik.konu(id) ?: return
    Column(m.fillMaxSize()) {
        UstCubuk(k.baslik, o.geri) { FavoriDugmesi(o, "konu/${k.id}") }
        LazyColumn(contentPadding = kenar, verticalArrangement = Arrangement.spacedBy(14.dp)) { konuGovdesi(o, k) }
    }
}

// ───────────────────────── Düşünürler ─────────────────────────

@Composable
fun DusunurlerEkrani(o: Ortak, m: Modifier) {
    val (one, digerleri) = o.icerik.dusunurler.partition { it.oncelikli }
    Column(m.fillMaxSize()) {
        UstCubuk("Düşünürler")
        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
            item { BolumBasligi("ÖNE ÇIKANLAR") }
            items(one, key = { it.id }) { DusunurSatiri(o, it) }
            var sonDevir = ""
            digerleri.forEach { d ->
                if (d.devir != sonDevir) {
                    sonDevir = d.devir
                    val baslik = d.devir.uppercase(TR)
                    item(key = "baslik-${d.id}") { BolumBasligi(baslik) }
                }
                item(key = d.id) { DusunurSatiri(o, d) }
            }
        }
    }
}

@Composable
private fun DusunurSatiri(o: Ortak, d: Dusunur) {
    Row(
        Modifier.fillMaxWidth().clickable { o.git("dusunur/${d.id}") }.padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Rozet(basHarf(d.ad))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(d.ad, style = MaterialTheme.typography.titleMedium)
            Text(d.kisa, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
        }
    }
}

@Composable
fun DusunurDetayEkrani(o: Ortak, id: String, m: Modifier) {
    val d = o.icerik.dusunur(id) ?: return
    val fikirler = o.icerik.dusunurFikirleri(id)
    Column(m.fillMaxSize()) {
        UstCubuk(d.ad, o.geri)
        LazyColumn(contentPadding = kenar, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text(d.donem, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.height(6.dp))
                Text(d.hayat, style = MaterialTheme.typography.bodyLarge)
            }
            item {
                Text("Başlıca eserleri", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(6.dp))
                d.eserler.forEach { e ->
                    Text("•  $e", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 2.dp))
                }
            }
            if (d.id == "nursi") item {
                OutlinedButton(onClick = { o.git("kitap/sozler") }, modifier = Modifier.fillMaxWidth()) { Text("Risale-i Nur özetlerine git") }
            }
            if (fikirler.isNotEmpty()) {
                item { Text("Konulara göre fikirleri", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary) }
                items(fikirler) { (k, f) ->
                    Card(
                        onClick = { o.git("konu/${k.id}") },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(k.baslik, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.height(4.dp))
                            Text(f.metin, style = MaterialTheme.typography.bodyMedium, maxLines = 4)
                            Spacer(Modifier.height(4.dp))
                            Text(f.kaynak, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

// ───────────────────────── Risale-i Nur ─────────────────────────

@Composable
fun RisaleEkrani(o: Ortak, m: Modifier) {
    Column(m.fillMaxSize()) {
        UstCubuk("Risale-i Nur")
        LazyColumn(contentPadding = kenar, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text(
                    "Bediüzzaman Said Nursî'nin Risale-i Nur Külliyatı'ndan seçilmiş bölümlerin özetleri: her bölümün konusu, " +
                        "ne anlatmak istediği ve ana fikri.",
                    style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            items(o.icerik.risale, key = { it.id }) { k ->
                Card(
                    onClick = { o.git("kitap/${k.id}") },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(k.ad, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                            Text("${k.bolumler.size} bölüm", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(k.aciklama, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            item {
                Text(
                    "Özetler, metnin aslı değil; anlamın sade bir aktarımıdır. Asıl metni okumanız tavsiye edilir.",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun KitapEkrani(o: Ortak, id: String, m: Modifier) {
    val k = o.icerik.kitap(id) ?: return
    Column(m.fillMaxSize()) {
        UstCubuk(k.ad, o.geri)
        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
            item {
                Text(k.aciklama, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(16.dp))
            }
            items(k.bolumler, key = { it.id }) { b ->
                ListeSatiri(b.baslik, b.konu, { o.git("bolum/${b.id}") }) { FavoriDugmesi(o, "bolum/${b.id}") }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

@Composable
fun BolumEkrani(o: Ortak, id: String, m: Modifier) {
    val (k, b) = o.icerik.bolum(id) ?: return
    val i = k.bolumler.indexOf(b)
    Column(m.fillMaxSize()) {
        UstCubuk(b.baslik, o.geri) { FavoriDugmesi(o, "bolum/${b.id}") }
        LazyColumn(contentPadding = kenar, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Text(k.ad.uppercase(TR), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                Text(b.konu, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            }
            item {
                Text("Ne anlatıyor?", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(b.ozet, style = MaterialTheme.typography.bodyLarge)
            }
            item {
                Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Ana fikir", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(Modifier.height(4.dp))
                        Text(b.anaFikir, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
            if (b.dersler.isNotEmpty()) item {
                Text("Çıkarılacak dersler", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                b.dersler.forEach { Text("•  $it", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 3.dp)) }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    if (i > 0) TextButton(onClick = { o.geri(); o.git("bolum/${k.bolumler[i - 1].id}") }) { Text("‹ Önceki") } else Spacer(Modifier)
                    if (i < k.bolumler.lastIndex) TextButton(onClick = { o.geri(); o.git("bolum/${k.bolumler[i + 1].id}") }) { Text("Sonraki ›") }
                }
            }
        }
    }
}

// ───────────────────────── Favoriler ─────────────────────────

@Composable
fun FavorilerEkrani(o: Ortak, m: Modifier) {
    val konular = o.icerik.konular.filter { "konu/${it.id}" in o.favoriler }
    val bolumler = o.icerik.risale.flatMap { k -> k.bolumler.filter { "bolum/${it.id}" in o.favoriler }.map { k to it } }
    Column(m.fillMaxSize()) {
        UstCubuk("Favoriler")
        if (konular.isEmpty() && bolumler.isEmpty()) {
            Text(
                "Henüz favori yok. Beğendiğiniz konu ve bölümlerde kalp simgesine dokunarak buraya ekleyebilirsiniz.",
                style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp),
            )
            return@Column
        }
        LazyColumn {
            if (konular.isNotEmpty()) {
                item { BolumBasligi("KONULAR") }
                items(konular, key = { "k" + it.id }) { k ->
                    ListeSatiri(k.baslik, k.giris, { o.git("konu/${k.id}") }) { FavoriDugmesi(o, "konu/${k.id}") }
                }
            }
            if (bolumler.isNotEmpty()) {
                item { BolumBasligi("RİSALE-İ NUR") }
                items(bolumler, key = { "b" + it.second.id }) { (k, b) ->
                    ListeSatiri("${k.ad} · ${b.baslik}", b.konu, { o.git("bolum/${b.id}") }) { FavoriDugmesi(o, "bolum/${b.id}") }
                }
            }
        }
    }
}

// ───────────────────────── Hakkında ─────────────────────────

@Composable
fun HakkindaEkrani(o: Ortak, m: Modifier) {
    Column(m.fillMaxSize()) {
        UstCubuk("Hakkında", o.geri)
        LazyColumn(contentPadding = kenar, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Text("Yazı boyutu", style = MaterialTheme.typography.titleMedium)
                Slider(value = o.olcek, onValueChange = o.olcekDegis, valueRange = 0.85f..1.4f, steps = 10)
                Text("Örnek metin: Gönül Çalab'ın tahtı.", style = MaterialTheme.typography.bodyLarge)
            }
            item { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) }
            item {
                Text("Bu uygulama nedir?", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Her gün bir konu seçilir; İmam Gazâlî başta olmak üzere İbnü'l-Arabî, Mevlânâ, Yunus Emre, " +
                        "Said Nursî ve diğer gönül insanlarının o konudaki düşünceleri, eserlerine atıfla sunulur. " +
                        "Risale-i Nur bölümünde ise Külliyat'tan seçilmiş bölümlerin özetleri ve ana fikirleri yer alır.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            item {
                Text("Etik ilkelerimiz", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                listOf(
                    "Tamamen ücretsizdir; ücretli içerik, abonelik ya da bağış baskısı yoktur.",
                    "Reklam yoktur.",
                    "İnternet izni dahi istemez; hiçbir veri toplanmaz, hiçbir yere gönderilmez.",
                    "Favorileriniz ve ayarlarınız yalnızca kendi telefonunuzda saklanır.",
                    "Bildirim, sayaç ya da 'seri' gibi bağımlılık oluşturan tasarımlar kullanılmaz.",
                    "Sözler kaynaklarıyla verilir; nisbeti tartışmalı sözler açıkça belirtilir.",
                    "Risale-i Nur metni kopyalanmaz; yalnızca özgün özetler sunulur.",
                ).forEach { Text("•  $it", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 2.dp)) }
            }
            item {
                Text("Kaynaklar hakkında", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Fikirler, eserlerin mealen (anlam olarak) aktarımıdır. Kesin ifade ve bağlam için eserlerin aslına " +
                        "ve güvenilir tercümelerine başvurulmalıdır. Bir hata fark ederseniz düzeltilmesi için bildirmeniz rica olunur.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            item {
                Text(
                    "Hikmet Bahçesi · sürüm 1.0 · ${o.icerik.konular.size} konu · ${o.icerik.dusunurler.size} düşünür · " +
                        "${o.icerik.risale.sumOf { it.bolumler.size }} Risale özeti",
                    style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
