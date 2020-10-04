package it.orsaferrovie.orsaferrovieapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScriviciFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScriviciFragment extends Fragment {



    public static ScriviciFragment newInstance() {
        ScriviciFragment fragment = new ScriviciFragment();
        return fragment;
    }

    public ScriviciFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup me = (ViewGroup) inflater.inflate(R.layout.fragment_scrivici, container, false);

        return me;
    }



}
