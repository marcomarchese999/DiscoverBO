package com.example.discoverbolo.ui.imageClassification;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.discoverbolo.DiscoverBoloUtility.Classification;
import com.example.discoverbolo.MainActivity;
import com.example.discoverbolo.R;
import com.example.discoverbolo.databinding.FragmentPhotoTakeBinding;

public class ImageCaptureFragment extends Fragment {
    private FragmentPhotoTakeBinding binding;
    ImageView imageView;
    Button pictureButton;
    TextView result;
    TextView confidence;
    TextView classified;
    ImageView goto_info_button;
    String[] classes;
    int imageSize = 224;

    public ImageCaptureFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,Bundle savedInstanceState) {
        super.onCreate(null);

        binding = FragmentPhotoTakeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //classified = binding.classified;

        result = binding.result;
        imageView = binding.imageView;
        
        goto_info_button = binding.gotoInfoButton;
        confidence = binding.confidenceTextView;
        
        pictureButton = binding.takePictureButton;
        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    //Request camera permission if we don't have it.
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
        
        goto_info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(result.getText()==null || result.getText().toString().length()==0){
                    Toast.makeText(getContext(),"You need to take a picture of the monument!",Toast.LENGTH_LONG).show();
                }
                else if(result.getText()=="NONE") Toast.makeText(getContext(),"Can't recognise the monument!",Toast.LENGTH_LONG).show();
                else{
                    Bundle bundle= new Bundle();
                    bundle.putString("name",result.getText().toString());
                    Navigation.findNavController(view).navigate(R.id.action_navigation_image_search_to_navigation_voice_search,bundle);
                }
            }
        });
        return root;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String res="";
        if (requestCode == 1 && resultCode == -1) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);
            goto_info_button.setVisibility(View.GONE);
            confidence.setVisibility(View.GONE);
            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            Classification c=((MainActivity)requireActivity()).classifyImage(image);
            if(c.getName()=="NONE") {
                result.setText("Unknown");
                System.out.println(c.getConfidence());
            }
            else {
                String resultString = c.getName().replace("_", " ");
                confidence.setVisibility(View.VISIBLE);
                goto_info_button.setVisibility(View.VISIBLE);
                result.setText(resultString);
                confidence.setText("Confidence: "+String.format("%.2f", c.getConfidence()*100)+"%");
            }
        }

    }


}