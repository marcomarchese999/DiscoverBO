package com.example.discoverbolo.DiscoverBoloUtility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Coordinates {

    private final int LONGITUDE=0, LATITUDE=1;
    private Context context;
    private Map<String, List<Double>> coords;
    private final String coordsFilePath="coordinates.txt";

    public Coordinates(Context cont){
        this.context=cont;
        this.coords=new HashMap<>();
        populateCoords();
    }

    public Double getLongFromMonument(String monument){
        Double c=this.coords.get(monument).get(LONGITUDE);
        return (c == null ? -1 : c);
    }

    public Double getLatFromMonument(String monument){
        Double c=this.coords.get(monument).get(LATITUDE);
        return (c == null ? -1 : c);
    }

    public void populateCoords(){
        try {
            AssetManager assetFiles = context.getAssets();
            InputStream is=assetFiles.open(coordsFilePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line="";
            String name="";
            Double longitude, latitude;

            while ((line = br.readLine()) != null) {
                name=line.split(";")[0].replaceAll("_"," ");
                try {

                    longitude = Double.parseDouble(line.split(";")[1]);
                    latitude = Double.parseDouble(line.split(";")[2]);

                } catch(  NumberFormatException e ){
                    e.printStackTrace();
                    return;
                }
                List<Double> cords=new ArrayList<>();
                cords.add(longitude);
                cords.add(latitude);
                coords.put(name, cords);
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("MONUMENTS LOADED:");
        for (String city : this.coords.keySet()){
            System.out.println(city+" "+this.coords.get(city).get(LONGITUDE)+" "+this.coords.get(city).get(LATITUDE));
        }
       return;
    }

    public AlertDialog myAlert(){
        AlertDialog alertDialog = new AlertDialog.Builder(this.context).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Coordinates error");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return alertDialog;
    }

}
