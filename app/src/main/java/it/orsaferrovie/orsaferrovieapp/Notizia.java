package it.orsaferrovie.orsaferrovieapp;


import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Created by Fabrizio on 27-mag-15.
 */
public class Notizia implements Serializable{
    private String _data;
    private String _testo;
    private int id;
    private URL _address;
    //public static DateTimeFormatter DATA_NOTIZIA = DateTimeFormat.forPattern("d MMMM yyyy");


    private String _immagine;

    public String get_data() {
        return _data;
    }

    public String get_testo() {
        return _testo;
    }

    public URL get_address() {
        return _address;
    }

    public String get_immagine() {
        return _immagine;
    }

    public Notizia (int id, String data, String testo, String URL, String image) {
        this(data, testo, URL, image);
        this.id = id;
    }
    public Notizia (String data, String testo, String URL, String image)  {
        id = -1;
        _data = data;
        _testo = testo;
        try {
            _address = new URL(URL);
        } catch (MalformedURLException ex) {
            _address = null;
        }
        _immagine = image;
    }
}
