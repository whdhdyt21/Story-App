# Story App

Aplikasi Android untuk berbagi cerita dan pengalaman secara publik. Dibuat sebagai proyek submission di Dicoding.

## Fitur

- 📖 Membuat dan membagikan cerita dengan gambar
- 📍 Integrasi lokasi (GPS) untuk tagging lokasi
- 🔐 Sistem autentikasi pengguna (Login & Register)
- 🌐 Upload gambar ke server
- 🗺️ Tampilkan cerita dalam bentuk peta
- 📂 Paging 3 untuk daftar cerita

## Instalasi

1. Clone repository ini
```bash
git clone https://github.com/whdhdyt21/Story-App.git
```
2. Buka project di Android Studio
3. Tambahkan konfigurasi di file `local.properties`:
```properties
BASE_URL = "https://story-api.dicoding.dev/"
```
4. Build dan jalankan aplikasi

## Konfigurasi Wajib

Tambahkan konfigurasi berikut di `local.properties`:
```properties
BASE_URL = "https://story-api.dicoding.dev/"
MAPS_API_KEY = "your_maps_api_key_here"  # Jika menggunakan Google Maps
```

## Teknologi Yang Digunakan

- ✅ Kotlin
- ✅ MVVM Architecture
- ✅ Retrofit & OkHttp3
- ✅ Room Database
- ✅ CameraX
- ✅ Paging 3
- ✅ DataStore Preferences
- ✅ Glide
- ✅ Google Maps (Opsional)
- ✅ Unit Testing & Mockito
- ✅ Navigation Component

## Cara Penggunaan

1. Register akun baru atau login dengan akun yang sudah ada
2. Tekan tombol "+" untuk membuat cerita baru
3. Ambil foto atau pilih dari galeri
4. Tambahkan deskripsi dan lokasi (opsional)
5. Posting cerita dan lihat di halaman beranda

## Kontribusi

Silakan buka issue terlebih dahulu sebelum membuat pull request. Pastikan untuk mengikuti gaya koding yang sudah digunakan.

Dikembangkan dengan ❤️ oleh whdhdyt21
