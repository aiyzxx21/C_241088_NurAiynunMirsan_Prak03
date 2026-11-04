
package com.example.prak3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: MahasiswaAdapter
    private val dataMahasiswa = arrayListOf<Mahasiswa>()

    private lateinit var etNim: EditText
    private lateinit var etNama: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etJurusan: EditText
    private lateinit var etNomorHp: EditText
    private lateinit var btnSimpan: Button
    private lateinit var btnBatal: Button
    private lateinit var listMahasiswa: ListView

    // null berarti tambah data; berisi ID berarti ubah data.
    private var editId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etNim = findViewById(R.id.etNim)
        etNama = findViewById(R.id.etNama)
        etAlamat = findViewById(R.id.etAlamat)
        etJurusan = findViewById(R.id.etJurusan)
        etNomorHp = findViewById(R.id.etNomorHp)
        btnSimpan = findViewById(R.id.btnSimpan)
        btnBatal = findViewById(R.id.btnBatal)
        listMahasiswa = findViewById(R.id.listMahasiswa)

        db = DatabaseHelper(this)
        adapter = MahasiswaAdapter(this, dataMahasiswa)
        listMahasiswa.adapter = adapter

        loadData()

        btnSimpan.setOnClickListener {
            simpan()
        }

        btnBatal.setOnClickListener {
            resetForm()
        }

        // Ketuk satu baris untuk mengubah data.
        listMahasiswa.setOnItemClickListener { _, _, position, _ ->
            isiForm(dataMahasiswa[position])
        }

        // Tekan lama satu baris untuk menghapus data.
        listMahasiswa.setOnItemLongClickListener { _, _, position, _ ->
            konfirmasiHapus(dataMahasiswa[position])
            true
        }
    }

    private fun loadData() {
        dataMahasiswa.clear()
        dataMahasiswa.addAll(db.getAllMahasiswa())
        adapter.notifyDataSetChanged()
    }

    private fun simpan() {
        val nim = etNim.text.toString().trim()
        val nama = etNama.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val jurusan = etJurusan.text.toString().trim()
        val nomorHp = etNomorHp.text.toString().trim()

        if (!validasi(nim, nama, nomorHp)) return

        val id = editId

        val mahasiswa = Mahasiswa(
            id = id ?: 0,
            nim = nim,
            nama = nama,
            alamat = alamat,
            jurusan = jurusan,
            nomorHp = nomorHp
        )

        val berhasil = if (id == null) {
            db.insertMahasiswa(mahasiswa) != -1L
        } else {
            db.updateMahasiswa(mahasiswa) > 0
        }

        if (berhasil) {
            val pesan = if (id == null) {
                "Data berhasil ditambahkan"
            } else {
                "Data berhasil diperbarui"
            }

            Toast.makeText(
                this,
                pesan,
                Toast.LENGTH_SHORT
            ).show()

            resetForm()
            loadData()
        } else {
            Toast.makeText(
                this,
                "Gagal menyimpan data",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun validasi(
        nim: String,
        nama: String,
        nomorHp: String
    ): Boolean {
        var valid = true

        if (nim.isEmpty()) {
            etNim.error = "NIM wajib diisi"
            valid = false
        } else if (nim.length < 5 || !nim.all { it.isDigit() }) {
            etNim.error = "NIM minimal 5 digit angka"
            valid = false
        } else if (db.isNimTerdaftar(nim, editId)) {
            etNim.error = "NIM sudah terdaftar"
            valid = false
        }

        if (nama.isEmpty()) {
            etNama.error = "Nama wajib diisi"
            valid = false
        }

        // Nomor HP boleh kosong. Jika diisi, harus valid.
        if (nomorHp.isNotEmpty() && !hpValid(nomorHp)) {
            etNomorHp.error =
                "Nomor HP tidak valid (contoh 081234567890)"
            valid = false
        }

        return valid
    }

    private fun hpValid(hp: String): Boolean {
        return hp.matches(Regex("^08\\d{8,11}$"))
    }

    private fun isiForm(mahasiswa: Mahasiswa) {
        editId = mahasiswa.id

        etNim.setText(mahasiswa.nim)
        etNama.setText(mahasiswa.nama)
        etAlamat.setText(mahasiswa.alamat)
        etJurusan.setText(mahasiswa.jurusan)
        etNomorHp.setText(mahasiswa.nomorHp)

        btnSimpan.text = "Perbarui"
        btnBatal.visibility = android.view.View.VISIBLE
    }

    private fun resetForm() {
        editId = null

        etNim.text.clear()
        etNama.text.clear()
        etAlamat.text.clear()
        etJurusan.text.clear()
        etNomorHp.text.clear()

        etNim.error = null
        etNama.error = null
        etAlamat.error = null
        etJurusan.error = null
        etNomorHp.error = null

        btnSimpan.text = "Simpan"
        btnBatal.visibility = android.view.View.GONE
    }

    private fun konfirmasiHapus(mahasiswa: Mahasiswa) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage(
                "Apakah kamu yakin ingin menghapus data " +
                        mahasiswa.nama + "?"
            )
            .setPositiveButton("Hapus") { _, _ ->
                val berhasil = db.deleteMahasiswa(mahasiswa.id) > 0

                if (berhasil) {
                    Toast.makeText(
                        this,
                        "Data berhasil dihapus",
                        Toast.LENGTH_SHORT
                    ).show()

                    if (editId == mahasiswa.id) {
                        resetForm()
                    }

                    loadData()
                } else {
                    Toast.makeText(
                        this,
                        "Gagal menghapus data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }
}
