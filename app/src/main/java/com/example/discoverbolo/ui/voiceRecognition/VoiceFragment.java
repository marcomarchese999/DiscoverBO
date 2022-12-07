package com.example.discoverbolo.ui.voiceRecognition;


import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


import com.example.discoverbolo.DiscoverBoloUtility.Coordinates;
import com.example.discoverbolo.MainActivity;
import com.example.discoverbolo.R;
import com.example.discoverbolo.databinding.FragmentRilevaVoceBinding;

import static android.content.ContentValues.TAG;

public class VoiceFragment extends Fragment{

    private RegistraViewModel registraViewModel;
    private SharedViewModel sharedViewModel;
    private FragmentRilevaVoceBinding binding;

    //public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private TextView textView; //?
    private TextView boxMonument;
    private ImageView micButton;
    private ImageView sendButton;
    private ImageView mapButton;
    private List<String> allowedWords = Arrays.asList( "basilica di san domenico","basilica di san francesco","basilica di santo stefano","basilica san petronio",
            "fontana del nettuno","le due torri","porta saragozza","santuario di san luca","finestrella","monumento a ugo bassi","palazzo re enzo","porta lame");

    private boolean attivo = false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        registraViewModel =
                new ViewModelProvider(this).get(RegistraViewModel.class);
        binding = FragmentRilevaVoceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedViewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);

        if(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }

        textView = binding.textSearch;
        micButton = binding.buttonVoiceSearch;
        sendButton=binding.buttonTextSearch;
        boxMonument = binding.boxMonument;
        mapButton = binding.mapButton;

        boxMonument.setMovementMethod(new ScrollingMovementMethod());

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.getContext(), 
            ComponentName.unflattenFromString("com.google.android.googlequicksearchbox/com.google.android.voicesearch.serviceapi.GoogleRecognitionService"));
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "it-IT");
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "it-IT");

        if(getArguments()!=null && !getArguments().isEmpty()){
            String name=getArguments().getString("name");
            textView.setText(name);
            setMonument(name);
        }

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Coordinates coord=new Coordinates(getContext());
                String monumentName=textView.getText().toString().toLowerCase(Locale.ROOT);

                double longitude=coord.getLongFromMonument(monumentName);
                double latitude=coord.getLatFromMonument(monumentName);

                if (longitude == -1 || latitude==-1){
                    coord.myAlert().show();
                    return;
                }

                System.out.println("REDIRECTION TO MAPS : "+ monumentName+" "+longitude);
                String uri = "http://maps.google.com/maps?daddr="+ longitude+","+latitude;
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse(uri));
                startActivity(viewIntent);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView!=null && textView.toString()!=""){
                    String name=textView.getText().toString().toLowerCase(Locale.ROOT);
                    setMonument(name);
                }
                else  Toast.makeText(getContext(),"Inserisci un monumento!",Toast.LENGTH_LONG).show();

            }
        });

        speechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle params) { Log.d(TAG, "onReadyForSpeech");}
            @Override
            public void onBeginningOfSpeech() {
                textView.setText("");
                textView.setHint("In ascolto...");
                Log.d(TAG, "onBeginningOfSpeech");
            }
            @Override
            public void onRmsChanged(float rmsdB) { Log.d(TAG, "onRmsChanged"); }
            @Override
            public void onBufferReceived(byte[] buffer) {
                Log.d(TAG, "onBufferReceived");
                Log.d(TAG, buffer.toString());
            }
            @Override
            public void onEndOfSpeech() {
                textView.setText(""); Log.d(TAG, "onEndOfSpeech"); }
            @Override
            public void onError(int error) {
                Log.d(TAG, "onError errorID: "+error);
                textView.setText("Errore, riprova: "+error);
            }

            @Override
            public void onResults(Bundle bundle) {
                micButton.setImageResource(R.drawable.ic_mic_black_off);
                textView.setHint("Search");
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                String word = "";
                for (int i=0; i<data.size();i++){
                    word=word+data.get(i).toLowerCase(Locale.ROOT);
                    if(i!=data.size()-1) word = word + " ";
                }
                textView.setText(word);
                if (allowedWords.contains(word)) {
                    setMonument(word);
                }
                else {
                    Toast.makeText(getContext(), "La parola/frase " + word + " non è valida!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.d(TAG, "onPartialResults");
            }
            @Override
            public void onEvent(int eventType, Bundle params) {
                Log.d(TAG, "onEvent");
            }
        });

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (attivo==true){
                    attivo=false;
                    micButton.setImageResource(R.drawable.ic_mic_black_off);
                    speechRecognizer.stopListening();
                }else if (attivo==false){
                    attivo=true;
                    micButton.setImageResource(R.drawable.ic_mic_red);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        speechRecognizer.destroy();
    }


    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.println(Log.WARN,"ok","Bravo");
                } else {
                    Log.println(Log.WARN,"Peccato","Qualcosa è andato storto");
                }
            });

    public void setMonument(String word){
        System.out.println(word);
        if(allowedWords.contains(word.toLowerCase())){
            String mon = word.replace(" ", "_");

            String monument = ((MainActivity) requireActivity()).getMonumentDescription(mon);
            System.out.println("OK MONUMENT");
            mapButton.setVisibility(View.VISIBLE);
            boxMonument.setText(monument);
        } else{
            Toast.makeText(getContext(), "error!", Toast.LENGTH_LONG).show();
        }
    }
}