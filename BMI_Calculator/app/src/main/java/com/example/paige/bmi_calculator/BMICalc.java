package com.example.paige.bmi_calculator;

import com.google.gson.Gson;

/**
 * Model - Generates a BMI for a given height in inches and weight in pounds
 */
public class BMICalc
{
    private double height, weight;

    public BMICalc ()
    {
        this.height = 0;
        this.weight = 0;
    }

    public BMICalc (double height, double weight)
    {
        this.weight = checkAndGetGreaterThanZero(weight, "Weight");
        this.height = checkAndGetGreaterThanZero(height, "Height");
    }

    public void setHeight (double height)
    {
        this.height = checkAndGetGreaterThanZero(height, "Height");
    }

    public void setWeight (double weight)
    {
       this.weight = checkAndGetGreaterThanZero(weight, "Weight");
    }

    private double checkAndGetGreaterThanZero (double value, String description)
    {
        if (value >0)
            return value;
        else
            throw new IllegalArgumentException (description + " must be greater than zero.");
    }

    public double getHeight ()
    {
        return height;
    }

    public double getWeight ()
    {
        return weight;
    }

    public double getBMI()
    {
        if (height > 0 && weight > 0) {
            return 703 * (weight / (height * height));
        }
        else
            throw new IllegalStateException ("Cannot get BMI before setting weight and height.");
    }

    public static BMICalc getObjectFromJSONString (String json)
    {
        Gson gson = new Gson ();
        return gson.fromJson (json, BMICalc.class);
    }

    public static String getJSONStringFromObject (BMICalc object)
    {
        Gson gson = new Gson ();
        return gson.toJson (object);
    }
}
