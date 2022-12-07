package com.example.discoverbolo.DiscoverBoloUtility;

public class Classification {
    private String name;
    private float confidence;

    public float getConfidence() {
        return confidence;
    }

    public String getName(){
        return name;
    }

    public Classification(String name, float confidence){
        this.confidence=confidence;
        this.name=name;
    }
}
