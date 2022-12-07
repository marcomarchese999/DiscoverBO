package com.example.discoverbolo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.example.discoverbolo.DiscoverBoloUtility.Classification;
import com.example.discoverbolo.ml.ModelBO;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.discoverbolo.databinding.ActivityMainBinding;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final Double MIN_SCORE=0.5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_voice, R.id.navigation_camera)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public String getMonumentDescription(String name){
        System.out.println(name);

        String result="Monument not found\n";

        StringBuilder text = new StringBuilder();

        try {
            InputStream is=getAssets().open(name.toLowerCase(Locale.ROOT));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
            System.out.println(text);
            return text.toString();
        } catch (IOException e) {
            Log.d("DEBUG", "IOException in monument description file opening");
            e.printStackTrace();

        }
        return result;
    }

    public Classification classifyImage(Bitmap image){
        String res="";

        try{
            ModelBO model = ModelBO.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorImage tensorImage = TensorImage.fromBitmap(image);
            // Runs model inference and gets result.
            ModelBO.Outputs outputs = model.process(tensorImage);
            List<Category> probability = outputs.getProbabilityAsCategoryList();

            int maxIndex=-1;
            float maxScore=0;

            for (int i=0; i<probability.size(); i++){
                if (probability.get(i).getScore()>maxScore){
                    maxScore=probability.get(i).getScore();
                    maxIndex=i;
                }
            }
            if (maxScore<MIN_SCORE){
                res="NONE";
                System.out.println("No good match");
            } else {
                res=probability.get(maxIndex).getLabel();
                System.out.println("best match: "+res);
            }
            // Releases model resources
            model.close();
            return new Classification(res, maxScore);

        } catch (IOException e){
            res="NONE";
            e.printStackTrace();
        }

        return new Classification(res, -1);
    }

}