package com.bayu.fajar.wisataku.api;


public class Server {
    //IP Localhost
    public static final String URL = "http://192.168.0.11/wisata/";
    public static final String URLU = "http://192.168.0.11/wisata/user/";
    public static final String URLA = "http://192.168.0.11/wisata/admin/";

    public static final String showProfil = URLU + "get_profile.php?id_user=";
    public static final String showProfilA = URLA + "get_profile.php?id_user=";

//    public static final String URL = "http://wisatajawabarat.online/";
//    public static final String URLU = "http://wisatajawabarat.online/user/";
//    public static final String URLA = "http://wisatajawabarat.online/admin/";
//
//    public static final String showProfil = URLU + "get_profile.php?id_user=";
//    public static final String showProfilA = URLA + "get_profile.php?id_user=";

}