package com.bayu.fajar.wisataku.Server;

public class ModelData {

    String id;
    String nama;
    String lng;
    String lat;
    String date;
    String time;
    String deskripsi;
    String harga;
    String id_user;

    public ModelData(){}

    public ModelData(String id, String nama, String lng, String lat, String date, String time, String deskripsi, String harga, String id_user) {
        this.id = id;
        this.nama = nama;
        this.lng = lng;
        this.lat = lat;
        this.date = date;
        this.time = time;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.id_user = id_user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }
}
