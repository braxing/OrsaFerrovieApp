package it.orsaferrovie.orsaferrovieapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialFragment extends Fragment {

    private ImageButton imgTwitter, imgFacebook;

    public static SocialFragment newInstance() {
        SocialFragment fragment = new SocialFragment();
        return fragment;
    }

    public SocialFragment() {
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
        ViewGroup me = (ViewGroup) inflater.inflate(R.layout.fragment_social, container, false);
        imgFacebook = (ImageButton) me.findViewById(R.id.imgFacebook);
        imgTwitter = (ImageButton) me.findViewById(R.id.imgTwitter);
        imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apriPaginaFacebook();
            }
        });
        imgTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apriPaginaTwitter();
            }
        });
        return me;
    }

    private void apriPaginaFacebook() {
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.facebook_page)));
        startActivity(facebookIntent);
    }

    private void apriPaginaTwitter() {
        Intent twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.twitter_page)));
        startActivity(twitterIntent);
    }


}
