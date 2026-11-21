package org.hikmetbahcesi.app

import android.content.Context
import org.json.JSONArray
import java.time.LocalDate

data class Dusunur(
    val id: String,
    val ad: String,
    val donem: String,
    val devir: String,
    val kisa: String,
    val hayat: String,
    val eserler: List<String>,
    val oncelikli: Boolean,
)

data class Fikir(
    val dusunurId: String,
    val metin: String,
    val kaynak: String,
    val alinti: String?, // tam metin alıntı (varsa) — yalnızca güvenilir, meşhur ifadeler
    val not: String?,    // nisbet/doğruluk notu
)

data class Konu(
    val id: String,
    val baslik: String,
    val giris: String,
    val ayet: String?,
    val fikirler: List<Fikir>,
    val tefekkur: String,
)

data class RisaleBolum(
    val id: String,
    val baslik: String,
    val konu: String,
    val ozet: String,
    val anaFikir: String,
    val dersler: List<String>,
)

data class RisaleKitap(
    val id: String,
    val ad: String,
    val aciklama: String,
    val bolumler: List<RisaleBolum>,
)

class Icerik(
    val dusunurler: List<Dusunur>,
    val konular: List<Konu>,
    val risale: List<RisaleKitap>,
) {
    private val dusunurMap = dusunurler.associateBy { it.id }
    fun dusunur(id: String) = dusunurMap[id]
    fun konu(id: String) = konular.firstOrNull { it.id == id }
    fun kitap(id: String) = risale.firstOrNull { it.id == id }
    fun bolum(id: String): Pair<RisaleKitap, RisaleBolum>? {
        for (k in risale) k.bolumler.firstOrNull { it.id == id }?.let { return k to it }
        return null
    }

    /** Günün konusu: tarihe göre sabit sırayla döner, herkes aynı gün aynı konuyu görür. */
    fun gununKonusu(tarih: LocalDate): Konu {
        val i = Math.floorMod(tarih.toEpochDay(), konular.size.toLong()).toInt()
        return konular[i]
    }

    /** Bir düşünürün tüm konulardaki fikirleri. */
    fun dusunurFikirleri(id: String): List<Pair<Konu, Fikir>> =
        konular.flatMap { k -> k.fikirler.filter { it.dusunurId == id }.map { k to it } }

    companion object {
        fun yukle(ctx: Context): Icerik {
            fun oku(ad: String) = ctx.assets.open(ad).bufferedReader(Charsets.UTF_8).use { it.readText() }
            fun JSONArray.strings() = List(length()) { getString(it) }

            val d = JSONArray(oku("dusunurler.json")).let { a ->
                List(a.length()) { i ->
                    val o = a.getJSONObject(i)
                    Dusunur(
                        o.getString("id"), o.getString("ad"), o.getString("donem"),
                        o.getString("devir"), o.getString("kisa"), o.getString("hayat"),
                        o.getJSONArray("eserler").strings(), o.optBoolean("oncelikli", false),
                    )
                }
            }
            val k = JSONArray(oku("konular.json")).let { a ->
                List(a.length()) { i ->
                    val o = a.getJSONObject(i)
                    val fa = o.getJSONArray("fikirler")
                    Konu(
                        o.getString("id"), o.getString("baslik"), o.getString("giris"),
                        o.optString("ayet").ifBlank { null },
                        List(fa.length()) { j ->
                            val f = fa.getJSONObject(j)
                            Fikir(
                                f.getString("dusunur"), f.getString("metin"), f.getString("kaynak"),
                                f.optString("alinti").ifBlank { null },
                                f.optString("not").ifBlank { null },
                            )
                        },
                        o.getString("tefekkur"),
                    )
                }
            }
            val r = JSONArray(oku("risale.json")).let { a ->
                List(a.length()) { i ->
                    val o = a.getJSONObject(i)
                    val ba = o.getJSONArray("bolumler")
                    RisaleKitap(
                        o.getString("id"), o.getString("ad"), o.getString("aciklama"),
                        List(ba.length()) { j ->
                            val b = ba.getJSONObject(j)
                            RisaleBolum(
                                b.getString("id"), b.getString("baslik"), b.getString("konu"),
                                b.getString("ozet"), b.getString("anaFikir"),
                                b.optJSONArray("dersler")?.strings() ?: emptyList(),
                            )
                        },
                    )
                }
            }
            return Icerik(d, k, r)
        }
    }
}

/** Yalnızca cihazda saklanan tercihler (favoriler, yazı boyutu). Hiçbir veri dışarı gönderilmez. */
class Tercihler(ctx: Context) {
    private val sp = ctx.getSharedPreferences("tercihler", Context.MODE_PRIVATE)

    fun favoriler(): Set<String> = sp.getStringSet("favoriler", emptySet())!!.toSet()
    fun favoriDegistir(anahtar: String): Set<String> {
        val s = favoriler().toMutableSet()
        if (!s.add(anahtar)) s.remove(anahtar)
        sp.edit().putStringSet("favoriler", s).apply()
        return s
    }

    var yaziOlcegi: Float
        get() = sp.getFloat("yaziOlcegi", 1f)
        set(v) = sp.edit().putFloat("yaziOlcegi", v).apply()
}
