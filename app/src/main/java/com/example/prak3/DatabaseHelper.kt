
package com.example.prak3

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    // Membuat tabel ketika database pertama kali dibuat
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_MAHASISWA (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nim TEXT NOT NULL UNIQUE,
                nama TEXT NOT NULL,
                alamat TEXT,
                jurusan TEXT,
                nomor_hp TEXT
            )
        """.trimIndent()

        db.execSQL(createTable)

        // Memasukkan data awal hanya saat database pertama dibuat
        seedData(db)
    }

    // Memperbarui database tanpa menghapus data lama
    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Jangan menggunakan DROP TABLE karena akan menghapus seluruh data.
        // Tambahkan proses migrasi sesuai perubahan struktur tabel
        // jika suatu saat versi database dinaikkan.
    }

    // Data awal mahasiswa
    private fun seedData(db: SQLiteDatabase) {
        val seed = listOf(
            Mahasiswa(
                nim = "21001",
                nama = "Ahmad Fauzi",
                alamat = "Jl. Pettarani No. 5",
                jurusan = "Sistem Informasi",
                nomorHp = "081234567890"
            ),
            Mahasiswa(
                nim = "21002",
                nama = "Siti Nurhaliza",
                alamat = "Jl. Perintis Kemerdekaan",
                jurusan = "Sistem Informasi",
                nomorHp = "082198765432"
            ),
            Mahasiswa(
                nim = "21003",
                nama = "Budi Santoso",
                alamat = "Jl. Rappocini No. 12",
                jurusan = "Sistem Informasi",
                nomorHp = "081345678901"
            )
        )

        seed.forEach {
            db.insert(TABLE_MAHASISWA, null, toValues(it))
        }
    }

    // Mengubah data mahasiswa menjadi ContentValues
    private fun toValues(mhs: Mahasiswa): ContentValues {
        return ContentValues().apply {
            put("nim", mhs.nim)
            put("nama", mhs.nama)
            put("alamat", mhs.alamat)
            put("jurusan", mhs.jurusan)
            put("nomor_hp", mhs.nomorHp)
        }
    }

    // CREATE: Menambahkan mahasiswa
    fun insertMahasiswa(mhs: Mahasiswa): Long {
        return writableDatabase.insert(
            TABLE_MAHASISWA,
            null,
            toValues(mhs)
        )
    }

    // UPDATE: Memperbarui mahasiswa berdasarkan ID
    fun updateMahasiswa(mhs: Mahasiswa): Int {
        return writableDatabase.update(
            TABLE_MAHASISWA,
            toValues(mhs),
            "id = ?",
            arrayOf(mhs.id.toString())
        )
    }

    // DELETE: Menghapus mahasiswa berdasarkan ID
    fun deleteMahasiswa(id: Int): Int {
        return writableDatabase.delete(
            TABLE_MAHASISWA,
            "id = ?",
            arrayOf(id.toString())
        )
    }

    // Mengecek NIM yang sudah terdaftar
    fun isNimTerdaftar(
        nim: String,
        exceptId: Int? = null
    ): Boolean {
        val cursor = readableDatabase.query(
            TABLE_MAHASISWA,
            arrayOf("id"),
            if (exceptId == null) {
                "nim = ?"
            } else {
                "nim = ? AND id != ?"
            },
            if (exceptId == null) {
                arrayOf(nim)
            } else {
                arrayOf(nim, exceptId.toString())
            },
            null,
            null,
            null
        )

        return cursor.use {
            it.moveToFirst()
        }
    }

    // READ: Mengambil seluruh data mahasiswa
    fun getAllMahasiswa(): List<Mahasiswa> {
        val list = mutableListOf<Mahasiswa>()

        val cursor = readableDatabase.query(
            TABLE_MAHASISWA,
            null,
            null,
            null,
            null,
            null,
            "nama ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Mahasiswa(
                        id = it.getInt(
                            it.getColumnIndexOrThrow("id")
                        ),
                        nim = it.getString(
                            it.getColumnIndexOrThrow("nim")
                        ),
                        nama = it.getString(
                            it.getColumnIndexOrThrow("nama")
                        ),
                        alamat = it.getString(
                            it.getColumnIndexOrThrow("alamat")
                        ),
                        jurusan = it.getString(
                            it.getColumnIndexOrThrow("jurusan")
                        ),
                        nomorHp = it.getString(
                            it.getColumnIndexOrThrow("nomor_hp")
                        )
                    )
                )
            }
        }

        return list
    }

    companion object {
        private const val DB_NAME = "praktikum.db"
        private const val DB_VERSION = 1
        private const val TABLE_MAHASISWA = "mahasiswa"
    }
}
