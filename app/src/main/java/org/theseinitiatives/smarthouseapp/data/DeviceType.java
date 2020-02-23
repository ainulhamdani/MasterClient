package org.theseinitiatives.smarthouseapp.data;

import android.content.Context;
import android.graphics.drawable.Drawable;

import org.theseinitiatives.smarthouseapp.R;

public class DeviceType {
    public static final String LAMP = "lamp";
    public static final String LAMP_ON = "lamp_on";
    public static final String LAMP_OFF = "lamp_off";
    public static final String TV = "tv";
    public static final String DISHWASHER = "diswasher";
    public static final String COFFEE_MACHINE = "coffee_machine";
    public static final String HEATER = "heater";
    public static final String MICROWAVE = "microwave";
    public static final String REFRIGERATOR = "refrigerator";
    public static final String WASHING_MACHINE = "washing_machine";

    public static Drawable getDeviceDrawable(Context context, String type){
        switch (type) {
            case LAMP:
                return context.getResources().getDrawable(R.drawable.lamp);
            case LAMP_ON:
                return context.getResources().getDrawable(R.drawable.lamp_on);
            case LAMP_OFF:
                return context.getResources().getDrawable(R.drawable.lamp_off);
            case TV:
                return context.getResources().getDrawable(R.drawable.television);
            case DISHWASHER:
                return context.getResources().getDrawable(R.drawable.dishwasher);
            case COFFEE_MACHINE:
                return context.getResources().getDrawable(R.drawable.coffee_machine);
            case HEATER:
                return context.getResources().getDrawable(R.drawable.heater);
            case MICROWAVE:
                return context.getResources().getDrawable(R.drawable.microwave);
            case REFRIGERATOR:
                return context.getResources().getDrawable(R.drawable.refrigerator);
            case WASHING_MACHINE:
                return context.getResources().getDrawable(R.drawable.washing_machine);
            default: return  null;
        }
    }
}
