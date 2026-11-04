
package com.example.prak3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class MahasiswaAdapter(
    private val context: Context,
    private val daftarMahasiswa: ArrayList<Mahasiswa>
) : BaseAdapter() {

    override fun getCount(): Int = daftarMahasiswa.size

    override fun getItem(position: Int): Mahasiswa =
        daftarMahasiswa[position]

    override fun getItemId(position: Int): Long =
        daftarMahasiswa[position].id.toLong()

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_mahasiswa, parent, false)

        val mahasiswa = getItem(position)

        val tvNimNama = view.findViewById<TextView>(R.id.tvNimNama)
        val tvJurusan = view.findViewById<TextView>(R.id.tvJurusan)

        tvNimNama.text = "${mahasiswa.nim} - ${mahasiswa.nama}"
        tvJurusan.text = mahasiswa.jurusan

        return view
    }
}
